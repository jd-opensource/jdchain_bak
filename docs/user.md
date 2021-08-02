## 用户

`JD Chain`实现了基于角色和权限的用户账户管理体系。

### 1. 用户

可类比传统数据库的用户概念，`JD Chain`用户是接入`JD Chain`网络的必要身份，本质上由一对公私钥对标识，公钥和地址信息记录在账本用户数据集中。

### 2. 角色

角色名称不区分大小写，最长不超过20个字符，多个角色名称之间用半角的逗点`,`分隔

系统会预置一个默认角色`DEFAULT`，所有未指定角色的用户都以赋予该角色的权限，若初始化时未配置默认角色的权限，则为默认角色分配所有权限；

#### 2.1 多角色策略

表示如何处理一个对象被赋予多个角色时的综合权限，在`RolesPolicy`枚举中定义：
```java
public enum RolesPolicy {

	// 合并权限，综合权限是所有角色权限的并集，即任何一个角色的权限都被继承
	UNION((byte) 0),

	// 交叉权限，综合权限是所有角色权限的交集，即只有全部角色共同拥有的权限才会被继承
	INTERSECT((byte) 1);
}
```

### 3. 权限

`JD Chain`权限设计分为两类：账本权限，交易权限。

#### 3.1 账本权限

账本相关的权限，这些权限属于全局性的

- `CONFIGURE_ROLES`配置角色
- `AUTHORIZE_USER_ROLES`授权用户角色
- SET_CONSENSUS 设置共识协议
- SET_CRYPTO 设置密码体系
- `APPROVE_TX`参与方核准交易，如果不具备此项权限，则无法作为节点签署由终端提交的交易
- `CONSENSUS_TX`参与方共识交易
- `REGISTER_PARTICIPANT`注册参与方
- SET_USER_ATTRIBUTES 设置用户属性
- `REGISTER_USER`注册用户
- `REGISTER_EVENT_ACCOUNT`注册事件账户
- `WRITE_EVENT_ACCOUNT`发布事件
- `REGISTER_DATA_ACCOUNT`注册数据账户
- `WRITE_DATA_ACCOUNT`写入数据账户
- `REGISTER_CONTRACT`注册合约
- `UPGRADE_CONTRACT`升级合约

#### 3.2 交易权限

一个用户可以发起的交易类型

- `DIRECT_OPERATION`交易中包含指令操作
- `CONTRACT_OPERATION`交易中包含合约操作

### 4. 控制逻辑

`JD Chain`[交易](transaction.md)执行前会验证交易的签名信息，签名主要包含**节点签名**和**终端用户签名**。

#### 4.1 节点身份验证

> 网关提交交易前，会使用网关配置文件中配置的公私钥信息所代表的节点用户，自动添加签名到节点签名列表中。

`JD Chain`运行时网络执行交易前，要求节点签名用户至少有一个具有`LedgerPermission.APPROVE_TX`权限。

#### 4.2 终端用户验证

提交交易前，要求添加终端用户签名信息。

执行到具体操作前会校验相应账本/交易权限，策略都是至少有一个终端用户包含操作权限。

例如：创建用户账户操作执行前会校验所有终端用户签名中的用户**至少有一个**包含`LedgerPermission.REGISTER_USER`权限。

### 5. SDK

可在`ledger.init`中配置好角色权限，在组网成功后所配置的角色和权限信息会写入到相关账本中。
也可以通过以下相关`SDK`代码向运行中`JD Chain`网络执行用户相关操作：

#### 5.1 注册用户

默认使用`ed25519`编码

创建公私钥对：

```java
BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
```

从已存在的公私钥恢复：

```java
PubKey pubKey = KeyGenUtils.decodePubKey("7VeRLdGtSz1Y91gjLTqEdnkotzUfaAqdap3xw6fQ1yKHkvVq");
PrivKey privKey = KeyGenUtils.decodePrivKey("177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x", "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY");
BlockchainKeypair user = new BlockchainKeypair(pubKey, privKey);
```

注册用户：

```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
txTemp.users().register(user.getIdentity());
```

#### 5.2 创建角色

```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 创建角色 MANAGER ，并设置可以写数据账户，能执行交易
txTemp.security().roles().configure("MANAGER")
        .enable(LedgerPermission.WRITE_DATA_ACCOUNT)
        .disable(LedgerPermission.REGISTER_USER)
        .enable(TransactionPermission.DIRECT_OPERATION);
```

#### 5.3 用户赋权

```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

// 给用户设置 MANAGER 角色权限
txTemp.security().authorziations().forUser(Bytes.fromBase58("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye")).authorize("MANAGER").unauthorize("DEFAULT").setPolicy(RolesPolicy.UNION);
// 或者
txTemp.security().authorziations().forUser(user.getIdentity()).authorize("MANAGER").unauthorize("DEFAULT").setPolicy(RolesPolicy.UNION);
```