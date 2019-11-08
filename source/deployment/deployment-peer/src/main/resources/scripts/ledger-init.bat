cd /d %~dp0
cd ..
set HOME=%cd%
java -jar -server -Djdchain.log=%HOME%/log %HOME%/libs/tools-initializer-booter-1.1.1.RELEASE.jar  -l %HOME%/config/init/local.conf -i %HOME%/config/init/ledger.init $1 $2 $3
