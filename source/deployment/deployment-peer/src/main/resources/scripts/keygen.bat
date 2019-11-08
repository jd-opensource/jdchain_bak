cd /d %~dp0
cd ..
set HOME=%cd%
java -jar %HOME%/libs/tools-keygen-booter-1.1.1.RELEASE.jar -Djdchain.log=%HOME%/log -o  %HOME%/config/keys %1 %2 %3
