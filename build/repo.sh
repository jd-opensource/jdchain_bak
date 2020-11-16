#调用当前脚本目录下 env.sh 脚本，设置环境变量，处理当前传入参数；
if [ ! $ENV_SHELL ]
then 
    source `dirname $0`/env.sh "$*"
fi

echo ""

echo "切换到主库根目录。。。"
cd $BASE_DIR
pwd

#先执行初始化; 注：此命令只在 .git/config 文件中没有相应子模块的配置时才生效，重复执行并不会更改已有的配置；
git submodule init

echo ""

#根据本地仓库的 .git 目录下是否存在 local.sh 脚本判断是否在更新子模块的代码库之前执行本地化配置；
echo "检查是否执行子模块的本地化配置。。。"
#判断本地化配置脚本是否存在；
LOCAL_CONFIG="$BASE_DIR/.git/local.config"

LOCALIZED=0
if [ -f $LOCAL_CONFIG -a $CONFIG_REPO_PUBLIC == 0 ]
then
    #执行子模块的本地化配置，将子模块的远程仓库地址指向本地；
    echo "---------------- 执行仓库的本地化配置 ----------------"
    #解析本地配置
    KEYS=($(cat $LOCAL_CONFIG | awk -F '=' 'length($1)>0 { print $1}'))
    VALUES=($(cat $LOCAL_CONFIG | awk -F '=' 'length($2)>0 { print $2}'))
    
    #匹配子模块 URL 配置名称的正则表达式
    # REG="submodule[\.].*[\.]url"

    #判断本地配置是否为空；
    echo "共有 ${#KEYS[@]} 项本地配置。。。"
    if [ ${#KEYS[@]} -gt 0 ]
    then
        for ((i=0; i<${#KEYS[@]}; i++));
        do
            #执行本地化配置；
            echo "[$i]: git config ${KEYS[i]} ${VALUES[i]}"
            git config ${KEYS[i]} ${VALUES[i]}

            #检查执行结果是否正常
            ERR=$?
            if [ $ERR != 0 ]
            then
                echo "执行仓库的本地化配置的过程中发生了错误[$ERR]！！终止构建！！"
                ${RTN} $ERR
            fi
        done
        
        LOCALIZED=1
    fi
    
    echo "---------------- 完成执行仓库的本地化配置 ----------------"
    echo ""
fi


# 如果没有进行仓库的本地化配置，则将仓库设置为公开配置；
cd $BASE_DIR

if [ $LOCALIZED == 0 ]
then
    echo "---------------- 执行仓库的公共配置 ----------------"

    echo "同步子模块的公共配置。。。"
    git submodule sync

    # 更新远程仓库地址；
    git config remote.origin.url git@github.com:blockchain-jd-com/jdchain.git
    
    echo "---------------- 完成执行仓库的公共配置 ----------------"
    echo ""
fi


#同步主库的远程仓库 origin 的推送地址；
REMOTE_ORIGIN_URL=$(git config --get remote.origin.url)
git config remote.origin.pushurl $REMOTE_ORIGIN_URL

echo "远程仓库的地址更新为："$REMOTE_ORIGIN_URL
echo ""

#同步更新子模块的远程仓库 origin 的地址;
echo "---------------- 更新子模块的远程仓库地址 ----------------"
# 子模块名称列表；

SUBMODULES=$(git submodule | awk '{print $2}')
for m in $SUBMODULES;
do
    SUBMODULE_URL=$(git config --get submodule.$m.url)
    echo "模块[$m].URL="$SUBMODULE_URL

    cd $BASE_DIR/$m

    git config remote.origin.url $SUBMODULE_URL
    git config remote.origin.pushurl $SUBMODULE_URL

    cd $BASE_DIR
done

#首次执行同步更新子模块的远程仓库 origin 的地址会将主项目地址更改，以下操作确保主项目远程仓库地址正确
git config remote.origin.url $REMOTE_ORIGIN_URL
git config remote.origin.pushurl $REMOTE_ORIGIN_URL

#检查是否要跳过子模块更新环节；
if [ $SKIP_SUBMODULES_UPDATE == 1 ]
then
    echo "跳过子模块代码更新操作。。。[$RTN]"
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

# 更新子模块代码库
echo "---------------- 更新子模块代码库 ----------------"
cd $BASE_DIR
echo "git submodule update --recursive --progress --jobs 6"
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

echo ""
