#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
  boot_file=$(ls ../libs | grep tools-regparti-booter-)
  if [ ! -n "$boot_file" ]; then
    echo "tools-regparti-booter is null"
  else
      echo "register new participant"
      java -jar $HOME/libs/$boot_file $*
  fi