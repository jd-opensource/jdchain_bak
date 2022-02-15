#!/bin/bash

HOME=$(cd `dirname $0`;cd ../; pwd)
boot_file=$(ls $HOME/libs | grep tools-initializer-booter-)
JDK_VERSION=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}')
if [[ $JDK_VERSION == 1.8.* ]]; then
  opens=""
else
  opens="--add-opens java.base/java.lang=ALL-UNNAMED"
  opens=$opens" --add-opens java.base/java.util=ALL-UNNAMED"
  opens=$opens" --add-opens java.base/java.net=ALL-UNNAMED"
  opens=$opens" --add-opens java.base/sun.security.x509=ALL-UNNAMED"
  opens=$opens" --add-opens java.base/sun.security.util=ALL-UNNAMED"
fi
if [ ! -n "$boot_file" ]; then
  echo "tools-initializer-booter is null"
else
  java -jar -server $opens -Djdchain.log=$HOME/logs $HOME/libs/$boot_file -l $HOME/config/init/local.conf -i $HOME/config/init/ledger.init $*
fi