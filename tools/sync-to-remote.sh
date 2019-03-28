
#Require Eironment Variable: FROM_DIR, REMOTE, PROC_DIR

FROM_DIR=$1
REMOTE=$2
PROC_DIR=$3

echo "FROM_DIR="$FROM_DIR
echo "REMOTE="$REMOTE
echo "PROC_DIR="$PROC_DIR

REMOTE_DIR=@$REMOTE:$PROC_DIR

echo "========================================="
echo "Begin synchronizing to [$REMOTE_DIR]"
echo "========================================="

ssh $REMOTE "cd $PROC_DIR; sh $PROC_DIR/stop.sh; rm -rf $PROC_DIR/*"
scp -r $FROM_DIR/* $REMOTE_DIR
ssh $REMOTE "cd $PROC_DIR; chmod +x $PROC_DIR/*.sh; sh $PROC_DIR/start.sh"

ssh $REMOTE<<EOF
cd $PROC_DIR
chmod +x $PROC_DIR/*.sh
$PROC_DIR/start.sh > /dev/null 2>&1 &
exit

