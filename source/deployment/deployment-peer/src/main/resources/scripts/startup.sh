#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
PEER=$(ls $HOME/system | grep deployment-peer-)
if [ ! -n "$PEER" ]; then
  echo "Peer Is Null !!!"
else
  nohup java -jar -server -Xmx1g -Xms512m -Dpeer.log=$HOME $HOME/system/$PEER -home=$HOME -c $HOME/config/ledger-binding.conf -p 7080 $* >$HOME/bin/peer.out 2>&1 &
fi