#!/bin/bash

#设置Java命令
JAVA_BIN=java

#定义程序启动的Jar包前缀
APP_JAR_PREFIX=deploy-peer-

#Peer节点Web端口
#请运维根据实际环境进行调整或通过-p参数传入
WEB_PORT=7080
#端口配置参数
IS_CONFIG=false
for i in "$@"; do
    if [ $i = "-p" ];then
        IS_CONFIG=true
    fi
done

#检查Java环境变量
if [ ! -n "$JAVA_HOME" ]; then
  echo "UnFound environment variable[JAVA_HOME], will use command[java]..."
else
  JAVA_BIN=$JAVA_HOME/bin/java
fi

#获取当前的根目录
APP_HOME=$(cd `dirname $0`;cd ../; pwd)

#System目录
APP_SYSTEM_PATH=$APP_HOME/system

#nohup输出日志路径
LOG_OUT=$APP_HOME/bin/peer.out

#获取Peer节点的启动Jar包
APP_JAR=$(ls $APP_SYSTEM_PATH | grep $APP_JAR_PREFIX)

#Config配置路径
CONFIG_PATH=$APP_HOME/config

#ledger-binding.conf完整路径
LEDGER_BINDING_CONFIG=$CONFIG_PATH/ledger-binding.conf

#application-peer.properties完整路径
SPRING_CONFIG=$CONFIG_PATH/application-peer.properties

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
JAVA_OPTS="-jar -server -Xms2048m -Xmx2048m $opens -Djdchain.log=$APP_HOME/logs -Dlog4j.configurationFile=file:$APP_HOME/config/log4j2-peer.xml -Dpolyglot.engine.WarnInterpreterOnly=false"

#APP具体相关命令
APP_CMD=$APP_SYSTEM_PATH/$APP_JAR" -home="$APP_HOME" -c "$LEDGER_BINDING_CONFIG" -p "$WEB_PORT" -sp "$SPRING_CONFIG
if [ $IS_CONFIG = true ];then
    APP_CMD=$APP_SYSTEM_PATH/$APP_JAR" -home="$APP_HOME" -c "$LEDGER_BINDING_CONFIG" -sp "$SPRING_CONFIG
fi

#APP_JAR的具体路径
APP_JAR_PATH=$APP_SYSTEM_PATH/$APP_JAR

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
  echo "warn: Peer already started! (pid=$psid)"
  echo "================================"
else
  echo "Starting Peer ......"
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