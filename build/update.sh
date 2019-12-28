
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

#先执行初始化; 注：此命令只在 .git/config 文件中没有相应子模块的配置时才生效，重复执行并不会更改已有的配置；
git submodule init

#根据本地仓库的 .git 目录下是否存在 local.sh 脚本判断是否在更新子模块的代码库之前执行本地化配置；
echo "检查是否执行子模块的本地化配置。。。"
#判断本地化配置脚本是否存在；
LOCAL_CONFIG="$BASE_DIR/.git/local.config"
if [ -f $LOCAL_CONFIG ]
then
    #执行子模块的本地化配置，将子模块的远程仓库地址指向本地；
    echo "---------------- 执行子模块的本地化配置 ----------------"
    #解析本地配置
    KEYS=($(cat $LOCAL_CONFIG | awk -F '=' 'length($1)>0 { print $1}'))
    VALUES=($(cat $LOCAL_CONFIG | awk -F '=' 'length($2)>0 { print $2}'))
    
    for ((i=0; i<${#KEYS[@]}; i ++));
    do
        echo "[$i]: git config ${KEYS[i]} ${VALUES[i]}"
        git config ${KEYS[i]} ${VALUES[i]}

        #检查执行结果是否正常
        ERR=$?
        if [ $ERR != 0 ]
        then
            echo "执行子模块的本地化配置的过程中发生了错误[$ERR]！！终止构建！！"
            ${RTN} $ERR
        fi
    done

    echo "---------------- 完成子模块的本地化配置 ----------------"
else
    echo "---------------- 执行子模块的公共配置 ----------------"
    git submodule sync
fi

    
echo "---------------- 更新子模块代码库 ----------------"
cd $BASE_DIR
git submodule update --recursive --progress --jobs 6

#检查执行结果是否正常
ERR=$?
if [ $ERR != 0 ]
then
    echo "更新子模块代码库时发生了错误！！返回错误码：$ERR"
    ${RTN} $ERR
fi

#标记代码已经更新；
SUBMODULES_UPDATED=1

echo "---------------- 完成子模块代码库更新 ----------------"
