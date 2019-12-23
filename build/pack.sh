

#调用当前脚本目录下 env.sh 脚本，设置环境变量，处理当前传入参数；
if [ ! $ENV_SHELL ]
then 
    source `dirname $0`/env.sh "$*"
fi

# 更新代码库；
source $UPDATE_SHELL

echo "--------------- 开始编译打包产品 ---------------"

cd $DEPLOY_DIR
echo "当前目录：`pwd`"

#初始化变量 SKIP_TESTS；
if [ ! $SKIP_TESTS ]
then 
    SKIP_TESTS=0
fi

if [ $SKIP_TESTS == 1 ]
then
    echo "执行命令：mvn clean package -DskipTests=true"
    mvn clean package -DskipTests=true
else
    echo "执行命令：mvn clean package"
    mvn clean package
fi

echo "--------------- 完成编译打包产品 ---------------"
