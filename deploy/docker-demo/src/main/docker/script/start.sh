#!/bin/bash

cd $RELEASE_DIR

# invoke it once again;
#gw_pid_file="./gw/bin/PID.log"
#if [ ! -f gw_pid_file ] ; then
#  echo "get the PID.log, only to start the peer and gateway."
  unzip -o conf.zip

  for i in `seq 0 3`
  do
  unzip -n -d ./peer$i jdchain-peer-$RELEASE_VERSION.RELEASE.zip
  chmod +x ./peer$i/bin/*
  done

  unzip -n -d ./gw jdchain-gateway-$RELEASE_VERSION.RELEASE.zip
  chmod +x ./gw/bin/*
#fi

sh ./peer0/bin/peer-startup.sh
sh ./peer1/bin/peer-startup.sh
sh ./peer2/bin/peer-startup.sh
sh ./peer3/bin/peer-startup.sh
sleep 30
sh ./gw/bin/startup.sh

tail -f /dev/null
