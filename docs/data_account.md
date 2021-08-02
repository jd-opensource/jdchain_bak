## 数据账户

`JD Chain`存放`KV`数据的数据结构。

### 1. 基本概念

可类比传统数据库的表的概念，上层应用需要保存的应用数据最终都应表现为`KV`类型数据写入到数据账户中。

写入`KV`数据之前，需要创建或使用已存在数据账户。

一个账本可以创建无限多个数据账户，一个数据账户中可以写入无限多个`KV`数据。

`KV`数据有`Version`（数据版本）的概念，以`KEY`作为唯一标识，`VALUE`的更新写入需要提供当前该`KEY`的最高版本，`KEY`不存在时为`-1`。

### 2. SDK

以下只描述主要步骤，完整示例代码可参照[JD Chain Samples](samples.md)数据账户部分。

#### 2.1 注册数据账户

创建数据账户：
```java
BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
System.out.println("数据账户地址：" + dataAccount.getAddress());
// 注册数据账户
txTemp.dataAccounts().register(dataAccount.getIdentity());
```

从已存在数据账户公钥恢复：
```java
PubKey pubKey = KeyGenUtils.decodePubKey("7VeRLdGtSz1Y91gjLTqEdnkotzUfaAqdap3xw6fQ1yKHkvVq");
BlockchainIdentity dataAccountIdentity = new BlockchainIdentityData(pubKey);
System.out.println("数据账户地址：" + dataAccountIdentity.getAddress());
// 注册数据账户
txTemp.dataAccounts().register(dataAccountIdentity);
```

#### 2.2 写入数据

```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

txTemp.dataAccount("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye")
        .setText("key1", "value1", -1)
        .setText("key1", "value1", 0)
        .setInt64("key2", 1, -1)
        .setJSON("key3", "{}", -1)
        .setBytes("key4", Bytes.fromInt(2), -1);
```

支持写入数据类型：

- `setText`，字符类型
- `setInt64`，长整型
- `setJSON`，`JSON`串
- `setBytes`，字节数组
- `setTimestamp`，时间戳，长整型
- `setXML`，`XML`文本
- `setImage`，图片字节数据

> 本质上仅支持`String/Long/[]byte`这三种数据类型，`JSON/XML/Image/Timestamp`等起标识作用，用于扩展差异化数据展示等场景需求

`setText("key1", "value1", -1)`中第三个参数即为数据版本，需要传入`JD Chain`网络中当前`key1`的最高数据版本，首次写入时传入`-1`。