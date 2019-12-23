
DIST_BASE_DIR=/usr/local/blockchain/prototype/dist/peer

cd /usr/local/blockchain/prototype/source/prototype.git/source/



echo "========================================="
echo "Begin updating source code"
echo "========================================="

git checkout develop
git pull origin develop

echo "========================================="
echo "Source code was updated to latest!"
echo "========================================="


echo "========================================="
echo "Begin compiling"
echo "========================================="

mvn clean package -T 2C

echo "========================================="
echo "Begin copy artifacts to local dist dir!"
echo "========================================="

rm -rf $DIST_BASE_DIR/*

cp peer/target/peer-*.jar $DIST_BASE_DIR

cp -r peer/config $DIST_BASE_DIR
cp peer/target/classes/application.properties $DIST_BASE_DIR/config

cp -r peer/shell/* $DIST_BASE_DIR


echo "========================================="
echo "congratulation! Well done!"
echo "========================================="
