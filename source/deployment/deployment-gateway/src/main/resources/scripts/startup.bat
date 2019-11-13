cd /d %~dp0
cd ..
set HOME=%cd%
java -jar -server -Djdchain.log=%HOME%/log %HOME%/lib/deployment-gateway-1.1.1.RELEASE.jar -c %HOME%/config/gateway.conf -debug -sp %HOME%/config/application-gw.properties %1 %2 %3
