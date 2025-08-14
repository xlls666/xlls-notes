import json
from datetime import datetime

import numpy as np
from langchain_community.embeddings import DashScopeEmbeddings
from langchain_core.documents import Document
from langchain_redis import RedisConfig
from openai import BaseModel
from redis import Redis
from redis.connection import ConnectionPool

from config.logging_config import LoggerFactory


class Notes(BaseModel):
    id: int
    notesUserId: int
    tag: str
    title: str
    source: str
    content: str
    createTime: str
    updateTime: str


class Response(BaseModel):
    code: int
    message: str
    data: object = None
    success: bool

class NotesStorageService:
    """
    笔记存储服务类，负责处理笔记的向量化和存储到Redis的操作
    """

    def __init__(self, embedding_model: DashScopeEmbeddings, redis_config: RedisConfig, redis_pool: ConnectionPool):
        self.embedding_model = embedding_model
        self.redis_config = redis_config
        # 使用连接池创建Redis客户端
        self.redis_pool = redis_pool
        self.redis_client = Redis(connection_pool=self.redis_pool)
        self.logger = LoggerFactory.get_logger("store_service")
        
        # 确保索引包含所有必要的字段
        self._ensure_index_exists()

    def _ensure_index_exists(self):
        """
        确保存储笔记的索引存在，如果不存在则创建
        """
        try:
            # 检查索引是否已存在
            self.redis_client.ft(self.redis_config.index_name).info()
            self.logger.info(f"索引 {self.redis_config.index_name} 已存在")
        except:
            # 索引不存在，创建新索引
            self.logger.info(f"索引 {self.redis_config.index_name} 不存在，正在创建...")
            try:
                # 定义索引模式，包含所有需要的字段
                schema = [
                    "text", "TEXT",  # 文档内容
                    "embedding", "VECTOR", "FLAT", "6", "TYPE", "FLOAT32", "DIM", "1536", "DISTANCE_METRIC", "COSINE",  # 向量字段
                    "user_id", "TAG", "SEPARATOR", ",",  # 用户ID，TAG类型支持过滤
                    "create_time", "NUMERIC",  # 创建时间，NUMERIC类型支持范围查询
                    "update_time", "NUMERIC",  # 更新时间，NUMERIC类型支持范围查询
                    "tag", "TEXT",     # 标签
                    "title", "TEXT",   # 标题
                    "source", "TEXT"   # 来源
                ]

                # 创建索引
                self.redis_client.ft(self.redis_config.index_name).create_index(
                    schema,
                    definition={"type": "HASH", "prefix": f"{self.redis_config.index_name}:"}
                )
                self.logger.info(f"索引 {self.redis_config.index_name} 创建成功")
            except Exception as e:
                self.logger.error(f"创建索引时出错: {e}")

    def _datetime_to_timestamp(self, datetime_str: str) -> float:
        """
        将日期时间字符串转换为Unix时间戳
        """
        try:
            # 尝试解析常见的日期时间格式
            dt = datetime.fromisoformat(datetime_str.replace('Z', '+00:00'))
            return dt.timestamp()
        except ValueError:
            # 如果解析失败，尝试其他常见格式
            try:
                dt = datetime.strptime(datetime_str, '%Y-%m-%d %H:%M:%S')
                return dt.timestamp()
            except ValueError:
                # 如果所有格式都失败，返回0
                self.logger.warning(f"无法解析日期时间字符串: {datetime_str}，使用默认值0")
                return 0

    def store_note_to_redis(self, note):
        """
        将单个笔记向量化并存储到Redis中
        如果已存在相同ID的数据则更新，否则新增
        """
        try:
            # 构造文档对象
            doc = Document(
                page_content=note.content,
                metadata={
                    "id": str(note.id),
                    "tag": note.tag,
                    "title": note.title,
                    "source": note.source,
                    "create_time": note.createTime,
                    "update_time": note.updateTime,
                    "user_id": str(note.notesUserId)
                }
            )

            # 生成向量
            embedding = self.embedding_model.embed_query(doc.page_content)
            embedding_bytes = np.array(embedding, dtype=np.float32).tobytes()

            # 将时间字符串转换为时间戳
            create_time_timestamp = self._datetime_to_timestamp(note.createTime)
            update_time_timestamp = self._datetime_to_timestamp(note.updateTime)

            # 检查是否已存在相同ID的数据
            self.logger.info(f"正在处理笔记: {note.id}")
            self.logger.info(f"index_name: {self.redis_config.index_name}")
            
            # 先尝试查找ID完全匹配的笔记
            existing_key = self.find_note_by_id(self.redis_client, self.redis_config.index_name, str(note.id))
            self.logger.info(f"existing_key: {existing_key}")
            
            if existing_key:
                # 更新现有数据
                fields = {
                    'text': doc.page_content,
                    'embedding': embedding_bytes,
                    'user_id': doc.metadata['user_id'],
                    'create_time': create_time_timestamp,
                    'update_time': update_time_timestamp,
                    'tag': doc.metadata['tag'],
                    'title': doc.metadata['title'],
                    'source': doc.metadata['source'],
                    '_metadata_json': json.dumps(doc.metadata)
                }
                self.redis_client.hset(existing_key, mapping=fields)
                self.logger.info(f"已更新ID为 {note.id} 的笔记")
            else:
                # 新增数据
                # 使用note.id生成键名
                new_key = self.generate_new_key( self.redis_config.index_name, note.id)
                fields = {
                    'text': doc.page_content,
                    'embedding': embedding_bytes,
                    'user_id': doc.metadata['user_id'],
                    'create_time': create_time_timestamp,
                    'update_time': update_time_timestamp,
                    'tag': doc.metadata['tag'],
                    'title': doc.metadata['title'],
                    'source': doc.metadata['source'],
                    '_metadata_json': json.dumps(doc.metadata)
                }
                self.redis_client.hset(new_key, mapping=fields)
                self.logger.info(f"已新增ID为 {note.id} 的笔记")
        except Exception as e:
            self.logger.error(f"存储笔记时出错: {e}", exc_info=True)
            raise

    def find_note_by_id(self, redis_client, index_name, note_id):
        """
        根据笔记ID查找Redis中是否存在相同ID的数据
        """
        try:
            # 直接构造键名
            key = f"{index_name}:{note_id}"
            # 检查键是否存在
            if redis_client.exists(key):
                return key
            return None
        except Exception as e:
            self.logger.error(f"查询Redis时出错: {e}", exc_info=True)
            return None

    def generate_new_key(self, index_name, note_id):
        """
        生成新的Redis键名，使用index_name和note_id组合
        """
        return f"{index_name}:{note_id}"

    def close(self):
        """
        关闭Redis连接池
        """
        if self.redis_pool:
            self.redis_pool.disconnect()
            self.logger.info("Redis连接池已关闭")