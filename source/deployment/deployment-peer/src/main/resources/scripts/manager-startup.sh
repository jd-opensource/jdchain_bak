#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
UMP=$(ls $HOME/manager | grep manager-booter-)
if [ ! -n "UMP" ]; then
  echo "JDChain Manager Is Null !!!"
else
  nohup java -jar -server -Djump.log=$HOME $HOME/manager/$UMP -home $HOME -p 8000 $* >$HOME/bin/jump.out 2>&1 &
fi