#!/bin/bash

#启动Home路径
BOOT_HOME=$(cd `dirname $0`;cd ../; pwd)

#获取进程PID
PID=`ps -ef | grep $BOOT_HOME/manager/manager-booter | grep -v grep | awk '{print $2}'`

#通过Kill命令将进程杀死
if [ -z "$PID" ]; then
    echo "================================"
    echo "WARN: Unable to find JD Chain Manager PID($PID)."
    echo "================================"
else
    echo "Stopping Manager (PID = $PID) ......"
    kill $PID
    while kill -0 $PID 2>/dev/null; do sleep 1; done
    echo "================================"
    echo "Success"
    echo "================================"
fi