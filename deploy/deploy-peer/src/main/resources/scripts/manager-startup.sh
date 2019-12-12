#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
UMP=$(ls $HOME/manager | grep manager-booter-)
PROC_INFO=$HOME/manager/$UMP" -home "$HOME" -p 8000"
#echo $PROC_INFO
#get PID
PID=`ps -ef | grep "$PROC_INFO" | grep -v grep | awk '{print $2}'`
#echo $PID
if [[ ! -z $PID ]]
then
  echo "process already exists,please check... If necessary, you should kill the process first."
  exit
fi
if [ ! -n "UMP" ]; then
  echo "JDChain Manager Is Null !!!"
else
  nohup java -jar -server -Djdchain.log=$HOME $PROC_INFO $* >$HOME/bin/jump.out 2>&1 &
fi