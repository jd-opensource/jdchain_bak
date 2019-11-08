cd /d %~dp0
cd ..
set HOME=%cd%
java -jar -server -Xmx2g -Xms2g -Djdchain.log=%HOME%/log %HOME%/system/deployment-peer-1.1.1.RELEASE.jar -home=%HOME% -c %HOME%/config/ledger-binding.conf -p 7080 %1 %2 %3
