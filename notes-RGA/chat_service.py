from typing import List, Optional, AsyncGenerator
from datetime import datetime

from langchain_community.embeddings import DashScopeEmbeddings
from langchain_core.documents import Document
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_deepseek import ChatDeepSeek
from langchain_redis import RedisConfig, RedisVectorStore
from pydantic import BaseModel

from config.logging_config import LoggerFactory


# 定义聊天接口的输入输出模型
class ChatInput(BaseModel):
    message: str
    userId: Optional[str] = None
    createTimeStart: Optional[str] = None
    createTimeEnd: Optional[str] = None
    updateTimeStart: Optional[str] = None
    updateTimeEnd: Optional[str] = None
    pageSize: Optional[int] = 10


class ChatOutput(BaseModel):
    code: int = 200
    message: str = "success"
    data: str
    success: bool = True
    
    @classmethod
    def ok(cls, data: str):
        return cls(data=data)
    
    @classmethod
    def error(cls, message: str = "error", code: int = 500):
        return cls(code=code, message=message, data="", success=False)


class NotesChatService:
    def __init__(self, embedding_model: DashScopeEmbeddings, redis_config: RedisConfig, llm: ChatDeepSeek):
        # 初始化聊天相关组件
        self.vector_store = RedisVectorStore(embeddings=embedding_model, config=redis_config)
        self.llm = llm
        self.logger = LoggerFactory.get_logger("chat_service")

        # 创建基础chain组件
        self.prompt_template = ChatPromptTemplate.from_messages([
            ("user", """你是一个答疑机器人，你的任务是根据下述给定的已知信息回答用户的问题。
            已知信息：{context}
            用户问题：{question}
            如果已知信息不包含用户问题的答案，或者已知信息不足以回答用户的问题，出无法回答的原因。
            无论用户提出怎样的问题，回答不可超过1000字
            请用中文回答用户问题。""")
                    ])

    def collect_documents(self, segments):
        """收集本地向量化搜索结果"""
        text = []
        for relative_segment in segments:
            note = {
                "笔记正文内容": relative_segment.page_content,
                "笔记标签": relative_segment.metadata.get("tag"),
                "笔记标题": relative_segment.metadata.get("title"),
                "笔记来源": relative_segment.metadata.get("source"),
                "笔记创建时间": relative_segment.metadata.get("create_time"),
                "笔记更新时间": relative_segment.metadata.get("update_time"),
            }
            text.append(note)
        return text

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
                # 如果所有格式都失败，抛出异常
                raise ValueError(f"无法解析日期时间字符串: {datetime_str}")

    async def chat_with_rag_stream(self, input_data: ChatInput) -> AsyncGenerator[str, None]:
        """
        使用RAG技术进行聊天，支持流式响应
        """
        try:
            # 构建过滤条件
            search_kwargs = {"k": input_data.pageSize}
            
            # 构建过滤表达式
            filter_expressions = []
            
            # user_id过滤
            if input_data.userId:
                filter_expressions.append(f"@user_id:{{{str(input_data.userId).strip()}}}")
            
            # create_time范围过滤（使用NUMERIC字段的范围查询语法）
            if input_data.createTimeStart and input_data.createTimeEnd:
                start_timestamp = self._datetime_to_timestamp(input_data.createTimeStart)
                end_timestamp = self._datetime_to_timestamp(input_data.createTimeEnd)
                filter_expressions.append(f"@create_time:[{start_timestamp} {end_timestamp}]")
            elif input_data.createTimeStart:
                start_timestamp = self._datetime_to_timestamp(input_data.createTimeStart)
                filter_expressions.append(f"@create_time:[{start_timestamp} inf]")
            elif input_data.createTimeEnd:
                end_timestamp = self._datetime_to_timestamp(input_data.createTimeEnd)
                filter_expressions.append(f"@create_time:[-inf {end_timestamp}]")
                
            # update_time范围过滤（使用NUMERIC字段的范围查询语法）
            if input_data.updateTimeStart and input_data.updateTimeEnd:
                start_timestamp = self._datetime_to_timestamp(input_data.updateTimeStart)
                end_timestamp = self._datetime_to_timestamp(input_data.updateTimeEnd)
                filter_expressions.append(f"@update_time:[{start_timestamp} {end_timestamp}]")
            elif input_data.updateTimeStart:
                start_timestamp = self._datetime_to_timestamp(input_data.updateTimeStart)
                filter_expressions.append(f"@update_time:[{start_timestamp} inf]")
            elif input_data.updateTimeEnd:
                end_timestamp = self._datetime_to_timestamp(input_data.updateTimeEnd)
                filter_expressions.append(f"@update_time:[-inf {end_timestamp}]")
            
            # 如果有过滤条件，则添加到search_kwargs中
            if filter_expressions:
                # 使用括号将所有条件组合在一起
                if len(filter_expressions) > 1:
                    search_kwargs["filter"] = "(" + " ".join(filter_expressions) + ")"
                else:
                    search_kwargs["filter"] = filter_expressions[0]
                self.logger.info(f"使用的过滤表达式: {search_kwargs['filter']}")

            # 创建retriever
            retriever = self.vector_store.as_retriever(search_kwargs=search_kwargs)

            # 获取相关文档
            docs: List[Document] = retriever.invoke(input_data.message)
            
            # 添加调试日志，输出查询到的文档数量和具体内容
            self.logger.info(f"查询到的相关文档数量: {len(docs)}")
            for doc in docs:
                self.logger.info(f"相关文档内容: {doc.page_content}")

            # 如果没有找到相关文档，直接返回提示信息
            if not docs:
                self.logger.info("未找到相关文档")
                yield "data: " + ChatOutput.ok("我没有找到相关的笔记内容。请尝试调整查询条件或检查是否有对应用户的数据。").json() + "\n\n"
                return

            # 如果有相关文档，使用已获取的文档直接构建上下文，避免重复查询
            context_texts = self.collect_documents(docs)

            # 使用已有的上下文直接调用大模型
            chain = self.prompt_template | self.llm | StrOutputParser()
            
            # 流式生成响应
            async for chunk in chain.astream({"context": context_texts, "question": input_data.message}):
                if chunk:
                    # yield f"data: {chunk}\n\n"
                    yield chunk

            # 发送结束标记
            # yield "data: [DONE]\n\n"
            yield "\n\n"

        except Exception as e:
            self.logger.error(f"处理聊天请求时出错: {e}", exc_info=True)
            error_response = ChatOutput.error(f"处理请求时出错: {str(e)}").json()
            yield f"data: {error_response}\n\n"

    def chat_with_rag(self, input_data: ChatInput) -> ChatOutput:
        """
        使用RAG技术进行聊天
        """
        # 构建过滤条件
        search_kwargs = {"k": input_data.pageSize}
        
        # 构建过滤表达式
        filter_expressions = []
        
        # user_id过滤
        if input_data.userId:
            filter_expressions.append(f"@user_id:{{{str(input_data.userId).strip()}}}")
        
        # create_time范围过滤（使用NUMERIC字段的范围查询语法）
        if input_data.createTimeStart and input_data.createTimeEnd:
            start_timestamp = self._datetime_to_timestamp(input_data.createTimeStart)
            end_timestamp = self._datetime_to_timestamp(input_data.createTimeEnd)
            filter_expressions.append(f"@create_time:[{start_timestamp} {end_timestamp}]")
        elif input_data.createTimeStart:
            start_timestamp = self._datetime_to_timestamp(input_data.createTimeStart)
            filter_expressions.append(f"@create_time:[{start_timestamp} inf]")
        elif input_data.createTimeEnd:
            end_timestamp = self._datetime_to_timestamp(input_data.createTimeEnd)
            filter_expressions.append(f"@create_time:[-inf {end_timestamp}]")
            
        # update_time范围过滤（使用NUMERIC字段的范围查询语法）
        if input_data.updateTimeStart and input_data.updateTimeEnd:
            start_timestamp = self._datetime_to_timestamp(input_data.updateTimeStart)
            end_timestamp = self._datetime_to_timestamp(input_data.updateTimeEnd)
            filter_expressions.append(f"@update_time:[{start_timestamp} {end_timestamp}]")
        elif input_data.updateTimeStart:
            start_timestamp = self._datetime_to_timestamp(input_data.updateTimeStart)
            filter_expressions.append(f"@update_time:[{start_timestamp} inf]")
        elif input_data.updateTimeEnd:
            end_timestamp = self._datetime_to_timestamp(input_data.updateTimeEnd)
            filter_expressions.append(f"@update_time:[-inf {end_timestamp}]")
        
        # 如果有过滤条件，则添加到search_kwargs中
        if filter_expressions:
            # 使用括号将所有条件组合在一起
            if len(filter_expressions) > 1:
                search_kwargs["filter"] = "(" + " ".join(filter_expressions) + ")"
            else:
                search_kwargs["filter"] = filter_expressions[0]
            self.logger.info(f"使用的过滤表达式: {search_kwargs['filter']}")

        # 创建retriever
        retriever = self.vector_store.as_retriever(search_kwargs=search_kwargs)

        # 获取相关文档
        docs: List[Document] = retriever.invoke(input_data.message)
        
        # 添加调试日志，输出查询到的文档数量和具体内容
        self.logger.info(f"查询到的相关文档数量: {len(docs)}")
        for doc in docs:
            self.logger.info(f"相关文档内容: {doc.page_content}")

        # 如果没有找到相关文档，直接返回提示信息
        if not docs:
            self.logger.info("未找到相关文档")
            return ChatOutput.ok("我没有找到相关的笔记内容。请尝试调整查询条件或检查是否有对应用户的数据。")

        # 如果有相关文档，使用已获取的文档直接构建上下文，避免重复查询
        context_texts = self.collect_documents(docs)

        # 使用已有的上下文直接调用大模型
        response = (self.prompt_template | self.llm | StrOutputParser()).invoke({
            "context": context_texts,
            "question": input_data.message
        })

        self.logger.info(f"成功生成响应，问题: {input_data.message}")
        return ChatOutput.ok(response)