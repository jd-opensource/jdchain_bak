#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
GATEWAY=$(ls $HOME/lib | grep deployment-gateway-)
if [ ! -n "$GATEWAY" ]; then
  echo "GateWay Is Null !!!"
else
  nohup java -jar -server $HOME/lib/$GATEWAY -c $HOME/config/gateway.conf $* > gw.out 2>&1 &
fi