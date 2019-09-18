#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
UMP=$(ls $HOME/ext | grep ump-booter-)
if [ ! -n "UMP" ]; then
  echo "Unified Management Platform Is Null !!!"
else
  nohup java -jar -server -Djump.log=$HOME $HOME/ext/$UMP -p 8000 $* >$HOME/bin/jump.out 2>&1 &
fi