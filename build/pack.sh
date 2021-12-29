

#调用当前脚本目录下 env.sh 脚本，设置环境变量，处理当前传入参数；
if [ ! $ENV_SHELL ]
then 
    source `dirname $0`/env.sh "$*"
fi

# 更新代码库；
source $UPDATE_SHELL

#检查执行结果是否正常
ERR=$?
if [ $ERR != 0 ]
then
    echo "更新代码库时发生了错误[$ERR]！！终止打包！！"
    ${RTN} $ERR
fi


echo "--------------- 开始编译打包 ---------------"

cd $DEPLOY_DIR
echo "当前目录：$(pwd)"

#初始化变量 SKIP_TESTS；
if [ ! $SKIP_TESTS ]
then 
    SKIP_TESTS=0
fi

CMD="mvn clean package"

if [ $SKIP_TESTS == 1 ]
then
    echo "编译参数：-DskipTests=true"
    CMD="$CMD -DskipTests=true"
fi
if [ $SKIP_TESTS == 2 ]
then
    echo "编译参数：-Dmaven.test.skip=true"
    CMD="$CMD -Dmaven.test.skip=true"
fi

echo "执行命令：$CMD"
${CMD}

#检查执行结果是否正常
ERR=$?
if [ $ERR != 0 ]
then
    echo "编译打包过程中发生了错误[$ERR]！！终止打包！！"
    ${RTN} $ERR
fi

echo "--------------- 完成编译打包 ---------------"
