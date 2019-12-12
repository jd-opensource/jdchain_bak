PID=`ps -ef | grep "java -jar peer-" | grep -v grep | awk '{print $2}'`
PROC_PATH=`ps -ef | grep "java -jar peer-" | grep -v grep | awk '{print $8}'`
echo "Stopping peer[$PID][$PROC_PATH] ......"
kill 9 $PID

echo "Peer stopped!"
