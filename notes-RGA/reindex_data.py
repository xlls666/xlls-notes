import os
from datetime import datetime

from langchain_community.embeddings import DashScopeEmbeddings
from langchain_community.utilities import SQLDatabase
from langchain_core.documents import Document
from langchain_redis import RedisConfig
from redis import Redis
from sqlalchemy import text
import numpy as np
from config.load_key import load_key

"""
重新导入数据并创建包含user_id、create_time和update_time字段的索引
"""

# 设置环境变量
os.environ['DASHSCOPE_API_KEY'] = load_key('DASHSCOPE_API_KEY')
embedding_model = DashScopeEmbeddings(model='text-embedding-v1')

# Redis配置
redis_config = RedisConfig(
    index_name='notes-index',
    redis_url=f'redis://:{load_key("REDIS_PASSWORD")}@14.103.179.26:6379'
)

# 连接 MySQL 数据库
db = SQLDatabase.from_uri("mysql+pymysql://root:lmmqxyx666@14.103.179.26:3306/xlls-notes")

# 查询数据
query = text("SELECT * from personal_notes")
with db._engine.connect() as conn:
    result = conn.execute(query).mappings()
    rows = result.fetchall()
print("查询结果：", rows)

def datetime_to_timestamp(datetime_str: str) -> float:
    """
    将日期时间字符串转换为Unix时间戳
    """
    try:
        # 尝试解析常见的日期时间格式
        if isinstance(datetime_str, str):
            dt = datetime.fromisoformat(datetime_str.replace('Z', '+00:00'))
        else:
            dt = datetime_str
        return dt.timestamp()
    except ValueError:
        # 如果解析失败，尝试其他常见格式
        try:
            dt = datetime.strptime(str(datetime_str), '%Y-%m-%d %H:%M:%S')
            return dt.timestamp()
        except ValueError:
            # 如果所有格式都失败，返回0
            print(f"无法解析日期时间字符串: {datetime_str}，使用默认值0")
            return 0

# 转换为 Document 对象
documents = []
for row in rows:
    doc = Document(
        page_content=row['content'],
        metadata={
            "id": str(row['id']),
            "tag": row['tag'],
            "title": row['title'],
            "source": row['source'],
            "create_time": str(row['create_time']),
            "update_time": str(row['update_time']),
            "user_id": str(row['notes_user_id'])  # 确保user_id是字符串类型
        }
    )
    documents.append(doc)

# 直接使用Redis客户端连接Redis
redis_client = Redis(
    host='14.103.179.26',
    port=6379,
    password=load_key("REDIS_PASSWORD"),
    decode_responses=False  # 保持字节格式，向量数据需要
)

# 删除现有索引（如果存在）
try:
    redis_client.execute_command('FT.DROPINDEX', redis_config.index_name)
    print("已删除旧索引")
except Exception as e:
    print(f"删除旧索引时出错（可能索引不存在）: {e}")

# 创建新索引
try:
    # 定义索引模式
    index_args = [
        redis_config.index_name,
        'ON', 'HASH',
        'PREFIX', '1', f'{redis_config.index_name}:',
        'SCHEMA',
        'text', 'TEXT',  # 文档内容
        'embedding', 'VECTOR', 'FLAT', '6', 'TYPE', 'FLOAT32', 'DIM', '1536', 'DISTANCE_METRIC', 'COSINE',  # 向量字段
        'user_id', 'TAG', 'SEPARATOR', ',',  # 用户ID，TAG类型支持过滤
        'create_time', 'NUMERIC',  # 创建时间，NUMERIC类型支持范围查询
        'update_time', 'NUMERIC',  # 更新时间，NUMERIC类型支持范围查询
        'tag', 'TEXT',     # 标签
        'title', 'TEXT',   # 标题
        'source', 'TEXT'   # 来源
    ]
    
    redis_client.execute_command('FT.CREATE', *index_args)
    print("Redis索引创建成功")
except Exception as e:
    print(f"创建Redis索引时出错: {e}")

# 生成向量并存储数据
for i, doc in enumerate(documents):
    # 生成向量
    embedding = embedding_model.embed_query(doc.page_content)

    # 将列表转换为numpy数组，再转换为字节
    embedding_bytes = np.array(embedding, dtype=np.float32).tobytes()

    # 将时间字符串转换为时间戳
    create_time_timestamp = datetime_to_timestamp(doc.metadata['create_time'])
    update_time_timestamp = datetime_to_timestamp(doc.metadata['update_time'])

    # 构造存储的键名
    key = f"{redis_config.index_name}:{doc.metadata['id']}"

    # 构造存储的字段值
    fields = {
        'text': doc.page_content,
        'embedding': embedding_bytes,  # 将向量转换为字节存储
        'user_id': doc.metadata['user_id'],
        'create_time': create_time_timestamp,
        'update_time': update_time_timestamp,
        'tag': doc.metadata['tag'],
        'title': doc.metadata['title'],
        'source': doc.metadata['source'],
        '_metadata_json': str(doc.metadata)  # 保存完整的元数据
    }

    # 存储到Redis
    redis_client.hset(key, mapping=fields)

print(f"已成功存储 {len(documents)} 个文档到Redis")

# 验证存储结果
try:
    info = redis_client.execute_command('FT.INFO', redis_config.index_name)
    print("索引信息:")
    # 解析并打印关键信息
    for i in range(0, len(info), 2):
        if i+1 < len(info):
            key = info[i].decode('utf-8') if isinstance(info[i], bytes) else info[i]
            value = info[i+1]
            if key == 'attributes':
                print(f"  {key}:")
                for attr in value:
                    if isinstance(attr, list):
                        attr_str = [item.decode('utf-8') if isinstance(item, bytes) else item for item in attr]
                        print(f"    {attr_str}")
            else:
                value_str = value.decode('utf-8') if isinstance(value, bytes) else value
                print(f"  {key}: {value_str}")
except Exception as e:
    print(f"获取索引信息时出错: {e}")

redis_client.close()