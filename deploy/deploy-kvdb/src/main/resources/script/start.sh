#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
KVDB=$(ls $HOME/libs | grep kvdb-server)
JVM_SET="-Xmx2g -Xms2g"
PROC_INFO=$HOME/libs/$KVDB" -home "$HOME
LOG_SET="-Dlogging.path="$HOME/logs" -Dlogging.level=error"
#echo $PROC_INFO
#get PID
PID=`ps -ef | grep "$PROC_INFO" | grep -v grep | awk '{print $2}'`
#echo $PID
if [[ ! -z $PID ]]
then
  echo "process already exists,please check... If necessary, you should kill the process first."
  exit
fi
if [ ! -n "$KVDB" ]; then
  echo "Can not find kvdb-server !!!"
else
  nohup java -jar $LOG_SET $JVM_SET $PROC_INFO $* >/dev/null 2>&1 &

  echo $! > $HOME/system/pid
fi