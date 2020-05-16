#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
KVDB=$(ls $HOME/libs | grep kvdb-cli)
JVM_SET="-Xmx2g -Xms2g"
LOG_SET="-Dlogging.path="$HOME/logs" -Dlogging.level.root=error"
PROC_INFO=$HOME/libs/$KVDB
if [ ! -n "$KVDB" ]; then
  echo "Can not find kvdb-cli !!!"
else
  java -jar $LOG_SET $JVM_SET $PROC_INFO $*
fi