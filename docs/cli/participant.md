### 共识节点变更

借助`BFT-SMaRt`共识提供的Reconfig操作元语，`JD Chain`实现了在不停机的情况下快速更新共识网络拓扑，实现添加共识节点，移除共识节点，更新共识信息 等功能。
```bash
:bin$ ./jdchain-cli.sh participant -h
Usage: jdchain-cli participant [-hV] [--pretty] [--home=<path>] [COMMAND]
Add, update or delete participant.
  -h, --help          Show this help message and exit.
      --home=<path>   Set the home directory.
                        Default: ../
      --pretty        Pretty json print
  -V, --version       Print version information and exit.
Commands:
  register  Register new participant.
  active    Active participant.
  update    Update participant.
  inactive  Inactive participant.
  help      Displays help information about the specified command
```
- `register` [注册新节点](#注册新节点)
- `active` [激活节点](#激活节点)
-  `active` [更新节点](#更新节点)
- `inactive` [移除节点](#移除节点)

#### 注册新节点

```bash
:bin$ ./jdchain-cli.sh participant register -h
Register new participant.
Usage: jdchain-cli participant register [-hV] [--pretty] [--gw-host=<gwHost>]
                                        [--gw-port=<gwPort>] [--home=<path>]
                                        --name=<name>
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --home=<path>        Set the home directory.
      --name=<name>        Name of the participant
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `name`，新节点名称

注册新节点：
```bash
:bin$ ./jdchain-cli.sh participant register --name node4
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
// 选择账本
> 0
// 选择待注册节点公私钥（链上必须不存在此公私钥对应的用户）
select keypair to register, input the index:
0  k1                                      LdeNq3862vtUCeptww1T5mVvLbAeppYqVNdqD
1  1627618939                              LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw
2  node4                                   LdeNwG6ECEGz57o2ufhwSbnW4C35TvPqANK7T
2
input password of the key:
> 1
// 选择此交易签名用户（必须是链上存在的用户，且有相应操作权限）
select keypair to sign tx, input the index:
0  k1                                      LdeNq3862vtUCeptww1T5mVvLbAeppYqVNdqD
1  1627618939                              LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw
2  node4                                   LdeNwG6ECEGz57o2ufhwSbnW4C35TvPqANK7T
1
input password of the key:
> 1
register participant: [LdeNwG6ECEGz57o2ufhwSbnW4C35TvPqANK7T]
```
成功在账本`j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg`中注册了新的节点`LdeNwG6ECEGz57o2ufhwSbnW4C35TvPqANK7T`

可通过[共识节点列表](query.md#共识节点列表)查看新的账本列表：
```bash
:bin$ ./jdchain-cli.sh query participants
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
[{"address":"LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE","id":2,"name":"2","participantNodeState":"CONSENSUS","pubKey":"7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6"},{"address":"LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY","id":1,"name":"1","participantNodeState":"CONSENSUS","pubKey":"7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW"},{"address":"LdeNwG6ECEGz57o2ufhwSbnW4C35TvPqANK7T","id":4,"name":"node4","participantNodeState":"READY","pubKey":"7VeRKiWHcHjNoYH9kJk2fxoJxgBrstVJ7bHRecKewJAKcvUD"},{"address":"LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw","id":0,"name":"0","participantNodeState":"CONSENSUS","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"},{"address":"LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6","id":3,"name":"3","participantNodeState":"CONSENSUS","pubKey":"7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr"}]
register participant: [LdeNwG6ECEGz57o2ufhwSbnW4C35TvPqANK7T]
```
可以看出`node4`注册成功，地址为`LdeNwG6ECEGz57o2ufhwSbnW4C35TvPqANK7T`

#### 激活节点

激活节点前请正确配置并启动新节点，以下以刚注册成功的`node4`为例

1. 配置`node4`

解压`peer`压缩包，复制待加入账本其他节点中数据最新的节点数据库数据，然后修改`config/ledger-binding.conf`：
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

2. 启动`node4`
| **一定注意在启动新参与方节点进程之前确保完成了账本数据库的复制工作**

执行`peer4`中`bin`目录下`peer-startup.sh`脚本启动启动新参与方`peer4`节点进程：
```bash
./peer-startup.sh
```

3. 激活新节点
```bash
:bin$ ./jdchain-cli.sh participant active -h
Active participant.
Usage: jdchain-cli participant active [-hV] [--pretty] [--shutdown]
                                      --consensus-port=<consensusPort>
                                      [--home=<path>] --host=<host>
                                      --ledger=<ledger> --port=<port>
                                      --syn-host=<synHost> --syn-port=<synPort>
      --consensus-port=<consensusPort>
                             Set the participant consensus port.
  -h, --help                 Show this help message and exit.
      --home=<path>          Set the home directory.
      --host=<host>          Set the participant host.
      --ledger=<ledger>      Set the ledger.
      --port=<port>          Set the participant service port.
      --pretty               Pretty json print
      --shutdown             Restart the node server.
      --syn-host=<synHost>   Set synchronization participant host.
      --syn-port=<synPort>   Set synchronization participant port.
  -V, --version              Print version information and exit.
```
- `ledger`，账本哈希
- `host`，新节点地址
- `port`，新节点服务端口
- `consensus-port`，新节点共识端口
- `syn-host`，数据同步节点地址
- `syn-port`，数据同步节点服务端口

在账本`j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg`中激活`node4`（以步骤2中启动的服务地址和端口为`127.0.0.1`和`7084`例），共识端口设置为`10088`，同步节点地址和端口为`127.0.0.1`和`7080`为例：
```bash
:bin$./jdchain-cli.sh participant active --ledger j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg --host 127.0.0.1 --port 7084 --consensus-port 10088 --syn-host 127.0.0.1 --syn-port 7080
participant activated
```

成功后可通过[共识节点列表](query.md#共识节点列表)查询最新共识节点列表状态，`node4`为`CONSENSUS`

#### 更新节点

通过[激活节点](#激活节点)操作除了激活新增的节点外，还可以动态修改已经处于激活状态的共识节点的`IP`和`共识端口`信息，从而实现本机的共识端口变更，不同机器之间进行`账本迁移`。

| **在进行节点信息变更时，要求暂停向共识网络中发起新的业务数据上链请求**

1. 变更共识端口

| **操作前请确保变更到的端口未被占用**

如将`node4`共识端口由`10088`修改为`10188`，操作指令如下：

```bash
:bin$./jdchain-cli.sh participant update --ledger j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg --host 127.0.0.1 --port 7084 --consensus-port 10188 --syn-host 127.0.0.1 --syn-port 7080
participant updated
```
指令成功执行后，`peer1`的共识端口将自动变更为`10188`

2. 账本迁移

账本迁移指将一台机器（`IP`）上的共识节点迁移到另一台机器（`IP`）上，主要操作流程如下：

| **操作前请确保变更到的端口未被占用**

如将`node4`共识`IP`由`127.0.0.1`修改为`192.168.1.100`（另一台机器），操作指令如下：

```bash
:bin$./jdchain-cli.sh participant update --ledger j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg --host 192.168.1.100 --port 7084 --consensus-port 10188 --syn-host 127.0.0.1 --syn-port 7080 -shutdown
participant updated
```

**特别注意**：`-shutdown`会停止当前运行的当前账本共识服务，为必填选项，否则可能将导致整个网络需要重启。

#### 移除节点

```
:bin$ ./jdchain-cli.sh participant inactive -h
Inactive participant.
Usage: jdchain-cli participant inactive [-hV] [--pretty] --address=<address>
                                        [--home=<path>] --host=<host>
                                        --ledger=<ledger> --port=<port>
                                        --syn-host=<synHost>
                                        --syn-port=<synPort>
      --address=<address>    Set the participant address.
  -h, --help                 Show this help message and exit.
      --home=<path>          Set the home directory.
      --host=<host>          Set the participant host.
      --ledger=<ledger>      Set the ledger.
      --port=<port>          Set the participant service port.
      --pretty               Pretty json print
      --syn-host=<synHost>   Set synchronization participant host.
      --syn-port=<synPort>   Set synchronization participant port.
  -V, --version              Print version information and exit.
```
- `ledger`，账本哈希
- `address`，待移除节点共识端口
- `host`，待移除节点服务地址
- `port`，待移除节点服务端口
- `syn-host`，数据同步节点地址
- `syn-port`，数据同步节点服务端口

如移除`node4`：
```bash
:bin$ ./jdchain-cli.sh participant inactive --ledger j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg --address LdeNwG6ECEGz57o2ufhwSbnW4C35TvPqANK7T --host 127.0.0.1 --port 7084 --syn-host 127.0.0.1 --syn-port 7080
participant inactivated
```

成功后可通过[共识节点列表](query.md#共识节点列表)查询最新共识节点列表状态，`node4`为`DEACTIVATED`