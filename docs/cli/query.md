### 链上信息查询

```bash
:bin$ ./jdchain-cli.sh query -h
Usage: jdchain-cli query [-hV] [--pretty] [--gw-host=<gwHost>]
                         [--gw-port=<gwPort>] [--home=<path>] [COMMAND]
Query commands.
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
                             Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
                             Default: 8080
  -h, --help               Show this help message and exit.
      --home=<path>        Set the home directory.
                             Default: ../
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
Commands:
  ledgers                    Query ledgers.
  ledger                     Query ledger.
  participants               Query participants.
  block                      Query block.
  txs-count                  Query transactions count.
  txs                        Query transactions.
  tx                         Query transaction.
  users                      Query users.
  users-count                Query users count.
  user                       Query user.
  role-privileges            Query role privileges.
  user-privileges            Query user privileges.
  data-accounts-count        Query data accounts count.
  data-accounts              Query data accounts.
  data-account               Query data account.
  kvs-count                  Query key-values count.
  kvs                        Query kvs.
  kv                         Query kv.
  user-event-accounts-count  Query user event accounts count.
  user-event-accounts        Query user event accounts.
  user-event-account         Query user event account.
  user-event-names-count     Query user event names count.
  user-event-names           Query user event names.
  user-events-count          Query user events count.
  user-events                Query user events.
  latest-user-event          Query latest user event.
  contracts-count            Query contracts count.
  contracts                  Query contracts.
  contract                   Query contract.
  help                       Displays help information about the specified command
```
查询命令：[账本列表](#账本列表)，[账本详情](#账本详情)，[共识节点列表](#共识节点列表)，[区块详情](#区块详情)，[交易总数](#交易总数)，[交易列表](#交易列表)，[交易详情](#交易详情)，[用户总数](#用户总数)，[用户列表](#用户列表)，[用户详情](#用户详情)，[角色权限](#角色权限)，[用户权限](#用户权限)，[数据账户总数](#数据账户总数)，[数据账户列表](#数据账户列表)，[数据账户详情](#数据账户详情)，[KV总数](#KV总数)，[KV列表](#KV列表)，[KV详情](#KV详情)，[用户事件账户总数](#用户事件账户总数)，[用户事件账户列表](#用户事件账户列表)，[用户事件账户详情](#用户事件账户详情)，[用户事件名总数](#用户事件名总数)，[用户事件名列表](#用户事件名列表)，[用户事件总数](#用户事件总数)，[用户事件列表](#用户事件列表)，[最新用户事件](#最新用户事件)，[合约总数](#合约总数)，[合约列表](#合约列表)，[合约详情](#合约详情)

#### 账本列表

```bash
:bin$ ./jdchain-cli.sh query ledgers
j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
```
返回当前网关服务所有账本列表

#### 账本详情

选择账本，打印当前账本详细信息：
```bash
:bin$ ./jdchain-cli.sh query ledger
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"hash":"j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg","latestBlockHash":"j5n8KGMFsRM7jzf99XK1jDK342fauj3myKcdgPJyLYyxws","latestBlockHeight":18}
```

可添加`--pretty`格式化输出`json`数据

#### 共识节点列表

查询共识节点列表：
```bash
:bin$ ./jdchain-cli.sh query participants
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
[{"address":"LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE","id":2,"name":"2","participantNodeState":"CONSENSUS","pubKey":"7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6"},{"address":"LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY","id":1,"name":"1","participantNodeState":"CONSENSUS","pubKey":"7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW"},{"address":"LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw","id":0,"name":"0","participantNodeState":"CONSENSUS","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"},{"address":"LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6","id":3,"name":"3","participantNodeState":"CONSENSUS","pubKey":"7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr"}]
```
账本`j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg`中有四个共识节点，均为`CONSENSUS`状态

#### 区块详情

```bash
:bin$ ./jdchain-cli.sh query block -h
Query block.
Usage: jdchain-cli query block [-hV] [--pretty] [--gw-host=<gwHost>]
                               [--gw-port=<gwPort>] [--hash=<hash>]
                               [--height=<height>] [--home=<path>]
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --hash=<hash>        Block hash
      --height=<height>    Block height.
      --home=<path>        Set the home directory.
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `height`，区块高度，默认`-1`查询当前最高区块

如：
```bash
:bin$ ./jdchain-cli.sh query block --pretty
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{
        "adminAccountHash":"j5p5z4es9RPrQWFu2nSJBQFT68byeGqAdUDu63qa5xV8Df",
        "contractAccountSetHash":"j5kbvGQ1tFH2GXgk8ThF1co7H2gCrFdeZn7Nibva7Md72P",
        "dataAccountSetHash":"j5fSmQkk8tb9v9SYDmaAko3oJJqmCm54HGbQwgbV2nTCVk",
        "hash":"j5n8KGMFsRM7jzf99XK1jDK342fauj3myKcdgPJyLYyxws",
        "height":18,
        "ledgerHash":"j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg",
        "previousHash":"j5motWQdmckqTxkG3x8DcE6quv2oGteKzsHqoV89Lfo4Mj",
        "timestamp":1627703544928,
        "transactionSetHash":"j5rc9PV5p9C8mXDAt8p9MR4QY3VAr4wQNG7zC3MSvFpcGG",
        "userAccountSetHash":"j5haZvthy9gGaJ8M3mEbwtpeUg9Z113ifZbcWtzAeQcQdu",
        "userEventSetHash":"j5nke9ZAnVRf1Qgg4u9Ske8RoZFbFVzVrKQid14qcqXaAn"
}
```
返回当前最高区块详情

#### 交易总数

```bash
:bin$ ./jdchain-cli.sh query txs-count -h
Query transactions count.
Usage: jdchain-cli query txs-count [-hV] [--in-block] [--pretty]
                                   [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                                   --height=<height> [--home=<path>]
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --height=<height>    Block height.
      --home=<path>        Set the home directory.
      --in-block           In the given block.
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `height`，区块高度
- `in-block`，是否只统计`height`参数指定区块数据

如查询高度`10`区块交易总数（会统计区块`0-10`内所有交易）：
```bash
:bin$ ./jdchain-cli.sh query txs-count --height 10
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
11
```

查询高度`10`区块内交易总数（仅统计区块`10`交易）：
```bash
:bin$ ./jdchain-cli.sh query txs-count --height 10 --in-block
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
1
```

#### 交易列表

```bash
:bin$ ./jdchain-cli.sh query txs -h
Query transactions.
Usage: jdchain-cli query txs [-hV] [--in-block] [--pretty] --count=<count>
                             [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                             [--height=<height>] [--home=<path>] --index=<index>
      --count=<count>      Transaction item count
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --height=<height>    Block height.
      --home=<path>        Set the home directory.
      --in-block           In the given block.
      --index=<index>      Transaction item index
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `height`，区块高度
- `in-block`，是否只统计`height`参数指定区块数据
- `index`，查询起始位置
- `count`，最大返回

如查询高度`10`区块交易列表（会统计区块`0-10`内所有交易），从第`0`条开始，最大返回`1`条：
```bash
:bin$ ./jdchain-cli.sh query txs --height 10 --index 0 --count 1
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
[{"request":{"endpointSignatures":[],"nodeSignatures":[{"digest":"SMHYntB7uTm3N4mReke4srWHhpDkjDFGRuz7Bis8quJt19igevwA4rEwNmZqFGGLMExgmrPvdGrxgRrhLhpxUo1KGW","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"},{"digest":"SMHbyCLcvds5sRKaWPptSjixxoaiwkQfM1noLGpLSvsmxwUSg4J55UhJtK1ZWiQp3rxy5FEMZpwHGkywexYuwXEWHo","pubKey":"7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW"},{"digest":"SMGP3a7GHW6qWbQY3ZXX5UBhHNFLrSkUzRLLfCFA9Gu6CJVoYVvBQjjCEzaYq9ox38DvyZJQFuLWPirV2G6VMeARqQ","pubKey":"7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6"},{"digest":"SMHGzmEVzZPKyvN3zC6nLbVypufWEfd6cFvnMMjuEXFk5WncLeUx4CyAohTnNLP21ksJ6r15usowbkRFeaUYQgJkre","pubKey":"7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr"}],"transactionContent":{"operations":[{"@type":"com.jd.blockchain.ledger.LedgerInitOperation","initSetting":{"consensusParticipants":[{"address":"LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw","id":0,"name":"0","participantNodeState":"CONSENSUS","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"},{"address":"LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY","id":1,"name":"1","participantNodeState":"CONSENSUS","pubKey":"7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW"},{"address":"LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE","id":2,"name":"2","participantNodeState":"CONSENSUS","pubKey":"7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6"},{"address":"LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6","id":3,"name":"3","participantNodeState":"CONSENSUS","pubKey":"7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr"}],"consensusProvider":"com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider","consensusSettings":"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","createdTime":1627618941000,"cryptoSetting":{"autoVerifyHash":true,"hashAlgorithm":8216,"supportedProviders":[{"algorithms":[{},{},{},{},{},{},{}],"name":"com.jd.blockchain.crypto.service.classic.ClassicCryptoService"},{"algorithms":[{},{},{}],"name":"com.jd.blockchain.crypto.service.sm.SMCryptoService"}]},"ledgerSeed":"kULN7uzXyZuC7rD9BMLIgA==","ledgerStructureVersion":-1}},{"@type":"com.jd.blockchain.ledger.UserRegisterOperation","userID":{"address":"LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"}},{"@type":"com.jd.blockchain.ledger.UserRegisterOperation","userID":{"address":"LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY","pubKey":"7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW"}},{"@type":"com.jd.blockchain.ledger.UserRegisterOperation","userID":{"address":"LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE","pubKey":"7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6"}},{"@type":"com.jd.blockchain.ledger.UserRegisterOperation","userID":{"address":"LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6","pubKey":"7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr"}},{"@type":"com.jd.blockchain.ledger.RolesConfigureOperation","roles":[{"disableLedgerPermissions":[],"disableTransactionPermissions":[],"enableLedgerPermissions":["CONFIGURE_ROLES","AUTHORIZE_USER_ROLES","SET_CONSENSUS","SET_CRYPTO","REGISTER_PARTICIPANT","REGISTER_USER","REGISTER_DATA_ACCOUNT","REGISTER_CONTRACT","UPGRADE_CONTRACT","SET_USER_ATTRIBUTES","WRITE_DATA_ACCOUNT","APPROVE_TX","CONSENSUS_TX","REGISTER_EVENT_ACCOUNT","WRITE_EVENT_ACCOUNT"],"enableTransactionPermissions":["DIRECT_OPERATION","CONTRACT_OPERATION"],"roleName":"DEFAULT"}]},{"@type":"com.jd.blockchain.ledger.UserAuthorizeOperation","userRolesAuthorizations":[{"authorizedRoles":[],"policy":"UNION","unauthorizedRoles":[],"userAddresses":["LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw"]}]},{"@type":"com.jd.blockchain.ledger.UserAuthorizeOperation","userRolesAuthorizations":[{"authorizedRoles":[],"policy":"UNION","unauthorizedRoles":[],"userAddresses":["LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY"]}]},{"@type":"com.jd.blockchain.ledger.UserAuthorizeOperation","userRolesAuthorizations":[{"authorizedRoles":[],"policy":"UNION","unauthorizedRoles":[],"userAddresses":["LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE"]}]},{"@type":"com.jd.blockchain.ledger.UserAuthorizeOperation","userRolesAuthorizations":[{"authorizedRoles":[],"policy":"UNION","unauthorizedRoles":[],"userAddresses":["LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6"]}]}],"timestamp":1627618941000},"transactionHash":"j5vJGDBQLi6Vo5Gxtsab1vyL2TFaf1NoXDi6Xv2uvCcj9T"},"result":{"blockHeight":0,"dataSnapshot":{"adminAccountHash":"j5u4gqeAkKb3DoELpXP9bDgAxCVRQgKsguPvE1Wc9re1UT","userAccountSetHash":"j5hzkPPBJAqKs4rLWbEiFhbh1VW6Jc2xk878X5A6JywPnC"},"executionState":"SUCCESS","transactionHash":"j5vJGDBQLi6Vo5Gxtsab1vyL2TFaf1NoXDi6Xv2uvCcj9T"}}]
```

查询高度`10`区块内交易列表（仅统计区块`10`交易），从第`0`条开始，最大返回`1`：
```bash
:bin$ ./jdchain-cli.sh query txs --height 10 --index 0 --count 1 --in-block
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
[{"request":{"endpointSignatures":[{"digest":"SMKgce34AxQ8JEDDZY3x7iMvbL5QymiC93XPWqSirUB2AN8rvx18ynDS9f1zFA6QyXQNowA1XNhaLWnArrt7JHHxhY","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"}],"nodeSignatures":[{"digest":"SMKgce34AxQ8JEDDZY3x7iMvbL5QymiC93XPWqSirUB2AN8rvx18ynDS9f1zFA6QyXQNowA1XNhaLWnArrt7JHHxhY","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"}],"transactionContent":{"ledgerHash":"j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg","operations":[{"@type":"com.jd.blockchain.ledger.DataAccountKVSetOperation","accountAddress":"LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC","writeSet":[{"expectedVersion":-1,"key":"k1","value":{"bytes":"djE=","type":"TEXT"}}]}],"timestamp":1627632026435},"transactionHash":"j5fo2aAwp2tsneHm4wE8AnWLV7CKyax7BLWJqJm2V3WsrD"},"result":{"blockHeight":10,"dataSnapshot":{"adminAccountHash":"j5p5z4es9RPrQWFu2nSJBQFT68byeGqAdUDu63qa5xV8Df","dataAccountSetHash":"j5fSmQkk8tb9v9SYDmaAko3oJJqmCm54HGbQwgbV2nTCVk","userAccountSetHash":"j5mwiewVaxPLYQciovrB9nShWD5nr7YYFopbmWx28jqiFH","userEventSetHash":"j5wf9v6ixDDSD2gRi47r3vomkmTaRCqSLNbaNxrKcVdtD7"},"executionState":"SUCCESS","transactionHash":"j5fo2aAwp2tsneHm4wE8AnWLV7CKyax7BLWJqJm2V3WsrD"}}]
```

#### 交易详情

```bash
e:bin$ ./jdchain-cli.sh query tx -h
Query transaction.
Usage: jdchain-cli query tx [-hV] [--pretty] [--gw-host=<gwHost>]
                            [--gw-port=<gwPort>] [--hash=<hash>] [--home=<path>]
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --hash=<hash>        Transaction hash
      --home=<path>        Set the home directory.
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `hash`，交易哈希

如查询交易`j5fo2aAwp2tsneHm4wE8AnWLV7CKyax7BLWJqJm2V3WsrD`详情：
```bash
:bin$ ./jdchain-cli.sh query tx --hash j5fo2aAwp2tsneHm4wE8AnWLV7CKyax7BLWJqJm2V3WsrD
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"request":{"endpointSignatures":[{"digest":"SMKgce34AxQ8JEDDZY3x7iMvbL5QymiC93XPWqSirUB2AN8rvx18ynDS9f1zFA6QyXQNowA1XNhaLWnArrt7JHHxhY","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"}],"nodeSignatures":[{"digest":"SMKgce34AxQ8JEDDZY3x7iMvbL5QymiC93XPWqSirUB2AN8rvx18ynDS9f1zFA6QyXQNowA1XNhaLWnArrt7JHHxhY","pubKey":"7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"}],"transactionContent":{"ledgerHash":"j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg","operations":[{"@type":"com.jd.blockchain.ledger.DataAccountKVSetOperation","accountAddress":"LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC","writeSet":[{"expectedVersion":-1,"key":"k1","value":{"bytes":"djE=","type":"TEXT"}}]}],"timestamp":1627632026435},"transactionHash":"j5fo2aAwp2tsneHm4wE8AnWLV7CKyax7BLWJqJm2V3WsrD"},"result":{"blockHeight":10,"dataSnapshot":{"adminAccountHash":"j5p5z4es9RPrQWFu2nSJBQFT68byeGqAdUDu63qa5xV8Df","dataAccountSetHash":"j5fSmQkk8tb9v9SYDmaAko3oJJqmCm54HGbQwgbV2nTCVk","userAccountSetHash":"j5mwiewVaxPLYQciovrB9nShWD5nr7YYFopbmWx28jqiFH","userEventSetHash":"j5wf9v6ixDDSD2gRi47r3vomkmTaRCqSLNbaNxrKcVdtD7"},"executionState":"SUCCESS","transactionHash":"j5fo2aAwp2tsneHm4wE8AnWLV7CKyax7BLWJqJm2V3WsrD"}}
```

#### 用户总数

查询用户总数：
```bash
:bin$ ./jdchain-cli.sh query users-count
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
10
```
当前区块链网络共有`10`个用户

#### 用户列表

```bash
:bin$ ./jdchain-cli.sh query users -h
Query users.
Usage: jdchain-cli query users [-hV] [--pretty] --count=<count>
                               [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                               [--home=<path>] --index=<index>
      --count=<count>      User item count
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --home=<path>        Set the home directory.
      --index=<index>      User item index
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `index`，起始位置
- `count`，最大返回

如分页查询用户列表，从第`0`个开始，最大返回`10`条：
```bash
:bin$ ./jdchain-cli.sh query users --index 0 --count 10
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
ADDRESS                                 PUBKEY
LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6   7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr
LdeNq3862vtUCeptww1T5mVvLbAeppYqVNdqD   7VeRGuwP2iUykAL4beftP1DuDTj7y2uFGEM6mx3Dy7YSm2j1
LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC   7VeRFk4ANQHjWjAmAoL7492fuykTpXujihJeAgbXT2J9H9Yk
LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw   7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn
LdeNwTWpyzqioLURrHQuoGcnwA6YLiFWn3LNn   7VeRH7BsRntvJmomjw7YvF5HZVsSMb48GKzPnAP7iekRCLGq
LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY   7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW
LdeNisM5oTypwPYv9tnhFNosRjCyXzzViU4SA   7VeREyEcDcY85DRdWAEsmJ4Moh89eE21AU2LEDbYG3t3MrGo
LdeNqvSjL4izfpMNsGpQiBpTBse4g6qLxZ6j5   7VeRFd2LB8ZmYnVNc2pux5TwVqHv3pwT6JXoF3fzDon9bSXK
LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE   7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6
LdeNufGZewrvS7sE4VWC9m1SFkPqVwjBN87LB   7VeRMGBMMBQVoZQTU3mcJYGgbVcQzxXiq6NK69TaCjEoktLf
```
返回`10`个用户的地址和公私钥信息

| 由于`JD Chain`网络并不保证用户（数据账户/事件账户/合约账户）按创建顺序排列，所以当有新的用户注册后，前后两次相同参数的查询数据可能不一致！！！

#### 用户详情

```bash
:bin$ ./jdchain-cli.sh query user -h
Query user.
Usage: jdchain-cli query user [-hV] [--pretty] --address=<address>
                              [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                              [--home=<path>]
      --address=<address>   User address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，用户地址

根据地址`LdeNufGZewrvS7sE4VWC9m1SFkPqVwjBN87LB`查询用户详情：
```bash
:bin$ ./jdchain-cli.sh query user --address LdeNufGZewrvS7sE4VWC9m1SFkPqVwjBN87LB
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"address":"LdeNufGZewrvS7sE4VWC9m1SFkPqVwjBN87LB","pubKey":"7VeRMGBMMBQVoZQTU3mcJYGgbVcQzxXiq6NK69TaCjEoktLf"}
```

#### 角色权限

```bash
:bin$ ./jdchain-cli.sh query role-privileges -h
Query role privileges.
Usage: jdchain-cli query role-privileges [-hV] [--pretty] [--gw-host=<gwHost>]
       [--gw-port=<gwPort>] [--home=<path>] --role=<role>
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --home=<path>        Set the home directory.
      --pretty             Pretty json print
      --role=<role>        Role name
  -V, --version            Print version information and exit.
```
- `role`，角色名称

查询角色`ROLE1`权限信息：
```bash
:bin$ ./jdchain-cli.sh query role-privileges --role ROLE1
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"ledgerPrivilege":{"permissionCount":2,"privilege":["REGISTER_USER","REGISTER_DATA_ACCOUNT"]},"transactionPrivilege":{"permissionCount":2,"privilege":["DIRECT_OPERATION","CONTRACT_OPERATION"]}}
```

#### 用户权限

```bash
:bin$ ./jdchain-cli.sh query user-privileges -h
Query user privileges.
Usage: jdchain-cli query user-privileges [-hV] [--pretty] --address=<address>
       [--gw-host=<gwHost>] [--gw-port=<gwPort>] [--home=<path>]
      --address=<address>   User address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，用户地址

查询用户`LdeNufGZewrvS7sE4VWC9m1SFkPqVwjBN87LB`权限信息：
```bash
:bin$ ./jdchain-cli.sh query user-privileges --address LdeNufGZewrvS7sE4VWC9m1SFkPqVwjBN87LB
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"ledgerPrivilegesBitset":{"privilege":["CONFIGURE_ROLES","AUTHORIZE_USER_ROLES","SET_CONSENSUS","SET_CRYPTO","REGISTER_PARTICIPANT","REGISTER_USER","REGISTER_DATA_ACCOUNT","REGISTER_CONTRACT","UPGRADE_CONTRACT","SET_USER_ATTRIBUTES","WRITE_DATA_ACCOUNT","APPROVE_TX","CONSENSUS_TX","REGISTER_EVENT_ACCOUNT","WRITE_EVENT_ACCOUNT"]},"transactionPrivilegesBitset":{"privilege":["DIRECT_OPERATION","CONTRACT_OPERATION"]},"userAddress":"LdeNufGZewrvS7sE4VWC9m1SFkPqVwjBN87LB","userRole":["DEFAULT"]}
```

#### 数据账户总数

查询数据账户总数：
```bash
:bin$ ./jdchain-cli.sh query data-accounts-count
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
1
```
当前账本共有一个数据账户

#### 数据账户列表

```bash
:bin$ ./jdchain-cli.sh query data-accounts -h
Query data accounts.
Usage: jdchain-cli query data-accounts [-hV] [--pretty] --count=<count>
                                       [--gw-host=<gwHost>]
                                       [--gw-port=<gwPort>] [--home=<path>]
                                       --index=<index>
      --count=<count>      Data account item count
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --home=<path>        Set the home directory.
      --index=<index>      Data account item index
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `index`，起始位置
- `count`，最大返回

如分页查询数据账户列表，从第`0`个开始，最大返回`10`条：
```bash
:bin$ ./jdchain-cli.sh query data-accounts --index 0 --count 10
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
ADDRESS                                 PUBKEY
LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC   7VeRFk4ANQHjWjAmAoL7492fuykTpXujihJeAgbXT2J9H9Yk
```
返回数据账户的地址和公私钥信息

#### 数据账户详情

```bash
:bin$ ./jdchain-cli.sh query data-account -h
Query data account.
Usage: jdchain-cli query data-account [-hV] [--pretty] --address=<address>
                                      [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                                      [--home=<path>]
      --address=<address>   Data account address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，数据账户地址

查询数据账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`详情：
```bash
:bin$ ./jdchain-cli.sh query data-account --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"address":"LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC","dataRootHash":"j5vyv6SmvSXQNKyjsEVCQJsyqXxKGZMBU62fKvtdJm2W4y","headerRootHash":"j5sA2KPgY9vidgTUCjCJiscn2CXapgSJsVnWe54xXKohej","pubKey":"7VeRFk4ANQHjWjAmAoL7492fuykTpXujihJeAgbXT2J9H9Yk"}
```

#### KV总数

```bash
:bin$ ./jdchain-cli.sh query kvs-count -h
Query key-values count.
Usage: jdchain-cli query kvs-count [-hV] [--pretty] --address=<address>
                                   [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                                   [--home=<path>]
      --address=<address>   Data account address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，数据账户地址

查询数据账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`中`kv`数据总数：
```bash
:bin$ ./jdchain-cli.sh query kvs-count --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
1
```
共有一个`kv`

#### KV列表

```bash
:bin$ ./jdchain-cli.sh query kvs -h
Query kvs.
Usage: jdchain-cli query kvs [-hV] [--pretty] --address=<address>
                             --count=<count> [--gw-host=<gwHost>]
                             [--gw-port=<gwPort>] [--home=<path>]
                             --index=<index>
      --address=<address>   Data account address
      --count=<count>       KV item count
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --index=<index>       KV item index
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，数据账户地址
- `index`，起始位置
- `count`，最大返回

如分页查询数据账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`中`kv`列表，从第`0`个开始，最大返回`10`条：
```bash
:bin$ ./jdchain-cli.sh query kvs --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC --index 0 --count 10
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"key":"k1","type":"TEXT","value":"v1","version":0}
```

#### KV详情

```bash
:bin$ ./jdchain-cli.sh query kv -h
Query kv.
Usage: jdchain-cli query kv [-hV] [--pretty] --address=<address>
                            [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                            [--home=<path>] --key=<key>
      --address=<address>   Data account address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --key=<key>           Key
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，数据账户地址
- `key`，`key`

查询数据账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`中`k1`最新数据：
```bash
:bin$ ./jdchain-cli.sh query kv --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC --key k1
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"key":"k1","type":"TEXT","value":"v1","version":0}
```

#### 用户事件账户总数

查询数据账户总数：
```bash
:bin$ ./jdchain-cli.sh query user-event-accounts-count
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
4
```
当前账本共有`4`个数据账户

#### 用户事件账户列表

```bash
:bin$ ./jdchain-cli.sh query user-event-accounts -h
Query user event accounts.
Usage: jdchain-cli query user-event-accounts [-hV] [--pretty] --count=<count>
       [--gw-host=<gwHost>] [--gw-port=<gwPort>] [--home=<path>] --index=<index>
      --count=<count>      Event account item count
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --home=<path>        Set the home directory.
      --index=<index>      Event account item index
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `index`，起始位置
- `count`，最大返回

如分页查询用户事件账户列表，从第`0`个开始，最大返回`10`条：
```bash
:bin$ ./jdchain-cli.sh query user-event-accounts --index 0 --count 10
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
ADDRESS                                 PUBKEY
LdeNhAxxXjbh56LqeB7xHpgZgHG6GDTZ45GgJ   7VeR82o3hZy1AVEjmxfNpHHW3d1zabbELmJUnijGkKJNDXu5
LdeP33nxsYxYgaELQUkd8tBsTmwrkySiqnAVF   7VeRBA5zD2EDCiRtsiHzMQUEPf52hjKwhAi6PfNCgoiRQrSw
LdeNnDJyqYgxDernBf6Vh68CkM5FbJNYtQCPA   7VeRB71W3anhCBretEP2R9YjiFB7ne2o5qhsivPo3XmHZ7cJ
LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC   7VeRFk4ANQHjWjAmAoL7492fuykTpXujihJeAgbXT2J9H9Yk
```
返回用户事件账户的地址和公私钥信息

#### 用户事件账户详情

```bash
:bin$ ./jdchain-cli.sh query user-event-account -h
Query user event account.
Usage: jdchain-cli query user-event-account [-hV] [--pretty]
       --address=<address> [--gw-host=<gwHost>] [--gw-port=<gwPort>]
       [--home=<path>]
      --address=<address>   Event account address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，用户事件账户地址

查询事件账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`详情：
```bash
:bin$ ./jdchain-cli.sh query user-event-account --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"address":"LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC","pubKey":"7VeRFk4ANQHjWjAmAoL7492fuykTpXujihJeAgbXT2J9H9Yk"}
```

#### 用户事件名总数

```bash
:bin$ ./jdchain-cli.sh query user-event-names-count -h
Query user event names count.
Usage: jdchain-cli query user-event-names-count [-hV] [--pretty]
       --address=<address> [--gw-host=<gwHost>] [--gw-port=<gwPort>]
       [--home=<path>]
      --address=<address>   Event account address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，用户事件账户地址

查询事件账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`事件名总数：
```bash
:bin$ ./jdchain-cli.sh query user-event-names-count --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
1
```

#### 用户事件名列表

```bash
:bin$ ./jdchain-cli.sh query user-event-names -h
Query user event names.
Usage: jdchain-cli query user-event-names [-hV] [--pretty] --address=<address>
       --count=<count> [--gw-host=<gwHost>] [--gw-port=<gwPort>]
       [--home=<path>] --index=<index>
      --address=<address>   Event account address
      --count=<count>       Event name item count
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --index=<index>       Event name item index
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，事件账户地址
- `index`，起始位置
- `count`，最大返回

如分页查询用户事件账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`事件名列表，从第`0`个开始，最大返回`10`条：
```bash
:bin$ ./jdchain-cli.sh query user-event-names --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC --index 0 --count 10
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
t1
```
当前事件账户仅有一个事件名

#### 用户事件总数

```bash
:bin$ ./jdchain-cli.sh query user-events-count -h
Query user events count.
Usage: jdchain-cli query user-events-count [-hV] [--pretty] --address=<address>
       [--gw-host=<gwHost>] [--gw-port=<gwPort>] [--home=<path>] --name=<name>
      --address=<address>   Event account address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --name=<name>         Event name
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，事件账户地址
- `name`，事件名

查询事件账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`事件名`t1`中事件总数：
```bash
:bin$ ./jdchain-cli.sh query user-events-count --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC --name t1
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
1
```
当前事件名仅有一个事件

#### 用户事件列表

```bash
:bin$ ./jdchain-cli.sh query user-events -h
Query user events.
Usage: jdchain-cli query user-events [-hV] [--pretty] --address=<address>
                                     --count=<count> [--gw-host=<gwHost>]
                                     [--gw-port=<gwPort>] [--home=<path>]
                                     --index=<index> --name=<name>
      --address=<address>   Event account address
      --count=<count>       Event name item count
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --index=<index>       Event name item index
      --name=<name>         Event name
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，事件账户地址
- `name`，事件名
- `index`，起始位置
- `count`，最大返回

分页查询用户事件账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`事件名`t1`中事件，从第`0`个开始，最大返回`10`条：
```bash
:bin$ ./jdchain-cli.sh query user-events --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC --name t1 --index 0 --count 10
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"blockHeight":12,"content":{"bytes":"YzE=","type":"TEXT"},"contractSource":"","eventAccount":"LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC","name":"t1","sequence":0,"transactionSource":"j5jSszhiJUTbCGtFgxd6uBWyxj56CEHRyhDF6nAnUvJTp7"}
```

#### 最新用户事件

```bash
:bin$ ./jdchain-cli.sh query latest-user-event -h
Query latest user event.
Usage: jdchain-cli query latest-user-event [-hV] [--pretty] --address=<address>
       [--gw-host=<gwHost>] [--gw-port=<gwPort>] [--home=<path>] --name=<name>
      --address=<address>   Event account address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --name=<name>         Event name
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，时间账户地址
- `name`，事件名

查询用户事件账户`LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC`事件名`t1`最新事件：
```bash
:bin$ ./jdchain-cli.sh query latest-user-event --address LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC --name t1
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
{"blockHeight":12,"content":{"bytes":"YzE=","type":"TEXT"},"contractSource":"","eventAccount":"LdeNwQWabrf6WSjZ35saFo52MfQFhVKvm11aC","name":"t1","sequence":0,"transactionSource":"j5jSszhiJUTbCGtFgxd6uBWyxj56CEHRyhDF6nAnUvJTp7"}
```

#### 合约总数

查询合约总数：
```bash
:bin$ ./jdchain-cli.sh query contracts-count
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
1
```
当前账本仅有一个合约

#### 合约列表

```bash
:bin$ ./jdchain-cli.sh query contracts -h
Query contracts.
Usage: jdchain-cli query contracts [-hV] [--pretty] --count=<count>
                                   [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                                   [--home=<path>] --index=<index>
      --count=<count>      Contract item count
      --gw-host=<gwHost>   Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>   Set the gateway port. Default: 8080
  -h, --help               Show this help message and exit.
      --home=<path>        Set the home directory.
      --index=<index>      Contract item index
      --pretty             Pretty json print
  -V, --version            Print version information and exit.
```
- `index`，起始位置
- `count`，最大返回

分页查询合约，从第`0`个开始，最大返回`10`条：
```bash
:bin$ ./jdchain-cli.sh query contracts --index 0 --count 10
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
ADDRESS                                 PUBKEY
LdeNyF6jdNry5iCqmHdAFTQPvC8UkbJ9avoXH   7VeRFZEqSdXWQxaLUFaAgJVdVTssuwQdBg4KPGgCCTbrzqxA
```
返回合约地址和公钥信息

#### 合约详情

```bash
e:bin$ ./jdchain-cli.sh query contract -h
Query contract.
Usage: jdchain-cli query contract [-hV] [--pretty] --address=<address>
                                  [--gw-host=<gwHost>] [--gw-port=<gwPort>]
                                  [--home=<path>]
      --address=<address>   Contract address
      --gw-host=<gwHost>    Set the gateway host. Default: 127.0.0.1
      --gw-port=<gwPort>    Set the gateway port. Default: 8080
  -h, --help                Show this help message and exit.
      --home=<path>         Set the home directory.
      --pretty              Pretty json print
  -V, --version             Print version information and exit.
```
- `address`，合约地址

查询合约`LdeNyF6jdNry5iCqmHdAFTQPvC8UkbJ9avoXH`详情：
```bash
:bin$ ./jdchain-cli.sh query contract --address LdeNyF6jdNry5iCqmHdAFTQPvC8UkbJ9avoXH
select ledger, input the index:
INDEX  LEDGER
0      j5sB3sVTFgTqTYzo7KtQjBLSy8YQGPpJpvQZaW9Eqk46dg
> 0
package com.jdchain.samples.contract;

import com.jd.blockchain.contract.*;
import utils.*;
import com.jd.blockchain.crypto.*;
import com.jd.blockchain.ledger.*;

public class SampleContractImpl implements EventProcessingAware, SampleContract
{
    private ContractEventContext eventContext;

    public void setKVWithVersion(final String address, final String key, final String value, final long version) {
        this.eventContext.getLedger().dataAccount(Bytes.fromBase58(address)).setText(key, value, version);
    }

    public void setKV(final String address, final String key, final String value) {
        final TypedKVEntry[] entries = this.eventContext.getUncommittedLedger().getDataEntries(address, new String[] { key });
        long version = -1L;
        if (null != entries && entries.length > 0) {
            version = entries[0].getVersion();
        }
        this.eventContext.getLedger().dataAccount(Bytes.fromBase58(address)).setText(key, value, version);
    }

    public String registerUser(final String seed) {
        final CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
        final SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);
        final AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());
        final BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
        this.eventContext.getLedger().users().register(keypair.getIdentity());
        return keypair.getAddress().toBase58();
    }

    public String registerDataAccount(final String seed) {
        final CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
        final SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);
        final AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());
        final BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
        this.eventContext.getLedger().dataAccounts().register(keypair.getIdentity());
        return keypair.getAddress().toBase58();
    }

    public String registerEventAccount(final String seed) {
        final CryptoAlgorithm algorithm = Crypto.getAlgorithm("ed25519");
        final SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);
        final AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());
        final BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
        this.eventContext.getLedger().eventAccounts().register(keypair.getIdentity());
        return keypair.getAddress().toBase58();
    }

    public void publishEventWithSequence(final String address, final String topic, final String content, final long sequence) {
        this.eventContext.getLedger().eventAccount(Bytes.fromBase58(address)).publish(topic, content, sequence);
    }

    public void publishEvent(final String address, final String topic, final String content) {
        final Event event = this.eventContext.getUncommittedLedger().getLatestEvent(address, topic);
        long sequence = -1L;
        if (null != event) {
            sequence = event.getSequence();
        }
        this.eventContext.getLedger().eventAccount(Bytes.fromBase58(address)).publish(topic, content, sequence);
    }

    public void beforeEvent(final ContractEventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void postEvent(final ContractEventContext eventContext, final Exception error) {
    }
}

```