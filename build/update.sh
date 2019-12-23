
#调用当前脚本目录下 env.sh 脚本，设置环境变量，处理当前传入参数；
if [ ! $ENV_SHELL ]
then 
    source `dirname $0`/env.sh "$*"
fi

#检查是否要跳过子模块更新环节；
if [ $SKIP_SUBMODULES_UPDATE == 1 ]
then
    echo "跳过子模块代码更新操作。。。"
    ${RTN}
fi

#初始化变量 SUBMODULES_UPDATED
if [ ! $SUBMODULES_UPDATED ]
then
    SUBMODULES_UPDATED=0
fi

if [ $SUBMODULES_UPDATED == 1 ]
then
    echo "代码库已经最新，跳过更新操作。。。"
    ${RTN}
fi
    
echo "---------------- 更新代码库 ----------------"
cd $BASE_DIR
git submodule update --init --recursive

#检查执行结果是否正常
ERR=$?
if [ $ERR != 0 ]
then
    echo "更新代码库时发生了错误！！返回错误码：$ERR"
    ${RTN} $ERR
fi

#标记代码已经更新；
SUBMODULES_UPDATED=1

echo "---------------- 完成代码库更新 ----------------"
