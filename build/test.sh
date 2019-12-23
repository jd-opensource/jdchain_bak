

#调用当前脚本目录下 env.sh 脚本，设置环境变量，处理当前传入参数；
if [ ! $ENV_SHELL ]
then 
    source `dirname $0`/env.sh "$*"
fi

# 更新代码库；
source $UPDATE_SHELL


echo "---------------- 开始集成测试 ----------------"

cd $TEST_DIR
echo "当前目录：`pwd`"

echo "执行命令：mvn clean package"
mvn clean package

echo "---------------- 完成集成测试 ----------------"