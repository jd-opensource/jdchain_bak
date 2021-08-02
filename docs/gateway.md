## 网关服务

`JD Chain`的网关服务是应用的接入层。
终端接入是`JD Chain`网关的基本功能，在确认终端身份的同时提供连接节点、转发消息和隔离共识节点与客户端等服务。网关确认客户端的合法身份，接收并验证交易；网关根据初始配置文件与对应的共识节点建立连接，并转发交易数据。

### 1. 配置

网关配置在文件`gateway.conf`中：

```properties
#网关的HTTP服务地址；
http.host=0.0.0.0
#网关的HTTP服务端口；
http.port=8080
#网关的HTTP服务上下文路径，可选；
#http.context-path=

#共识节点的服务地址（与该网关节点连接的Peer节点的IP地址）；
peer.host=127.0.0.1
#共识节点的服务端口（与该网关节点连接的Peer节点的端口，即在Peer节点的peer-startup.sh中定义的端口）；
peer.port=7080
#共识节点的服务是否启用安全证书；
peer.secure=false

#是否存储共识拓扑信息，网关重启后若存储存在有效的拓扑信息可快速建立与拓扑中所有节点的连接
topology.store=false

#共识节点的服务提供解析器
#BftSmart共识Provider：com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider
#简单消息共识Provider：com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider
peer.providers=com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider

#数据检索服务对应URL，格式：http://{ip}:{port}，例如：http://127.0.0.1:10001
#若该值不配置或配置不正确，则浏览器模糊查询部分无法正常显示
data.retrieval.url=
schema.retrieval.url=

#默认公钥的内容（Base58编码数据）；
keys.default.pubkey=
#默认私钥的路径；在 pk-path 和 pk 之间必须设置其一；
keys.default.privkey-path=
#默认私钥的内容（加密的Base58编码数据）；在 pk-path 和 pk 之间必须设置其一；
keys.default.privkey=
#默认私钥的解码密码；
keys.default.privkey-password=

```

其中：

- `data.retrieval.url`与`schema.retrieval.url`分别与[Argus（高级检索）](https://github.com/blockchain-jd-com/jdchain-indexer)中的[区块链基础数据检索服务](https://github.com/blockchain-jd-com/jdchain-indexer#%E5%90%AF%E5%8A%A8%E5%8C%BA%E5%9D%97%E9%93%BE%E5%9F%BA%E7%A1%80%E6%95%B0%E6%8D%AE%E7%B4%A2%E5%BC%95%E6%A3%80%E7%B4%A2%E6%9C%8D%E5%8A%A1)和[`Schema`服务](https://github.com/blockchain-jd-com/jdchain-indexer#%E5%90%AF%E5%8A%A8value%E7%B4%A2%E5%BC%95%E6%9C%8D%E5%8A%A1)相对应，只有启动并正确配置了`Argus`，`JD Chain`浏览器才能正常使用搜索功能。

- `keys.default`几个参数代表接入`JD Chain`网络的身份信息，只有处于非停用（`DEACTIVATED`）状态的参与方身份才可作为此处的配置。