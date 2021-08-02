## 共识节点变更

借助`BFT-SMaRT`共识提供的`Reconfig`操作元语，`JD Chain`实现了在不停机的情况下快速更新共识网络拓扑，实现[添加共识节点](#1添加共识节点)，[移除共识节点](#2移除共识节点)，[更新共识信息](#3更新共识信息) 等功能。

**共识节点相关操作错误极容易导致整个网络不可用，甚至无法恢复，操作前请做好数据备份，务必谨慎操作，确保所有环境和指令正确**

以下操作说明均以在部署好的如下单机四节点环境操作为例：
- `ledger` `j5m4yF1uyxaMwwBWKaqJqyHkKViXs8LGe9ChWvPs1CqdjP`
- `peer0` 启动端口 `7080`，共识端口`10080`
- `peer1` 启动端口 `7081`，共识端口`10081`
- `peer2` 启动端口 `7082`，共识端口`10082`
- `peer3` 启动端口 `7083`，共识端口`10083`
- `网关` 服务端口 `8080`

### 1.添加共识节点

#### 1.1 生成身份信息

解压`peer`的`zip`包作为新的参与方节点`peer4`。使用`bin`目录下`keygen.sh`脚本生成公私钥信息：
```bash
$ ./keygen.sh -n new-node
# 输入私钥密码
Input password:
# 是否保存Base58编码后的私钥密码信息
Do you want to save encode password to file? Please input y or n ...y
```
执行完成后会在`peer4`中`config/keys`目录下生成`new-node.priv`/`new-node.pub`/`new-node.pwd`文件，分别保存公钥/私钥/私钥密码信息，用作新增节点的身份信息。

#### 1.2 注册新节点

`peer4`中`bin`目录下提供了`reg-parti.sh`注册参与方脚本：

```bash
./reg-parti.sh -ledger <账本HASH> -pub <新节点公钥> -priv <新节点私钥> -pass <新节点私钥密码> -name <新节点名称> -existpub <链上已存在用户公钥> -existpriv <链上已存在用户私钥> -existpass <链上已存在用户私钥密码> -host <网关IP> -port <网关端口>
```

参数：
- `ledger`，指定要注册新参与方的 用户手册账本，`Base58`编码的字符串，必填
- `pub`，步骤1中产生的用户公钥信息，`Base58`编码的字符串，必填
- `priv`，步骤1中产生的用户私钥信息，`Base58`编码的字符串，必填
- `pass`，步骤1中产生的用户私钥密码， `Base58`编码的字符串，必填
- `name`，新参与方的名字，字符串，必填
- `existpub`，账本中已注册的任意用户的公钥信息，`Base58`编码的字符串，必填
- `existpriv`，账本中已注册的任意用户的私钥信息，`Base58`编码的字符串，用于对客户端提交的交易进行终端签名，必填
- `existpass`，账本中已注册的任意用户的私钥密码信息，`Base58`编码的字符串，必填
- `host`，客户端接入网关`IP`，必填
- `port`，客户端接入网关`Port`，必填
- `debug`，开启`DEBUG`模式 ，默认为`false`，可选

示例：
```bash
./reg-parti.sh -ledger j5m4yF1uyxaMwwBWKaqJqyHkKViXs8LGe9ChWvPs1CqdjP -pub 3snPdw7i7PjLAfhTx22uU4C9oGRFpHAcFkt3wHNVSuQgodE616H45H -priv 177gjyyCaYGiTvjRQbxAZwUDc8SjriRwgWwAwZLuzCvuixJ9AbcZx2FWTMP1XoUigRqUJmD -pass 8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG -name 4 -existpub 3snPdw7i7PjWHy3hV5yw7JqMKfNqZdotSNzqJJHpnKmLCeJMd383jd -existpriv 177gjzjdwHqADjfC6yLqECZcEK8HPM8GAvjsQZrDiPNyLUGgqQPQ8zqjFBcV6TsKitmfFFV -existpass 8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG -host 127.0.0.1 -port 8080
```

执行成功后将打印成功信息，并显示`peer4`的用户地址信息。

#### 1.3 确定复制节点

查询每个共识节点的账本信息：
```bash
curl http://<ip>:<port>/ledgers/<ledgerHash>
```
> 其中`ip`和`port`为各个`peer`的`IP`地址和启动端口

选出具有最新区块数据的共识节点作为复制节点。

#### 1.4 构建新节点

修改`peer4`目录下以下文件：
1. `config/ledger-binding.conf`
```bash
# Base58编码的账本哈希
ledger.bindings=<账本hash>
# 账本名字，与账本相关的其他其他peer保持一致
binding.<账本HASH>.name=<节点名称>
# peer4的名名称，与[向现有共识网络注册新的参与方]操作中保持一致
binding.<账本hash>.parti.name=<节点名称>
# peer4的用户地址
binding.<账本hash>.parti.address=<peer4的用户地址>
# 新参与方base58编码的私钥，与[向现有共识网络注册新的参与方]操作中保持一致
binding.<账本hash>.parti.pk=<新参与方base58编码的私钥>
# 新参与方base58编码的私钥读取密码，与[向现有共识网络注册新的参与方]操作中保持一致
binding.<账本hash>.parti.pwd=<新参与方base58编码的私钥读取密码>
# 新参与方对应的账本数据库连接uri，即peer4的账本数据库存放位置，参照其他peer修改，不可与其他peer混用
binding.<账本hash>.db.uri=<账本数据库连接>
```

2. `bin/peer-startup.sh`

对于脚本中`PROC_INFO`变量的`-p` 参数修改为`peer4`的`http`启动端口 
3. 账本数据复制
   

步骤[确定复制节点](#确定复制节点)中选出了复制节点，以`peer0`为例，从`peer0`的`ledger-binding.conf`文件可知`peer0`的账本数据库存放地址，如：`rocksdb:///home/jdchain/peer0/rocksdb`，复制此目录所有数据到`peer4` `config/ledger-binding.conf`中`binding.<账本hash>.db.uri`指定的位置

> **除了数据库目录，切不可将其他节点所有内容直接复制使用**，因为像`runtime`等目录中会保存节点差异化的相关运行数据。


####  1.5 启动新节点

> **一定注意在启动新参与方节点进程之前确保完成了账本数据库的复制工作**

执行`peer4`中`bin`目录下`peer-startup.sh`脚本启动启动新参与方`peer4`节点进程：
```bash
./peer-startup.sh
```


####  1.6 激活新节点

> **在进行新参与方节点的激活操作时，要求暂停向共识网络中发起新的业务数据**

`peer`包中`bin`目录下提供了`active-parti.sh`激活参与方脚本：

```bash
./active-parti.sh -ledger <账本哈希> -httphost <新节点IP> -httpport <新节点启动端口> -consensushost <新节点共识IP> -consensusport <新节点共识端口> -synchost <数据同步节点IP> -syncport <数据同步节点端口> -debug -shutdown
```

参数：
- `ledger`，指定要注册新参与方的账本，`Base58`编码的字符串，必填
- `httphost`，新参与方`IP`地址，必填
- `httpport`，新参与方启动端口，必填
- `consensushost`，新参与方的共识`IP`地址，必填（**务必确保该端口未被占用**）
- `synchost`，新参与方进行账本数据复制的节点`IP`地址，必填
- `syncport`, 新参与方进行账本数据复制的节点的启动端口,，必填
- `debug`，开启`DEBUG`模式 ，默认为`false`，可选

示例：
```bash
./active-parti.sh -ledger j5m4yF1uyxaMwwBWKaqJqyHkKViXs8LGe9ChWvPs1CqdjP -httphost 127.0.0.1 -httpport 7084 -consensushost 127.0.0.1 -consensusport 10088 -synchost 127.0.0.1 -syncport 7080
```


#### 1.7 查询节点状态

查询网关接口，获取节点状态信息：

```bash
curl http://<网关ip>:<网关port>/ledgers/<账本hash>/participants
```

节点状态为`CONSENSUS`，说明添加成功。



### 2.移除共识节点

> **在多于4个共识节点的共识网络中，才允许进行节点的移除操作**
> **在进行节点的移除操作时，要求暂停向共识网络中发起新的业务数据上链请求**


#### 2.1 确定复制节点

查询每个共识节点的账本信息：
```bash
curl http://<ip>:<port>/ledgers/<ledgerHash>
```
> 其中`ip`和`port`为各个`peer`的`IP`地址和启动端口

选出具有最新区块数据的共识节点作为复制节点。


#### 2.2 移除节点

`peer`包中`bin`目录下提供了`deactive-parti.sh`移除节点脚本：

```bash
./reg-parti.sh -ledger <账本HASH> -participantAddress <待移除节点地址> -httphost <待移除节点的http启动IP地址> -httpport <待移除节点的http启动Port> -synchost <数据同步节点IP> -syncport <数据同步节点端口> -debug
```

参数：

- `ledger`，指定要注册新参与方的账本，`Base58`编码的字符串，必填
- `participantAddress`, 要移除的参与方用户地址，`Base58`编码的字符串，必填；
- `httphost`，待移除参与方`IP`地址，必填
- `httpport`，待移除参与方启动端口，必填
- `synchost`，待移除参与方进行账本数据复制的节点`IP`地址，必填
- `syncport`，待移除参与方进行账本数据复制的节点的启动端口, 必填
- `debug`，开启`DEBUG`模式 ，默认为`false`，可选

示例：
```bash
./deactive-parti.sh -ledger j5m4yF1uyxaMwwBWKaqJqyHkKViXs8LGe9ChWvPs1CqdjP -participantAddress LdeNnjhSDx9nxE5hoyzzbU7paWQhP4MAfpYiC -httphost 127.0.0.1 -httpport 7084 -synchost 127.0.0.1 -syncport 7080
```
执行此操作可将`peer4`从该共识网络中移除，不再参与共识服务。

#### 2.3 查询节点状态

查询网关接口，获取参与方状态信息：

```bash
curl http://<网关ip>:<网关port>/ledgers/<账本hash>/participants
```

参与方状态为非`CONSENSUS`，说明操作成功。



### 3.更新共识信息

通过[激活节点](#6. 激活新节点)操作除了激活新增的节点外，还可以动态修改已经处于激活状态的共识节点的`IP`和`共识端口`信息，从而实现本机的共识端口变更，不同机器之间进行`账本迁移`。

> **在进行节点信息变更时，要求暂停向共识网络中发起新的业务数据上链请求**

#### 3.1 变更共识端口

> **操作前请确保变更到的端口未被占用**

如将`peer1`共识端口由`10082`修改为`10182`，操作指令如下：

```bash
./active-parti.sh -ledger j5m4yF1uyxaMwwBWKaqJqyHkKViXs8LGe9ChWvPs1CqdjP -httphost 127.0.0.1 -httpport 7081 -consensushost 127.0.0.1 -consensusport 10182 -synchost 127.0.0.1 -syncport 7080
```
指令成功执行后，`peer1`的共识端口将自动变更为`10182`。


#### 3.2 账本迁移

账本迁移指将一台机器（`IP`）上的共识节点迁移到另一台机器（`IP`）上，主要操作流程如下：

> **操作前请确保变更到的端口未被占用**

1. 修改共识信息

如将`peer2`中账本`j5m4yF1uyxaMwwBWKaqJqyHkKViXs8LGe9ChWvPs1CqdjP`的共识`IP`由`127.0.0.1`修改为`192.168.1.100`（另一台机器），操作指令如下：

```bash
./active-parti.sh -ledger j5m4yF1uyxaMwwBWKaqJqyHkKViXs8LGe9ChWvPs1CqdjP -httphost 127.0.0.1 -httpport 7082 -consensushost 192.168.1.100 -consensusport 10084 -synchost 127.0.0.1 -syncport 7080 -shutdown
```

**特别注意**：`-shutdown`为必填选项，否则将导致整个网络需要重启。

指令成功执行后，`127.0.0.1`上账本`j5m4yF1uyxaMwwBWKaqJqyHkKViXs8LGe9ChWvPs1CqdjP`节点将不再参与此账本的共识服务。

2. 迁移节点数据

拷贝步骤1中`127.0.0.1`上与移出账本相关的所有数据（包括`rocksdb`，`runtime`等等）到`191.168.1.100`上一致的部署目录。

请修改`127.0.0.1`上`peer2/config/ledger-binding.conf`文件，去除移出的账本配置。若此节点上仅此一个账本，可关闭本节点进程。

修改`191.168.1.100`上`peer2/config/ledger-binding.conf`文件，保留移出的账本配置。

3. 启动节点

在`191.168.1.100`上执行`peer2/bin`目录下`peer-startup.sh`脚本启动节点。

查询网关接口：

```bash
curl http://<网关ip>:<网关port>/ledgers/<账本hash>/settings
```

查看账本各共识节点的相关信息。