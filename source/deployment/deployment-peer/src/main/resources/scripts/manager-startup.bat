set HOME=%cd%
java -jar -server -Djdchain.log=%HOME%/log %HOME%/manager/manager-booter-1.1.1.RELEASE.jar -home %HOME% -p 8000 %1 %2 %3
