#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
boot_file=$(ls ../libs | grep tools-initializer-booter-)
if [ ! -n "$boot_file" ]; then
  echo "tools-initializer-booter is null"
else
  java -jar $HOME/libs/$boot_file -l $HOME/config/init/local.conf -i $HOME/config/init/ledger.init $*
fi
