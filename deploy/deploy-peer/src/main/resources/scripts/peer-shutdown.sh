#!/bin/bash

#定义程序启动的Jar包前缀
APP_JAR_PREFIX=deploy-peer-

#获取当前的根目录
APP_HOME=$(cd `dirname $0`;cd ../; pwd)

#System路径
APP_SYSTEM_PATH=$APP_HOME/system

#获取Peer节点的启动Jar包
APP_JAR=$(ls $APP_SYSTEM_PATH | grep $APP_JAR_PREFIX)

#APP_JAR的具体路径
APP_JAR_PATH=$APP_SYSTEM_PATH/$APP_JAR

###################################
#(函数)判断程序是否已启动
#
#说明：
#使用awk，分割出pid ($1部分)，及Java程序名称($2部分)
###################################
#初始化psid变量（全局）
psid=0

checkpid() {
  psid=`ps -ef | grep $APP_JAR_PATH | grep -v grep | awk '{print $2}'`
}

###################################
#(函数)停止程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则开始执行停止，否则，提示程序未运行
#3. 使用kill -9 pid命令进行强制杀死进程
#4. 执行kill命令行紧接其后，马上查看上一句命令的返回值: $?
#5. 如果步骤4的结果$?等于0,则打印[OK]，否则打印[Failed]
#注意：echo -n 表示打印字符后，不换行
#注意: 在shell编程中，"$?" 表示上一句命令或者一个函数的返回值
###################################
stop() {
    checkpid
    if [[ $psid -ne 0 ]]; then
      echo "Stopping Peer (PID = $psid) ......"
      kill $psid
      while kill -0 $psid 2>/dev/null; do sleep 1; done
      echo "================================"
      echo "Success"
      echo "================================"
    else
      echo "================================"
      echo "WARN: Peer is not running"
      echo "================================"
    fi
}


#真正停止的处理流程
stop