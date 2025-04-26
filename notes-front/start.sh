
#!/bin/bash
# ./ry.sh start 启动 stop 停止 restart 重启 status 状态
AppName=notes-front.jar

# JVM参数
APP_HOME="$(dirname "$0")"
# LOG_PATH=$APP_HOME/logs/$AppName.log
CONFIG_FILE="$APP_HOME/config/application.yml"

# 确保日志目录存在
mkdir -p $APP_HOME/logs

if [ "$1" = "" ];
then
    echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
    exit 1
fi

if [ "$AppName" = "" ];
then
    echo -e "\033[0;31m 未输入应用名 \033[0m"
    exit 1
fi

# 检查配置文件是否存在
if [ ! -f "$CONFIG_FILE" ]; then
    echo -e "\033[0;31m 配置文件 $CONFIG_FILE 不存在 \033[0m"
    exit 1
fi

# JVM参数，移除垃圾回收日志记录
JVM_OPTS="-Dname=$AppName  -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC"

function start()
{
    PID=$(ps -ef | grep java | grep $AppName | grep -v grep | awk '{print $2}')

    if [ x"$PID" != x"" ]; then
        echo "$AppName is running..."
    else
        # 使用外部配置文件启动应用程序
        nohup java $JVM_OPTS -Dspring.config.location=file:$CONFIG_FILE -jar $AppName > /dev/null 2>&1 &
        echo "Start $AppName success..."
    fi
}

function stop()
{
    echo "Stop $AppName"

    PID=""
    query(){
        PID=$(ps -ef | grep java | grep $AppName | grep -v grep | awk '{print $2}')
    }

    query
    if [ x"$PID" != x"" ]; then
        kill -TERM $PID
        echo "$AppName (pid:$PID) exiting..."
        while [ x"$PID" != x"" ]
        do
            sleep 1
            query
        done
        echo "$AppName exited."
    else
        echo "$AppName already stopped."
    fi
}

function restart()
{
    stop
    sleep 2
    start
}

function status()
{
    # 调试信息：打印 ps -ef | grep java | grep $AppName | grep -v grep 的输出
    echo "Checking for $AppName process..."
    ps -ef | grep java | grep $AppName | grep -v grep

    PID=$(ps -ef | grep java | grep $AppName | grep -v grep | awk '{print $2}')
    if [ x"$PID" != x"" ]; then
        echo "$AppName is running..."
    else
        echo "$AppName is not running..."
    fi
}

case $1 in
    start)
    start;;
    stop)
    stop;;
    restart)
    restart;;
    status)
    status;;
    *)
    echo "Invalid operation"
    exit 1;;
esac
