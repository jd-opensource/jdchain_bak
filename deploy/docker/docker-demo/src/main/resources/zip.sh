#/bin/bash

# all in one;
allIn1_file="./jdchain-demo_1.3.0.tar.gz"
if [ -f $allIn1_file ] ; then
 rm -rf $allIn1_file
fi
docker save  jdchain-demo:1.3.0 -o jdchain-demo_1.3.0.tar
gzip jdchain-demo_1.3.0.tar
