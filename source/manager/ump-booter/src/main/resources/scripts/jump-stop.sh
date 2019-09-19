#!/bin/bash

#启动Home路径
BOOT_HOME=$(cd `dirname $0`;cd ../; pwd)

#获取进程PID
PID=`ps -ef | grep $BOOT_HOME/ext/ump-booter | grep -v grep | awk '{print $2}'`

#通过Kill命令将进程杀死
if [ -z "$PID" ]; then
    echo "Unable to find UMP PID. stop aborted."
else
    echo "Start to kill PID = $PID ..."
    kill -9 $PID
    echo "Unified Management Platform has been stopped ..."
fi