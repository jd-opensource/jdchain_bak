```bash
#!/bin/bash

HOME=$(cd `dirname $0`; pwd)

echo "" > nohup.out

ps -ef|grep '$HOME'|grep -v grep|cut -c 9-15|xargs kill -9

echo "Start jdchain Initialing in $HOME"

## parameters
# 节点数量
NODE_SIZE=4
# 允许错误节点个数
FAULT_SIZE=1
# 数据库URI
DB_URI=""
# peer API server start port
PEER_PORT=7080
# consus start port
CONSUS_PORT=10080
# DB password
DB_PWD=""
# ledger size
LEDGER_SIZE=1

while getopts ":N:f:d:P:p:w:L:" opt
do
    case $opt in
        N)
            NODE_SIZE=$OPTARG
        ;;
        f)
            FAULT_SIZE=$OPTARG
        ;;
        d)
            DB_URI=$OPTARG
        ;;
        P)
            PEER_PORT=$OPTARG
        ;;
        p)
            CONSUS_PORT=$OPTARG
        ;;
        w)
            DB_PWD=$OPTARG
        ;;
        L)
            LEDGER_SIZE=$OPTARG
        ;;
        ?)
            echo "unknow parameter"
    esac
done

# clear old data
rm -rf peer*
rm -rf gw*

####################################### init peers files
KEY_NAME=`date '+%s'`
i=0
while(( $i<$NODE_SIZE ))
do
    # uzip peer and gateway files
    unzip -oq jdchain-peer-* -d peer$i
    chmod 777 peer$i/bin/*

    # generate key
    mkdir peer$i/config/keys
    if [ -f "keygen" ];then
        chmod +x keygen
        ./keygen $KEY_NAME peer$i/config/keys "WprD3WiAi5S6Z1BSDlvUkhBVhcBiaxf"$i
    else
        cd peer$i/bin/
        expect -c "
        set timeout 10
        spawn ./keygen.sh -n $KEY_NAME
        expect \"*Input password:\"
        send \"1\r\"
        expect \"*input y or n *\"
        send \"y\r\"
        expect eof
        "
        cd $HOME
    fi

    IDs[$i]=$i
    PUBKs[$i]=$(cat peer$i/config/keys/$KEY_NAME.pub)
    PRIVs[$i]=$(cat peer$i/config/keys/$KEY_NAME.priv)
    PWDs[$i]=$(cat peer$i/config/keys/$KEY_NAME.pwd)

    let "i++"
done

# init peer-startup.sh
i=0
while(( $i<$NODE_SIZE ))
do
    sed -ri "s#7080#$PEER_PORT#g" peer$i/bin/peer-startup.sh
    let "PEER_PORT++"
    let "i++"
done

####################################### init gateway
unzip -oq jdchain-gateway-* -d gw
chmod 777 gw/bin/*

sed -ri "s#peer.port=7080#peer.port=$((PEER_PORT-NODE_SIZE))#g" gw/config/gateway.conf
sed -ri "s#keys.default.pubkey=#keys.default.pubkey=${PUBKs[0]}#g" gw/config/gateway.conf
sed -ri "s#keys.default.privkey=#keys.default.privkey=${PRIVs[0]}#g" gw/config/gateway.conf
sed -ri "s#keys.default.privkey-password=#keys.default.privkey-password=${PWDs[0]}#g" gw/config/gateway.conf


###################################### generate start and shutdown files
echo "#!/bin/bash" > start.sh
i=0
while(( $i<$NODE_SIZE ))
do
    echo "
nohup ./peer$i/bin/peer-startup.sh & " >> start.sh
    chmod 777 start.sh
    let "i++"
done
echo "
sleep 20

nohup ./gw/bin/startup.sh &" >> start.sh

echo "#!/bin/bash

ps -ef|grep '$HOME'|grep -v grep|cut -c 9-15|xargs kill -9
" > shutdown.sh
chmod 777 shutdown.sh


###################################### ledger init
k=0
while(( $k<$LEDGER_SIZE ))
do
    echo "初始化账本 "$k
    seed=`date +%s%N | md5sum |cut -c 1-32`
    i=0
    while(( $i<$NODE_SIZE ))
    do
        #### init local.conf
        sed -ri "s/local.parti.id(.*)/local.parti.id=$i/g" peer$i/config/init/local.conf
        sed -ri "s/local.parti.pubkey(.*)/local.parti.pubkey=${PUBKs[$i]}/g" peer$i/config/init/local.conf
        sed -ri "s/local.parti.privkey(.*)/local.parti.privkey=${PRIVs[$i]}/g" peer$i/config/init/local.conf
        sed -ri "s/local.parti.pwd(.*)/local.parti.pwd=${PWDs[$i]}/g" peer$i/config/init/local.conf
    
        if [ -z $DB_URI ]
        then
            sed -ri "s#ledger.db.uri(.*)#ledger.db.uri=rocksdb://$HOME/peer$i/rocksdb$k#g" peer$i/config/init/local.conf
        else
            sed -ri "s#ledger.db.uri(.*)#ledger.db.uri=$DB_URI/$i#g" peer$i/config/init/local.conf
        fi
    
        sed -ri "s#ledger.db.pwd(.*)#ledger.db.pwd=$DB_PWD#g" peer$i/config/init/local.conf
    
        #### init ledger.init
        echo "
ledger.seed=$seed
ledger.name=ledger$k
created-time=$(date +"%Y-%m-%d %H:%M:%S").000+0800
consensus.service-provider=com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider
consensus.conf=$HOME/peer$i/config/init/bftsmart.config
crypto.service-providers=com.jd.blockchain.crypto.service.classic.ClassicCryptoService, com.jd.blockchain.crypto.service.sm.SMCryptoService
crypto.verify-hash=true
crypto.hash-algorithm=SHA256
cons_parti.count=$NODE_SIZE" > peer$i/config/init/ledger.init
        j=0
        port=11010
        while(( $j<$NODE_SIZE ))
        do
            echo "
cons_parti.$j.name=$j
cons_parti.$j.pubkey=${PUBKs[$j]}
cons_parti.$j.initializer.host=127.0.0.1
cons_parti.$j.initializer.port=$port
cons_parti.$j.initializer.secure=false" >> peer$i/config/init/ledger.init
            let "j++"
            let "port++"
        done
    
        #### init bftsmart.config
        echo "" > peer$i/config/init/bftsmart.config
        j=0
        port=$((CONSUS_PORT+k*100))
        while(( $j<$NODE_SIZE ))
        do
            echo "
system.server.$j.network.host=127.0.0.1
system.server.$j.network.port=$port
system.server.$j.network.secure=false" >> peer$i/config/init/bftsmart.config
            let "j++"
            let "port++"
            let "port++"
        done
        echo "
system.communication.useSenderThread = true
system.communication.defaultkeys = true
system.servers.num = $NODE_SIZE
system.servers.f = $FAULT_SIZE
system.totalordermulticast.timeout = 60000
system.totalordermulticast.timeTolerance = 3000000
system.totalordermulticast.maxbatchsize = 2000
system.totalordermulticast.nonces = 10
system.totalordermulticast.verifyTimestamps = false
system.communication.inQueueSize = 500000
system.communication.outQueueSize = 500000
system.communication.send.retryInterval = 2000
system.communication.send.retryCount = 100
system.communication.useSignatures = 0
system.communication.useMACs = 1
system.debug = 0
system.shutdownhook = true
system.totalordermulticast.state_transfer = true
system.totalordermulticast.highMark = 10000
system.totalordermulticast.revival_highMark = 10
system.totalordermulticast.timeout_highMark = 200
system.totalordermulticast.log = true
system.totalordermulticast.log_parallel = false
system.totalordermulticast.log_to_disk = true
system.totalordermulticast.sync_log = false
system.totalordermulticast.checkpoint_period = 1000
system.totalordermulticast.global_checkpoint_period = 120000
system.totalordermulticast.checkpoint_to_disk = false
system.totalordermulticast.sync_ckp = false
system.initial.view = $( IFS=$','; echo "${IDs[*]}" )
system.ttp.id = 2001
system.bft = true" >> peer$i/config/init/bftsmart.config
    
        let "i++"
    done
    
    echo "" > nohup.out

    ### start ledger init
    i=0
    while(( $i<$NODE_SIZE ))
    do
        nohup expect -c "
        set timeout 180
        spawn peer$i/bin/ledger-init.sh
        expect \"*Any key to continue...*\"
        send \"1\r\"
        expect \"*Press any key to quit. *\"
        send \"quit\r\"
        expect eof
        " &
        let "i++"
    done
    
    tail -f nohup.out| while read line
    nSize=0
    do
        if [[ $line =~ "Update Ledger binding configuration success!" ]]
        then
            let "nSize++"
            if [[ $line == $NODE_SIZE ]]
            then
                echo -e ".\c"
            else
                echo ""
                echo "账本 "$k" 初始化完成"
                break
            fi
        else
            echo -e ".\c"
        fi
    done

    let "k++"
    sleep 1
done
```