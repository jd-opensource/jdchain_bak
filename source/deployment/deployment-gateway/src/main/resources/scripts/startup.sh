#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
GATEWAY=$(ls $HOME/lib | grep deployment-gateway-)
PROC_INFO=$HOME/lib/$GATEWAY" -c "$HOME/config/gateway.conf
#echo $PROC_INFO
#get PID
PID=`ps -ef | grep "$PROC_INFO" | grep -v grep | awk '{print $2}'`
#echo $PID
if [[ ! -z $PID ]]
then
  echo "process already exists,please check... If necessary, you should kill the process first."
  exit
fi
if [ ! -n "$GATEWAY" ]; then
  echo "GateWay Is Null !!!"
else
  nohup java -jar -server -Djdchain.log=$HOME $PROC_INFO $* >$HOME/bin/gw.out 2>&1 &
fi