#!/bin/bash

OME=$(cd `dirname $0`;cd ../; pwd)
boot_file=$(ls ../libs | grep tools-deactiveparti-booter-)
if [ ! -n "$boot_file" ]; then
  echo "tools-deactiveparti-booter is null"
else
    echo "deactive participant"
    java -jar $HOME/libs/$boot_file $*
fi