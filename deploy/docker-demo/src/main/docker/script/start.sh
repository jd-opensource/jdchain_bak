#!/bin/bash

cd $RELEASE_DIR

# check the files;
peer_file="./jdchain-peer-$RELEASE_VERSION.RELEASE.zip"
gw_file="./jdchain-gateway-$RELEASE_VERSION.RELEASE.zip"
sdk_file="./docker-sdk-$RELEASE_VERSION.RELEASE.jar"
if [[ ! -f $peer_file ]] || [[ ! -f $gw_file ]] || [[ ! -f $sdk_file ]] ; then
echo "not find $peer_file or $gw_file or $sdk_file in the $RELEASE_DIR, please check the image of jdchain-demo:$RELEASE_VERSION."
exit 1
fi

  unzip -o conf.zip

  for i in `seq 0 3`
  do
  unzip -n -d ./peer$i jdchain-peer-$RELEASE_VERSION.RELEASE.zip
  chmod +x ./peer$i/bin/*
  done

  unzip -n -d ./gw jdchain-gateway-$RELEASE_VERSION.RELEASE.zip
  chmod +x ./gw/bin/*

sh ./peer0/bin/peer-startup.sh
sh ./peer1/bin/peer-startup.sh
sh ./peer2/bin/peer-startup.sh
sh ./peer3/bin/peer-startup.sh
sleep 30
sh ./gw/bin/startup.sh
sleep 10
java -jar docker-sdk-1.3.0.RELEASE.jar > sdk.log

tail -f /dev/null
