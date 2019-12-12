
CUR_DIR=`pwd`

FROM_DIR=/usr/local/blockchain/prototype/dist/peer
PROC_DIR=/usr/local/blockchain/prototype/peer

REMOTE1=192.168.151.39
REMOTE2=192.168.151.40
REMOTE3=192.168.151.41


echo "Begin synchronizing ......"
cd $PROC_DIR
sh $PROC_DIR/stop.sh

cp -r $FROM_DIR/* $PROC_DIR

cd $PROC_DIR
chmod +x start.sh stop.sh
sh $PROC_DIR/start.sh

cd $CUR_DIR

echo "========================================="
echo "Success synchronizing local peer!"
echo "========================================="

./sync-to-remote.sh $FROM_DIR $REMOTE1 $PROC_DIR
cd $CUR_DIR

echo "========================================="
echo "Success synchronizing REMOTE peer[$REMOTE1]!"
echo "========================================="

./sync-to-remote.sh $FROM_DIR $REMOTE2 $PROC_DIR
cd $CUR_DIR

echo "========================================="
echo "Success synchronizing REMOTE peer[$REMOTE2]!"
echo "========================================="

#./sync-to-remote.sh $FROM_DIR $REMOTE3 $PROC_DIR

#echo "========================================="
#echo "Success synchronizing REMOTE peer[$REMOTE3]!"
#echo "========================================="


echo "========================================="
echo "Congratulations! All well done!"
echo "========================================="
