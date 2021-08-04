#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
boot_file=$(ls $HOME/libs | grep jdchain-cli-)
if [ ! -n "$boot_file" ]; then
  echo "can not find jdchain-cli in libs"
else
  java -jar $HOME/libs/$boot_file $*
fi
