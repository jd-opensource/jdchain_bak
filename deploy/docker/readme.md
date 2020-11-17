# jdchain-demo镜像使用说明
本镜像主要为快速构建JDChain测试环境使用，内嵌固定的公私钥，不可用于生产正式环境。  
JDChain在docker中的安装路径：/export/jdchain，网关对外端口为：8080。可通过docker-compose-all文件来修改端口。  
demo环境构建完成后执行sdk加载部分测试数据，区块高度：7，交易总数：8，用户总数：5，数据账户总数：2，合约总数：1。  

## 如何生成镜像
1. 如果构建的docker镜像为当前开发版本，将docker模块中的<version>和<docker-tag>跟主版本对齐，然后在deploy模块执行：mvn clean package即可。
如果镜像版本与所在开发版本不一致（举例说明：构建1.3.0的镜像版本，但当前开发版本是1.4.0），需要预先在deploy-peer和deploy-gateway的
target文件夹下放置相应版本zip安装包(jdchain-peer-xxx.zip,jdchain-gateway-xxx.zip)，然后在docker模块执行：mvn clean package。
2. 在maven构建过程中，两个zip安装包和docker-sdk-xxx.jar，会放至docker-demo模块src/main/docker/zip文件夹下。
3. maven构建完成后，控制台执行：docker images，可看到构建的jdchain-peer镜像。
4. 生成镜像文件。执行docker-demo模块中src/main/resources/zip.sh，可生成镜像的tar.gz压缩包；

## 镜像快速使用
1.在已经安装docker工具的环境中，装入jdchain-demo镜像：
````
docker load -i jdchain-demo_1.3.0.tar.gz
````
2.启动脚本
每次执行启动脚本时，会删除原有的容器，然后重新构建全新的容器。  
所以每次执行之后，会清除原先链上新增的区块。
````
sh start-net.sh
````
3.卸载容器
如果不再使用容器，在start-net.sh脚本所在路径下执行：
````
docker-compose -f docker-compose-all.yaml down
````

## SDK连接网关参数
````
ip=localhost
port=8080
#默认公钥的内容（Base58编码数据）；
keys.default.pubkey=3snPdw7i7PisoLpqqtETdqzQeKVjQReP2Eid9wYK67q9z6trvByGZs
#默认私钥的内容（加密的Base58编码数据）；在 pk-path 和 pk 之间必须设置其一；
keys.default.privkey=177gk2PbxhHeEdfAAqGfShJQyeV4XvGsJ9CvJFUbToBqwW1YJd5obicySE1St6SvPPaRrUP
#默认私钥的解码密码；
keys.default.privkey-password=8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG
````



