import logging
import logging.handlers
import os
from typing import Optional

# 定义日志格式
LOG_FORMAT = "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
DATE_FORMAT = "%Y-%m-%d %H:%M:%S"

# 日志目录
LOG_DIR = "logs"

# 确保日志目录存在
if not os.path.exists(LOG_DIR):
    os.makedirs(LOG_DIR)


class LoggerFactory:
    """Logger工厂类，用于动态创建和管理logger实例"""
    
    _loggers = {}
    
    @classmethod
    def get_logger(
            cls,
            name: str,
            level: int = logging.INFO,
            max_bytes: int = 10 * 1024 * 1024,  # 10MB
            backup_count: int = 5
    ) -> logging.Logger:
        """
        获取或创建一个配置好的logger
        
        :param name: logger名称
        :param level: 日志级别
        :param max_bytes: 日志文件最大字节数
        :param backup_count: 保留的备份日志文件数量
        :return: 配置好的logger
        """
        if name in cls._loggers:
            return cls._loggers[name]
        
        logger = logging.getLogger(name)
        logger.setLevel(level)
        
        # 创建格式化器
        formatter = logging.Formatter(LOG_FORMAT, DATE_FORMAT)
        
        # 添加控制台处理器
        console_handler = logging.StreamHandler()
        console_handler.setLevel(level)
        console_handler.setFormatter(formatter)
        logger.addHandler(console_handler)
        
        # 添加文件处理器
        log_file = os.path.join(LOG_DIR, f"{name}.log")
        file_handler = logging.handlers.RotatingFileHandler(
            log_file, maxBytes=max_bytes, backupCount=backup_count, encoding='utf-8'
        )
        file_handler.setLevel(level)
        file_handler.setFormatter(formatter)
        logger.addHandler(file_handler)
        
        cls._loggers[name] = logger
        return logger


# 为常用模块提供便捷访问
def get_api_logger() -> logging.Logger:
    """获取API模块logger"""
    return LoggerFactory.get_logger("rag_api")


def get_storage_logger() -> logging.Logger:
    """获取存储模块logger"""
    return LoggerFactory.get_logger("store_service")


def get_chat_logger() -> logging.Logger:
    """获取聊天模块logger"""
    return LoggerFactory.get_logger("chat_service")