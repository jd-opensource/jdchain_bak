
FILTER=$1

PID=`ps -ef | grep "name=$FILTER" | grep -v grep | awk '{print $2}'`

PROC_PATH=`ps -ef | grep "name=$FILTER" | grep -v grep | awk '{print $8}'`
echo "Stopping peer[$PID][$FILTER] ......"
kill -9 $PID

echo "Peer[$PID][$FILTER] stopped!"
