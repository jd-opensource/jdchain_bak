
#定义子脚本的错误返回命令
RTN="return"


#调用当前脚本目录下 env.sh 脚本，设置环境变量，处理当前传入参数；
source `dirname $0`/env.sh "$*"

echo "主目录：$BASE_DIR"

#判断是否忽略测试；
if [ $SKIP_TESTS == 1 ]
then 
    echo "跳过测试。。。"
else
    #执行测试；
    source $TEST_SHELL

    #检查执行结果是否正常
    ERR=$?
    if [ $ERR != 0 ]
    then
        echo "构建过程中发生了集成测试错误[$ERR]！！终止构建！！"
        exit $ERR
    fi
    #结束集成测试错误检查；
fi

#如果执行了测试，并且测试已经通过，那么在打包的过程中可以跳过执行单元测试；
if [ $SKIP_TESTS == 0 ]
then
    SKIP_TESTS=1
fi

#执行打包构建；
source $PACK_SHELL

#检查执行结果是否正常
ERR=$?
if [ $ERR != 0 ]
then
    echo "构建过程中发生了打包错误[$ERR]！！终止构建！！"
    exit $ERR
fi