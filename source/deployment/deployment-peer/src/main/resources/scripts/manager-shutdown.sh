#!/bin/bash

#启动Home路径
BOOT_HOME=$(cd `dirname $0`;cd ../; pwd)

#获取进程PID
PID=`ps -ef | grep $BOOT_HOME/manager/manager-booter | grep -v grep | awk '{print $2}'`

#通过Kill命令将进程杀死
if [ -z "$PID" ]; then
    echo "Unable to find JDChain Manager PID. stop aborted."
else
    echo "Start to kill PID = $PID ..."
    kill -9 $PID
    echo "JDChain Manager has been stopped ..."
fi