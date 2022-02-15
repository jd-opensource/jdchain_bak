#!/bin/bash

#设置Java命令
JAVA_BIN=java

#定义程序启动的Jar包前缀
APP_JAR_PREFIX=deploy-gateway-

#检查Java环境变量
if [ ! -n "$JAVA_HOME" ]; then
  echo "UnFound environment variable[JAVA_HOME], will use command[java]..."
else
  JAVA_BIN=$JAVA_HOME/bin/java
fi

#获取当前的根目录
APP_HOME=$(cd `dirname $0`;cd ../; pwd)

#Lib目录
APP_LIB_PATH=$APP_HOME/lib

#nohup输出日志路径
LOG_OUT=$APP_HOME/bin/gw.out

#获取Peer节点的启动Jar包
APP_JAR=$(ls $APP_LIB_PATH | grep $APP_JAR_PREFIX)

#Config配置路径
CONFIG_PATH=$APP_HOME/config

#gateway.conf完整路径
GATEWAY_CONFIG=$CONFIG_PATH/gateway.conf

#application-gw.properties完整路径
SPRING_CONFIG=$CONFIG_PATH/application-gw.properties

JDK_VERSION=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}')
if [[ $JDK_VERSION == 1.8.* ]]; then
  opens=""
else
  opens="--add-opens java.base/java.lang=ALL-UNNAMED"
  opens=$opens" --add-opens java.base/java.util=ALL-UNNAMED"
  opens=$opens" --add-opens java.base/java.net=ALL-UNNAMED"
  opens=$opens" --add-opens java.base/sun.security.x509=ALL-UNNAMED"
  opens=$opens" --add-opens java.base/sun.security.util=ALL-UNNAMED"
fi

#定义程序启动的参数
JAVA_OPTS="-jar -server -Xms1024m -Xmx1024m $opens -Djdchain.log=$APP_HOME/logs -Dlog4j.configurationFile=file:$APP_HOME/config/log4j2-gw.xml"

#APP具体相关命令
APP_CMD=$APP_LIB_PATH/$APP_JAR" -c "$GATEWAY_CONFIG" -sp "$SPRING_CONFIG

#APP_JAR的具体路径
APP_JAR_PATH=$APP_LIB_PATH/$APP_JAR

#JAVA_CMD具体命令
JAVA_CMD="$JAVA_BIN $JAVA_OPTS $APP_CMD"

###################################
#(函数)判断程序是否已启动
#
#说明：
#使用awk，分割出pid ($1部分)，及Java程序名称($2部分)
###################################
#初始化psid变量（全局）
psid=0

checkpid() {
  javaps=`ps -ef | grep $APP_JAR_PATH | grep -v grep | awk '{print $2}'`

  if [[ -n "$javaps" ]]; then
    psid=$javaps
  else
    psid=0
  fi
}

###################################
#(函数)打印系统环境参数
###################################
info() {
  echo "System Information:"
  echo "****************************"
  echo `uname -a`
  echo
  echo `$JAVA_BIN -version`
  echo
  echo "APP_HOME=$APP_HOME"
  echo "APP_JAR=$APP_JAR"
  echo "CONFIG_PATH=$CONFIG_PATH"
  echo "APP_JAR_PATH=$APP_JAR_PATH"
  echo
  echo "JAVA_CMD=$JAVA_CMD"
  echo "****************************"
}

#真正启动的处理流程
checkpid

if [[ $psid -ne 0 ]]; then
  echo "================================"
  echo "warn: Gateway already started! (pid=$psid)"
  echo "================================"
else
  echo "Starting Gateway ......"
  nohup $JAVA_BIN $JAVA_OPTS $APP_CMD $* >$LOG_OUT 2>&1 &
  JAVA_CMD="$JAVA_BIN $JAVA_OPTS $APP_CMD $*"
  sleep 1
  checkpid
  if [[ $psid -ne 0 ]]; then
    echo "(pid=$psid) [OK]"
    info
  else
    echo "[Failed]"
  fi
fi