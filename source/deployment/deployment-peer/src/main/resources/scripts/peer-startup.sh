#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
PEER=$(ls $HOME/system | grep deployment-peer-)
JVM_SET="-server -Xmx2g -Xms1g"
PROC_INFO=$HOME/system/$PEER" -home="$HOME" -c "$HOME/config/ledger-binding.conf" -p 7080"
#echo $PROC_INFO
#get PID
PID=`ps -ef | grep "$PROC_INFO" | grep -v grep | awk '{print $2}'`
#echo $PID
if [[ ! -z $PID ]]
then
  echo "process already exists,please check... If necessary, you should kill the process first."
  exit
fi
if [ ! -n "$PEER" ]; then
  echo "Peer Is Null !!!"
else
  nohup java -jar $JVM_SET -Djdchain.log=$HOME $PROC_INFO $* >$HOME/bin/peer.out 2>&1 &
fi