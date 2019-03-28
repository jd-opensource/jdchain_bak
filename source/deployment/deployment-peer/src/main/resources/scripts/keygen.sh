#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
boot_file=$(ls ../libs | grep tools-keygen-booter-)
if [ ! -n "$boot_file" ]; then
  echo "tools-keygen-booter is null"
else
  if [ ! -d $HOME/config/keys ];then
    mkdir $HOME/config/keys
    echo "create new dir $HOME/config/keys"
    echo "keys file will be saved $HOME/config/keys"
  else
    echo "keys file will be saved $HOME/config/keys"
  fi
  java -jar $HOME/libs/$boot_file -o $HOME/config/keys -l $HOME/config/init/local.conf $*
fi