#!/bin/bash

#启动Home路径
HOME=$(cd `dirname $0`;cd ../; pwd)

#进程启动后PID.log所在路径
PID_LOG=$HOME/system/pid

#从启动文件中读取PID
if [ -f "$PID_LOG" ]; then
    # File exist
    echo "Read PID From File:[$PID_LOG] ..."
    PID=`sed -n '$p' $PID_LOG`
#启动文件不存在则直接通过PS进行过滤
else
    PID=`ps -ef | grep $HOME/libs/kvdb-server | grep -v grep | awk '{print $2}'`
fi

#通过Kill命令将进程杀死
if [ -z "$PID" ]; then
    echo "Unable to find kvdb PID. stop aborted."
else
    echo "Start to kill PID = $PID ..."
    kill -9 $PID
    echo "kvdb has been stopped ..."
    echo "" > $PID_LOG
fi