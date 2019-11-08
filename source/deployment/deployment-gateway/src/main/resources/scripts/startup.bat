cd /d %~dp0
cd ..
set HOME=%cd%
java -jar -server -Djdchain.log=%HOME%/log %HOME%/lib/deployment-gateway-1.1.1.RELEASE.jar -c %HOME%/config/gateway.conf %1 %2 %3
