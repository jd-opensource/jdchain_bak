#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
PEER=$(ls $HOME/system | grep deployment-peer-)
if [ ! -n "$PEER" ]; then
  echo "Peer Is Null !!!"
else
  nohup java -jar -server -Xmx2g -Xms2g $HOME/system/$PEER -home=$HOME -c $HOME/config/ledger-binding.conf -p 7080 $* &
fi