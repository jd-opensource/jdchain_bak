## SDK

`JD Chain`提供了`Java`和`Go`版本的`SDK`，此处以`Java`为例，`Go`版本参照[framework-go](https://github.com/blockchain-jd-com/framework-go)

`SDK`可执行示例代码参照[JD Chain Samples](https://github.com/blockchain-jd-com/jdchain/tree/master/samples)

### 1. 连接网关

通过`GatewayServiceFactory`中静态方法连接网关：
```java
static GatewayServiceFactory connect(NetworkAddress gatewayAddress)

static GatewayServiceFactory connect(NetworkAddress gatewayAddress, BlockchainKeypair userKey)

static GatewayServiceFactory connect(String gatewayHost, int gatewayPort, boolean secure)

static GatewayServiceFactory connect(String gatewayHost, int gatewayPort, boolean secure, BlockchainKeypair userKey)
```

其中`BlockchainKeypair userKey`参数用于自动签署终端用户签名，可不传，不传`userKey`时需要在提交交易前调用签名操作，添加终端账户签名信息。

通过：
```java
BlockchainService blockchainService = GatewayServiceFactory.connect(gatewayHost, gatewayPort, false).getBlockchainService();
```
创建区块连服务，常规情况`BlockchainService`**单例**即可。

### 2. 调用过程

#### 2.1 操作

1. 新建交易：
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
```
2. 操作：
```java
BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
// 注册用户
txTemp.users().register(user.getIdentity());
```
> 一笔交易中可以包含多个操作，所有操作类型参照[交易](transaction.md)文档。
3. 准备交易
```java
PreparedTransaction ptx = txTemp.prepare();
```
4. 终端用户签名
```java
ptx.sign(userKey);
```
> 若[连接网关](#1-连接网关)中传入了用户身份信息，且用户身份具备交易中包含的所有操作[权限](user.md)，此处签名操作可省略。
5. 提交交易
```java
TransactionResponse response = ptx.commit();
```

#### 2.2 查询

`BlockchainService`中包含了所有链上数据的查询方法，直接使用即可：
```java
LedgerInfo ledgerInfo = blockchainService.getLedger(ledger);
```

### 3. 操作类型

按功能模块划分：`用户操作`，`数据账户操作`，`事件操作`，`合约操作`，`查询`。

#### 3.1 用户操作

用户/角色/权限相关说明参照[用户](user.md)文档说明。

1. 注册用户
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 生成用户
BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
System.out.println("用户地址：" + user.getAddress());
// 注册用户
txTemp.users().register(user.getIdentity());

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

2. 角色赋权
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 创建角色 MANAGER ，并设置可以写数据账户，能执行交易
txTemp.security().roles().configure("MANAGER")
        .enable(LedgerPermission.WRITE_DATA_ACCOUNT)
        .enable(TransactionPermission.DIRECT_OPERATION);

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

3. 用户赋权
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 赋予用户 user MANAGER 角色，取消 ADMIN 角色，设置多角色策略策略为合并策略
txTemp.security().authorziations().forUser(user.getAddress()).authorize("MANAGER").unauthorize("ADMIN").setPolicy(RolesPolicy.UNION);

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

#### 3.2 数据账户操作

1. 创建数据账户
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 生成数据账户
BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
System.out.println("数据账户地址：" + dataAccount.getAddress());
// 注册数据账户
txTemp.dataAccounts().register(dataAccount.getIdentity());

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

2. 写KV
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// expVersion是针对此key的插入更新操作次数严格递增，初始为-1
txTemp.dataAccount(Bytes.fromBase58("LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn")).setInt64("key2", 1024, -1);

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

#### 3.3 事件操作

1. 创建事件账户
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 生成事件账户
BlockchainKeypair eventAccount = BlockchainKeyGenerator.getInstance().generate();
System.out.println("事件账户地址：" + eventAccount.getAddress());
// 注册事件账户
txTemp.eventAccounts().register(eventAccount.getIdentity());

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

2. 发布事件
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// sequence是针对此消息name的序列严格递增，初始为-1，可通过查询事件名下最新事件获取序列参数
txTemp.eventAccount(Bytes.fromBase58("LdeNiAPuZ5tpYZVrrbELJNjqdvB51PBpNd8QA")).publish("topic", "content", -1);

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

3. 监听事件
```java
// 监听系统事件，目前仅有新区快产生事件
blockchainService.monitorSystemEvent(ledger,
        SystemEvent.NEW_BLOCK_CREATED, 0, (eventMessages, eventContext) -> {
            for (Event eventMessage : eventMessages) {
                // content中存放的是当前链上最新高度
                System.out.println("New block:" + eventMessage.getSequence() + ":" + BytesUtils.toLong(eventMessage.getContent().getBytes().toBytes()));
            }
        });

// 监听用户自定义事件
blockchainService.monitorUserEvent(ledger, "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", "sample-event", 0, (eventMessage, eventContext) -> {

    BytesValue content = eventMessage.getContent();
    switch (content.getType()) {
        case TEXT:
        case XML:
        case JSON:
            System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + content.getBytes().toUTF8String());
            break;
        case INT64:
        case TIMESTAMP:
            System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + BytesUtils.toLong(content.getBytes().toBytes()));
            break;
        default: // byte[], Bytes
            System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + content.getBytes().toBase58());
            break;
    }
});
```

#### 3.4 合约操作

1. 部署合约
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 生成合约账户
BlockchainKeypair contractAccount = BlockchainKeyGenerator.getInstance().generate();
System.out.println("合约地址：" + contractAccount.getAddress());
// 部署合约
txTemp.contracts().deploy(contractAccount.getIdentity(), FileUtils.readBytes("src/main/resources/contract-samples-1.4.2.RELEASE.car"));

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

2. 升级合约
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 解析已存在的合约身份信息
BlockchainIdentity contractIdentity = new BlockchainIdentityData(KeyGenUtils.decodePubKey("7VeRCfSaoBW3uRuvTqVb26PYTNwvQ1iZ5HBY92YKpEVN7Qht"));
System.out.println("合约地址：" + contractIdentity.getAddress());
// 部署合约
txTemp.contracts().deploy(contractIdentity, FileUtils.readBytes("src/main/resources/contract-samples-1.4.2.RELEASE.car"));

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
```

3. 调用合约

基于动态代理方式合约调用，需要依赖合约接口：
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 一次交易中可调用多个（多次调用）合约方法
// 调用合约的 registerUser 方法
SampleContract sampleContract = txTemp.contract("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", SampleContract.class);
GenericValueHolder<String> userAddress = ContractReturnValue.decode(sampleContract.registerUser(UUID.randomUUID().toString()));

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
Assert.assertTrue(response.isSuccess());

// 获取返回值
System.out.println(userAddress.get());
```

非动态代理方式合约调用，不需要依赖合约接口及实现：
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

ContractEventSendOperationBuilder builder = txTemp.contract();
// 一次交易中可调用多个（多次调用）合约方法
// 调用合约的 registerUser 方法，传入合约地址，合约方法名，合约方法参数列表
builder.send("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", "registerUser",
        new BytesDataList(new TypedValue[]{
                TypedValue.fromText(UUID.randomUUID().toString())
        })
);

PreparedTransaction ptx = txTemp.prepare();
ptx.sign(adminKey);
TransactionResponse response = ptx.commit();
Assert.assertTrue(response.isSuccess());

// 解析合约方法调用返回值
for (int i = 0; i < response.getOperationResults().length; i++) {
    BytesValue content = response.getOperationResults()[i].getResult();
    switch (content.getType()) {
        case TEXT:
            System.out.println(content.getBytes().toUTF8String());
            break;
        case INT64:
            System.out.println(BytesUtils.toLong(content.getBytes().toBytes()));
            break;
        case BOOLEAN:
            System.out.println(BytesUtils.toBoolean(content.getBytes().toBytes()[0]));
            break;
        default: // byte[], Bytes
            System.out.println(content.getBytes().toBase58());
            break;
    }
}
```

#### 3.5 查询

与[网关 API](api.md)所提供查询一致，参照[Query Samples](https://github.com/blockchain-jd-com/jdchain/blob/master/samples/sdk-samples/src/test/java/com/jdchain/samples/sdk/QuerySample.java)