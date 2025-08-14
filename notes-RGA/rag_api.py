from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.responses import StreamingResponse
from langchain_community.embeddings import DashScopeEmbeddings
from langchain_deepseek import ChatDeepSeek
from langchain_redis import RedisConfig
from redis.connection import ConnectionPool

from chat_service import NotesChatService, ChatInput, ChatOutput
from config.load_config import load_config
from config.load_key import load_key
from config.logging_config import LoggerFactory
from store_service import NotesStorageService, Notes, Response

"""
RAG 本地定制化AI API接口
"""

# 创建存储服务实例
embedding_model = DashScopeEmbeddings(model='text-embedding-v1', dashscope_api_key=load_key("DASHSCOPE_API_KEY"))
print(f'redis://:{load_key("REDIS_PASSWORD")}@{load_config("REDIS_URL")}')
redis_config = RedisConfig(index_name='notes-index',
                           redis_url=f'redis://:{load_key("REDIS_PASSWORD")}@{load_config("REDIS_URL")}')
redis_pool = ConnectionPool(
    host=load_config("REDIS_URL"),
    port=6379,
    password=load_key("REDIS_PASSWORD"),
    decode_responses=False,
    max_connections=20  # 设置最大连接数
)
notes_storage_service = NotesStorageService(embedding_model, redis_config, redis_pool)
llm = ChatDeepSeek(model_name="deepseek-reasoner", base_url="https://api.deepseek.com",
                    api_key=load_key("DEEPSEEK_API_KEY"))
notes_chat_service = NotesChatService(embedding_model, redis_config, llm)

# 创建logger实例
logger = LoggerFactory.get_logger("rag_api")


# 定义lifespan事件处理器
@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("应用启动中...")
    yield
    notes_storage_service.close()
    logger.info("应用已关闭")


# 创建FastAPI应用
app = FastAPI(title="RAG API Server", version="1.0", lifespan=lifespan)


@app.post("/rag/store")
async def store(note: Notes):
    try:
        logger.info(f"收到存储请求，笔记ID: {note.id}")
        # 调用存储服务，将note向量化保存到redis
        notes_storage_service.store_note_to_redis(note)
        logger.info(f"笔记 {note.id} 存储成功")
        return Response(code=200, message="success", data=None, success=True)
    except Exception as e:
        logger.error(f"处理失败: {str(e)}", exc_info=True)
        return Response(code=500, message=f"处理失败: {str(e)}", data=None, success=False)


@app.post("/rag/chat", response_model=ChatOutput)
async def chat(input_data: ChatInput) -> ChatOutput:
    """
    使用RAG技术进行聊天
    """
    logger.info(f"收到聊天请求: {input_data.message}")
    result = notes_chat_service.chat_with_rag(input_data)
    logger.info("聊天请求处理完成")
    return result
@app.post("/rag/chat-stream")
async def chat_stream(input_data: ChatInput):
    """
    使用RAG技术进行聊天，支持流式响应
    """
    logger.info(f"收到流式聊天请求: {input_data.message}")

    async def event_generator():
        async for chunk in notes_chat_service.chat_with_rag_stream(input_data):
            yield chunk

    return StreamingResponse(event_generator(), media_type="text/event-stream")




if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=20002)