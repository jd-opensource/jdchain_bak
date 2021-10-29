## 证书

`JD Chain`身份认证支持两种模式：`KEYPAIR`(默认)/`CA`，即公私钥对和证书。

证书模式采用`X.509`标准的数字证书作为用户标识，证书字段中，附加组织和角色等信息。

`JD Chain`使用`jdchain-cli`/`openssl`生成的自签名证书，也支持使用`CFCA`等国家认可的第三方`CA`颁发的外部证书。

`JD Chain` `CA`支持`RSA 2048`/`ECDSA P-256`/`SM2 SM3WithSM2`/`ED25519`四种签名算法。


### 类别

`JD Chain`证书体系分`ROOT`，`CA`，`PEER`，`GW`，`USER`几个类别。

使用证书`Subject`中`OU`字段区分。

#### ROOT

根证书，可用于签发证书及账本初始化时作为账本证书。

#### CA

中间证书，可用于签发证书及账本初始化时作为账本证书。

#### PEER

共识节点证书，注册参与方时需要提供`PEER`类型证书。

#### GW

网关证书，网关所配置公私钥对应账户信息在链上必须存储有`GW`类型的证书。

#### USER

普通用户证书

### 实现

`JD Chain`证书使用链上存储方式。主要存储于两个地方：
- 元数据区，存储账本初始化时配置的根证书列表
- 用户账户头部，存储用户注册时提供的证书

> 根证书支持列表，即支持多个参与机构使用不同的证书链，且根证书可使用`ROOT`证书，也可使用`CA`（中间）证书，但节点/网关/用户证书必须由配置在`JD Chain`根证书列表中的证书直接签出。

#### 账本初始化

*`ledger.init`*
较`KEYPAIR`模式有如下修改：

1. `identity-mode`
```properties
identity-mode=CA
```
`identity-mode`身份认证模式，`KEYPAIR`（默认）/`CA`

2. `root-ca-path`
```properties
root-ca-path=/**/ledger1.crt,/**/ledger2.crt
```
`root-ca-path`根证书列表，使用`ROOT`或者`CA`类型证书，多个根证书使用半角逗号分割。初始化完成后，证书信息会上链存储，通过[2.7 获取账本初始化配置信息](api.md#27-获取账本初始化配置信息)可查。

3. `cons_parti.*.ca-path`

**CA 模式参与方需要增加配置网关信息，网关节点IP和端口不需要填写**

节点公钥配置改为证书地址：

```properties
// KEYPAIR
// cons_parti.0.pubkey-path=
// cons_parti.0.pubkey=

// CA
cons_parti.0.ca-path=/**/peer0.crt
```

*`local.conf`*
较`KEYPAIR`模式有如下修改：
```properties
#当前参与方的公钥，用于非证书模式
# local.parti.pubkey=
#当前参与方的证书信息，用于证书模式
local.parti.ca-path=

#当前参与方的私钥文件，PEM格式,用于证书模式
local.parti.privkey-path=
```

#### 节点运行

节点启动和运行时会校验证书类型，时间有效性以及是否由某个根证书签出等，一旦校验失败会阻止网关接入，不再对外服务。

#### 网关接入

*gateway.conf*
```properties
#默认公钥的内容（Base58编码数据），非CA模式下必填；
keys.default.pubkey=
#默认网关证书路径（X509,PEM），CA模式下必填；
keys.default.ca-path=/home/imuge/jd/nodes/peer0/config/keys/gw1.crt
#默认私钥的路径；在 pk-path 和 pk 之间必须设置其一；
keys.default.privkey-path=/home/imuge/jd/nodes/peer0/config/keys/gw1.key
#默认私钥的内容（加密的Base58编码数据）；在 pk-path 和 pk 之间必须设置其一；
keys.default.privkey=
```

网关接入网络需要配置`GW`类型证书及对应的私钥信息，证书类型必须是`GW`。

网关接入时会做如下认证：
- 证书类型包含`GW`
- 根证书列表存在类型正确且有效证书
- 网关证书由根证书列表中某个证书签出（此证书类型正确且有效）

#### 交易认证

交易时使用证书持有者私钥签名，交易内容不包含签名用户证书信息。
交易执行前会校验所有账本根证书，签名终端用户和节点用户的证书类型及有效性。

> 请务必在证书到期前更新证书有效期

#### 证书更新

1. 根证书

`SDK`方式：
```java
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
txTemp.metaInfo().ca(X509Utils.resolveCertificate("*.crt"));
```

命令行方式：[更新账本根证书](cli/tx.md#更新账本根证书)

2. 节点/网关/普通用户证书

> 在`JD Chain`中，共识节点，网关配置的接入账户和普通用户本质都是用户账户类型，它们对应的证书管理方式一致。

`SDK`方式：

```java
txTemp.user("user address").ca(X509Utils.resolveCertificate("*.crt"));
```

命令行方式：[更新用户证书](cli/tx.md#更新用户证书)

### 证书生成

使用`jdchain-cli`提供的[keys](cli/keys.md)和[ca](cli/ca.md)指令工具创建公私钥对以及签发证书。

其中[ca-test](cli/ca.md#生成测试证书)可一键生成账本初始化所需的所有证书外加可用的普通用户证书。