#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
boot_file=$(ls ../libs | grep tools-activeparti-booter-)
if [ ! -n "$boot_file" ]; then
  echo "tools-activeparti-booter is null"
else
    echo "active participant"
    java -jar $HOME/libs/$boot_file $*
fi