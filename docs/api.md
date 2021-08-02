# 网关API

[账本](#2-账本)
- [获取账本总数](#21-获取账本总数)
- [获取账本列表](#22-获取账本列表)
- [获取账本详细信息](#23-获取账本详细信息)
- [获取账本参与方总数](#24-获取账本参与方总数)
- [获取账本参与方列表](#25-获取账本参与方列表)
- [获取账本元数据信息](#26-获取账本元数据信息)
- [获取账本初始化配置信息](#27-获取账本初始化配置信息)
- [获取账本管理数据信息](#28-获取账本管理数据信息)

[区块](#3-区块)
- [获取最新区块](#31-获取最新区块)
- [根据区块哈希获取区块详细信息](#32-根据区块哈希获取区块详细信息)
- [根据区块高度获取区块详细信息](#33-根据区块高度获取区块详细信息)

[交易](#4-交易)
- [获取账本交易总数](#41-获取账本交易总数)
- [根据区块高度查询交易数量](#42-根据区块高度查询交易数量)
- [根据区块哈希查询交易数量](#43-根据区块哈希查询交易数量)
- [根据区块高度查询区块内的交易数量](#44-根据区块高度查询区块内的交易数量)
- [根据区块哈希查询区块内的交易数量](#45-根据区块哈希查询区块内的交易数量)
- [获取指定区块高度的交易列表](#46-获取指定区块高度的交易列表)
- [获取指定哈希的区块的交易列表](#47-获取指定哈希的区块的交易列表)
- [获取交易详细信息](#48-获取交易详细信息)

[用户](#5-用户)
- [获取用户总数](#51-获取用户总数)
- [获取用户列表](#52-获取用户列表)
- [获取用户详细信息](#53-获取用户详细信息)
- [获取指定高度的区块的用户总数](#54-获取指定高度的区块的用户总数)
- [获取指定哈希的区块的用户总数](#55-获取指定哈希的区块的用户总数)
- [根据区块高度查询区块内的用户数量](#56-根据区块高度查询区块内的用户数量)
- [根据区块哈希查询区块内的用户数量](#57-根据区块哈希查询区块内的用户数量)
- [查询最新区块新增用户数量](#58-查询最新区块新增用户数量)

[角色权限](#6-角色权限)
- [根据角色获取权限信息](#61-根据角色获取权限信息)
- [根据用户获取权限信息](#62-根据用户获取权限信息)

[数据账户](#7-数据账户)
- [获取账户列表](#71-获取账户列表)
- [获取账户详细信息](#72-获取账户详细信息)
- [获取账户总数](#73-获取账户总数)
- [获取指定高度的区块的数据账户总数](#74-获取指定高度的区块的数据账户总数)
- [获取指定哈希的区块的数据账户总数](#75-获取指定哈希的区块的数据账户总数)
- [根据区块高度查询区块内的数据账户数量](#76-根据区块高度查询区块内的数据账户数量)
- [根据区块哈希查询区块内的数据账户数量](#77-根据区块哈希查询区块内的数据账户数量)
- [获取某数据账户KV总数](#78-获取某数据账户KV总数)
- [获取某数据账户KV详情](#79-获取某数据账户KV详情)
- [获取某数据账户KV详情](#710-获取某数据账户KV详情)
- [获取某数据账户KV整个历史详情](#711-获取某数据账户KV整个历史详情)

[合约](#8-合约)
- [获取合约总数](#81-获取合约总数)
- [获取指定区块高度的合约总数](#82-获取指定区块高度的合约总数)
- [获取指定区块哈希的合约总数](#83-获取指定区块哈希的合约总数)
- [根据区块高度查询区块内的合约总数](#84-根据区块高度查询区块内的合约总数)
- [根据区块哈希查询区块内的合约总数](#85-根据区块哈希查询区块内的合约总数)
- [获取合约列表](#86-获取合约列表)
- [获取合约详细信息](#87-获取合约详细信息)

[事件](#9-用户自定义事件)

- [获取事件账户列表](#91-获取事件账户列表)
- [获取事件账户](#92-获取事件账户)
- [获取事件账户总数](#93-获取事件账户总数)
- [获取事件名数量](#94-获取事件名数量)
- [获取事件名列表](#95-获取事件名列表)
- [获取最新事件](#96-获取最新事件)
- [获取事件数量](#97-获取事件数量)
- [获取事件列表](#98-获取事件列表)

## 1 API调用说明

该文档内的所有api的调用成功和失败均按照以下规则

### 1.1 成功

```json
{
  "data": ...,
  "success": true
}
```

说明

    - success 值为 true 表明api调用成功
    - data 为返回的数据，具体数据类型参考具体的api说明

### 1.2 失败

```json
{
  "error": {
    "errorCode": 5000,
    "errorMessage": "未预期的异常！ --Unsupported access ledger[6Gw3cK4uazegy4HjoaM81ck9NgYLNoKyBMb7a1TK1jt3d] !"
  },
  "success": false
}

[comment]: <> (```)

说明

    - success 值为 false 表明api调用成功
    - errorCode 为异常代码
    - errorMessage 为错误提示

## 2 账本

### 2.1 获取账本总数

​```http
GET /ledgers/count
```

#### 参数
无


#### 请求实例
```http
http://localhost:8080/ledgers/count
```

#### 返回实例

```json
{
  "data": 2,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|账本总数|


### 2.2 获取账本列表

```http
GET /ledgers
```

#### 参数
无


#### 请求实例
```http
http://localhost:8080/ledgers
```

#### 返回实例

```json
{
   "data" : [
      "j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp"
   ],
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|data|账本哈希列表|

### 2.3 获取账本详细信息

```http
GET /ledgers/{ledger}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型 |
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串 |


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp
```

#### 返回实例

```json
{
   "success" : true,
   "data" : {
      "hash" : "j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp",
      "latestBlockHeight" : 0,
      "latestBlockHash" : "j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp"
   }
}
```

说明

|名称|说明|
|---|---|
|data|账本信息|
|hash|账本哈希|
|latestBlockHash | 最新区块哈希 |
|latestBlockHeight| 最新账本高度 |


### 2.4 获取账本参与方总数

```http
GET /ledgers/{ledger}/participants/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型 |
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串 |


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/participants/count
```

#### 返回实例

```json
{
  "data": 4,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|账本参与方总数|


### 2.5 获取账本参与方列表

```http
GET /ledgers/{ledger}/participants
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型 |
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串 |


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/participants
```

#### 返回实例

```json
{
   "data" : [
      {
         "pubKey" : "7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6",
         "participantNodeState" : "CONSENSUS",
         "id" : 2,
         "address" : {
            "value" : "LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE"
         },
         "name" : "2"
      },
      {
         "participantNodeState" : "CONSENSUS",
         "pubKey" : "7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW",
         "id" : 1,
         "name" : "1",
         "address" : {
            "value" : "LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY"
         }
      },
      {
         "pubKey" : "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn",
         "participantNodeState" : "CONSENSUS",
         "address" : {
            "value" : "LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw"
         },
         "name" : "0",
         "id" : 0
      },
      {
         "id" : 3,
         "address" : {
            "value" : "LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6"
         },
         "name" : "3",
         "pubKey" : "7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr",
         "participantNodeState" : "CONSENSUS"
      }
   ],
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|id|参与方唯一标识|
|name|参与方名称|
|address.value|参与方地址|
|pubKey|参与方公钥|
|participantNodeState|参与方状态|

`ParticipantNodeState` 参与方状态：

- `READY` 就绪，在此状态下，参与方的账户可以作为网关节点接入终端的交易请求
- `CONSENSUS` 共识，在此状态下，参与方的账户可以作为共识节点参与共识，也可以作为网关节点接入终端的交易请求
- `DEACTIVATED` 停用，在此状态下，参与方的账户既不能作为共识节点参与共识，也不能作为网关节点接入终端的交易请求

### 2.6 获取账本元数据信息

```http
GET /ledgers/{ledger}/metadata
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型 |
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串 |


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/metadata
```

#### 返回实例

```json
{
   "success" : true,
   "data" : {
      "participantsHash" : "j5qzkZgpGMczBDNKXRGJdyPTndkxPvB7Gdrfiqy6LvG7P5",
      "userRolesHash" : "j5rggjQQtcjMTSwxFyDedcxPSxVVfjAB3khJDvgdiGFhiB",
      "seed" : "NiSpjQVYRMMEA+1bnyU2eA==",
      "settingsHash" : "j5iNkXJptLJrJcFL1YJpuvbJCh6H3iioBNJG7QKPESUUif",
      "rolePrivilegesHash" : "j5hBCfTVv77mTyM5WHoGnw1cezgzB3QDqojqjhg5qmuECn",
      "ledgerStructureVersion" : 0
   }
}
```

说明

|名称|说明|
|---|---|
|seed|账本生成种子|
|participantsHash|成员数据集哈希|
|userRolesHash|用户角色集哈希|
|settingsHash|配置数据集哈希|
|rolePrivilegesHash|角色权限数据集哈希|
|ledgerStructureVersion|账本结构版本|


### 2.7 获取账本初始化配置信息

```http
GET /ledgers/{ledger}/settings
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/settings
```

#### 返回实例

```json
{
  "data": {
    "ledgerStructureVersion": 0,
    "participantsHash": "j5qzkZgpGMczBDNKXRGJdyPTndkxPvB7Gdrfiqy6LvG7P5",
    "seed": "3624a98d-055844c3-0403ed5b-9f253678",
    "consensusProtocol": "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider",
    "consensusSettings": {
      "systemConfigs": [
        {
          "name": "system.bft",
          "value": "true"
        },
        {
          "value": "true",
          "name": "system.communication.defaultkeys"
        },
        {
          "value": "500000",
          "name": "system.communication.inQueueSize"
        },
        {
          "value": "500000",
          "name": "system.communication.outQueueSize"
        },
        {
          "name": "system.communication.send.retryCount",
          "value": "100"
        },
        {
          "name": "system.communication.send.retryInterval",
          "value": "2000"
        },
        {
          "name": "system.communication.useMACs",
          "value": "1"
        },
        {
          "name": "system.communication.useSenderThread",
          "value": "true"
        },
        {
          "value": "0",
          "name": "system.communication.useSignatures"
        },
        {
          "value": "0",
          "name": "system.debug"
        },
        {
          "value": "0,1,2,3",
          "name": "system.initial.view"
        },
        {
          "name": "system.servers.f",
          "value": "1"
        },
        {
          "value": "4",
          "name": "system.servers.num"
        },
        {
          "value": "true",
          "name": "system.shutdownhook"
        },
        {
          "name": "system.totalordermulticast.checkpoint_period",
          "value": "1000"
        },
        {
          "name": "system.totalordermulticast.checkpoint_to_disk",
          "value": "false"
        },
        {
          "value": "120000",
          "name": "system.totalordermulticast.global_checkpoint_period"
        },
        {
          "name": "system.totalordermulticast.highMark",
          "value": "10000"
        },
        {
          "name": "system.totalordermulticast.log",
          "value": "true"
        },
        {
          "value": "false",
          "name": "system.totalordermulticast.log_parallel"
        },
        {
          "name": "system.totalordermulticast.log_to_disk",
          "value": "true"
        },
        {
          "name": "system.totalordermulticast.maxbatchsize",
          "value": "2000"
        },
        {
          "value": "10",
          "name": "system.totalordermulticast.nonces"
        },
        {
          "value": "10",
          "name": "system.totalordermulticast.revival_highMark"
        },
        {
          "name": "system.totalordermulticast.state_transfer",
          "value": "true"
        },
        {
          "name": "system.totalordermulticast.sync_ckp",
          "value": "false"
        },
        {
          "value": "false",
          "name": "system.totalordermulticast.sync_log"
        },
        {
          "value": "3000000",
          "name": "system.totalordermulticast.timeTolerance"
        },
        {
          "name": "system.totalordermulticast.timeout",
          "value": "60000"
        },
        {
          "name": "system.totalordermulticast.timeout_highMark",
          "value": "200"
        },
        {
          "value": "false",
          "name": "system.totalordermulticast.verifyTimestamps"
        },
        {
          "name": "system.ttp.id",
          "value": "2001"
        }
      ],
      "viewId": 0,
      "nodes": [
        {
          "id": 0,
          "networkAddress": {
            "host": "127.0.0.1",
            "port": 10080,
            "secure": false
          },
          "pubKey": "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn",
          "address": "LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw"
        },
        {
          "pubKey": "7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW",
          "address": "LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY",
          "id": 1,
          "networkAddress": {
            "host": "127.0.0.1",
            "port": 10082,
            "secure": false
          }
        },
        {
          "id": 2,
          "networkAddress": {
            "host": "127.0.0.1",
            "port": 10084,
            "secure": false
          },
          "address": "LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE",
          "pubKey": "7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6"
        },
        {
          "networkAddress": {
            "host": "127.0.0.1",
            "port": 10086,
            "secure": false
          },
          "id": 3,
          "pubKey": "7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr",
          "address": "LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6"
        }
      ]
    },
    "cryptoSetting": {
      "supportedProviders": [
        {
          "algorithms": [
            {
              "name": "AES",
              "code": -32230
            },
            {
              "name": "ED25519",
              "code": 16661
            },
            {
              "code": 16662,
              "name": "ECDSA"
            },
            {
              "name": "RSA",
              "code": -16105
            },
            {
              "name": "RIPEMD160",
              "code": 8217
            },
            {
              "name": "SHA256",
              "code": 8216
            },
            {
              "name": "JVM-SECURE-RANDOM",
              "code": 4123
            }
          ],
          "name": "com.jd.blockchain.crypto.service.classic.ClassicCryptoService"
        },
        {
          "name": "com.jd.blockchain.crypto.service.sm.SMCryptoService",
          "algorithms": [
            {
              "code": -16126,
              "name": "SM2"
            },
            {
              "name": "SM3",
              "code": 8195
            },
            {
              "name": "SM4",
              "code": -32252
            }
          ]
        }
      ],
      "hashAlgorithm": 8216,
      "autoVerifyHash": false
    },
    "participantNodes": [
      {
        "participantNodeState": "CONSENSUS",
        "pubKey": "7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6",
        "address": {
          "value": "LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE"
        },
        "name": "2",
        "id": 2
      },
      {
        "id": 1,
        "participantNodeState": "CONSENSUS",
        "pubKey": "7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW",
        "address": {
          "value": "LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY"
        },
        "name": "1"
      },
      {
        "id": 0,
        "address": {
          "value": "LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw"
        },
        "name": "0",
        "participantNodeState": "CONSENSUS",
        "pubKey": "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"
      },
      {
        "id": 3,
        "participantNodeState": "CONSENSUS",
        "pubKey": "7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr",
        "address": {
          "value": "LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6"
        },
        "name": "3"
      }
    ]
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|ledgerStructureVersion|账本种子信息|
|participantsHash|参与方数据集哈希|
|seed|账本种子信息|
|consensusProtocol|共识协议，以字符串方式显示|
|consensusSettings|共识配置|
|cryptoSetting|加密算法配置|
|participantNodes|参与方节点信息|

### 2.8 获取账本管理数据信息

```http
GET /ledgers/{ledger}/admininfo
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型 |
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串 |


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/admininfo
```

#### 返回实例

```json
{
   "data" : {
      "participants" : [
         {
            "id" : 2,
            "pubKey" : "7VeRFF1ednwhrFoe5cngKwPUJ2N4iFKD9Jt53GxSCc1MmPQ6",
            "name" : "2",
            "participantNodeState" : "CONSENSUS",
            "address" : {
               "value" : "LdeNwsiuo7n6HULWhNKc87PBXJXAfGKFon9RE"
            }
         },
         {
            "name" : "1",
            "pubKey" : "7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW",
            "id" : 1,
            "participantNodeState" : "CONSENSUS",
            "address" : {
               "value" : "LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY"
            }
         },
         {
            "id" : 0,
            "name" : "0",
            "pubKey" : "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn",
            "address" : {
               "value" : "LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw"
            },
            "participantNodeState" : "CONSENSUS"
         },
         {
            "participantNodeState" : "CONSENSUS",
            "address" : {
               "value" : "LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6"
            },
            "name" : "3",
            "pubKey" : "7VeRGE4V9MR7HgAqTrkxGvJvaaKRZ3fAjHUjYzpNBGcjfAvr",
            "id" : 3
         }
      ],
      "participantCount" : 4,
      "metadata" : {
         "ledgerStructureVersion" : 0,
         "seed" : "NiSpjQVYRMMEA+1bnyU2eA==",
         "userRolesHash" : "j5rggjQQtcjMTSwxFyDedcxPSxVVfjAB3khJDvgdiGFhiB",
         "settingsHash" : "j5iNkXJptLJrJcFL1YJpuvbJCh6H3iioBNJG7QKPESUUif",
         "participantsHash" : "j5qzkZgpGMczBDNKXRGJdyPTndkxPvB7Gdrfiqy6LvG7P5",
         "rolePrivilegesHash" : "j5hBCfTVv77mTyM5WHoGnw1cezgzB3QDqojqjhg5qmuECn"
      },
      "settings" : {
         "cryptoSetting" : {
            "supportedProviders" : [
               {
                  "algorithms" : [
                     {
                        "name" : "AES",
                        "code" : -32230
                     },
                     {
                        "name" : "ED25519",
                        "code" : 16661
                     },
                     {
                        "code" : 16662,
                        "name" : "ECDSA"
                     },
                     {
                        "name" : "RSA",
                        "code" : -16105
                     },
                     {
                        "name" : "RIPEMD160",
                        "code" : 8217
                     },
                     {
                        "name" : "SHA256",
                        "code" : 8216
                     },
                     {
                        "name" : "JVM-SECURE-RANDOM",
                        "code" : 4123
                     }
                  ],
                  "name" : "com.jd.blockchain.crypto.service.classic.ClassicCryptoService"
               },
               {
                  "name" : "com.jd.blockchain.crypto.service.sm.SMCryptoService",
                  "algorithms" : [
                     {
                        "code" : -16126,
                        "name" : "SM2"
                     },
                     {
                        "name" : "SM3",
                        "code" : 8195
                     },
                     {
                        "name" : "SM4",
                        "code" : -32252
                     }
                  ]
               }
            ],
            "hashAlgorithm" : 8216,
            "autoVerifyHash" : false
         },
         "consensusProvider" : "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider",
         "consensusSetting" : {
            "value" : "11u5FERE3JChMzbiFcGwjdyyegB6Dq6Nn3s1zB7U9AFs9xaphxosYGwQ2NAAXi2fM2NKf1WKpuvaWz51gsfWRSm7SJiAX9B26hxwmjwtSuGwcSJTZkDcScAFV5Pvv4pnbeZwvzcygEE4PGRYw835PN65eaaFX9114UjmaKeGBexzKwGYDAfc5hGPtpqmeuNurc6yHycRnRV1NNq9ZMUSiSpquiJgh1a8XtaYW9nakSH6jJKYSsX8axhYjMpta8CdBwkMZPZDCMcDojwzdzDQyaovTarACtxUWCdYz88U986k8SB3y5Z3PvU9QpSufCZEMPgLEGThi4PNhCSmdmcCJPEGvrfqfTTXMPDvPxLBZLKkpLHpXvmDiLot2pTCkPRicLhF7bnACW5CbNBkB73TRvdnAgFYBqKqQHuMxoTFyrABGdtWT5niFp8DXsTvv2sKSMxFZWgdDt2x5ax1xVCogzJTgxAQrd2ykDd32dkCdzKQJ6RpA3cJABguKXs6txXoeY2VPCg19mZCezNUW1SvhMVw7Dy1a8HHT2xMZ77MwHTXXfn2bEKNJCAv1PRM1re2Upnqo55am8ssoeAvABMCogKQAM56YsekyuusoYierZmi7pD7o4oNQaPGbzgHs2R9XDjLr2esNvDiJbVkr725PUw5Zgs9nqKg2vo11Z42eyXQxzw3Czg4FNts5xm7BeMwJXkT337cZHT2mJkUp58SUxmEB2TcGnzHXvz7qqdJ5NZRQaY87KgrEzo2xTwm8VW9AegiUNgExo5xxJXsGM1p24NCxgodjGnqx6FhB6JDjubDiU2oC7HAnqhwUF6AKgAQpdhGhvs2ts4dxYu8vEoZ5eyQaM2NXfUbm5jTAUvCaafd4oXZDXik4Jv79oUGtncVg52oj3MKZ4nqicMwoAz7esxDX9BWwyR1fEbU7vGPn9FPh87kdqh5koMF2BLhqnd5puobFsL38wtpmwA62DNtuekvTcTFjE97QMqpRKwzos2Kjnah6k6CyJ9DXkaUT7KE1fjWxuTv9794J93wVzheKjEy5nRqhWKHpj1AEPmrfPB9kS1TRUWGEN5aRSjWUgzydcpXutrPfm4wg3Xhgme5fURvE9Pm8F7pTdKaWS6dXDDm5bqWbQ38mMnmkvta54RL563bRPLS3fqmaRyshjN8V5nfec7LF4Fxj1PJhJ5XWR8tmHSD3Yd311BjZ8HaqtaCb74uvjGzjd6M1bh1KU8r96wMcY31Ldc2551iXU7hx2tmBKCAnJekvfQKRv7NvUphfSLJ5AqhAiibFbuQupkz9RgbcpD4Agm6Vv36CiiTDR9PNneP7xjYifuzSUL5hhJwoEpc9Zr6cdhbkAn7sePDkPMo1WCvvJPV9gR4MufSjCN1uxGTXF2fQCmucZemBJ7gTXc5uuATYyip1DRq4m8zpWQ9sz1wyMbqHVSnFKbcApDYhcxPmuF4FTm36gYbCU3q4TuMQvXUoz4Zfjc5y4kaR7im8ocuPFBCXbYgpUjeta52LTJbw2xtNG7EPdjGPXcvqNj4yQSrdGirGJc7dXoUhsJSki1j2dm2T3hdWXsu5oLmxfadoqQXqazhrd7bQJBViEYri51vkMkf8BJCJF8y8wEdZsDcusYYqSZn1mYToHKjAbu4oxGLnp7AM6TSpMjqdsxNBTCgD4u9Q2GvFKEW5S3W5fQvkPhEYSS5Q12K7oHTi2Cs22oQRADXw5FxRedzvSfuQdeA3RN8XgbSHr72mQXzso6SvLQda4Xnfz9M2hSsxg8QRsQm4dBsZ5aSGnZw45dHKHBNRv2RxmHntFbcc1JwCPhbbBxwE8zWp28pEdF1K1qRSRbNMorXvvNBQZPvjoztLc15u2KcQK3Xczvdmc5naBpqoPWmUxcntsX9STCz5ep7sjf5KAkLxq2XQjruC2mbbVNVvZ6o4vnpQxGDVegMigATbSrNWipCp6mqJA7RDdKomdb21apSANr8V3s2nCjDrqMSjgwSxnUMAfS1BuDuhBF8pJB5YnmWJ2WTFCJKndU5D1UnZPawPmtxpG9m2jxKka1DRBJrWGN4UNYZHmqwpy6pAGiFAjg1Yak5cwsPAyHvaP6AFjVqpZbphccWmch1rKhah2BKhC3Jt2WoCmdYKJB6yNJHXgyNmaesUqYTKuq6yJk12QqQGAsHw9H3PYYTARaEHt1qPo76LB8vzUmcUV9dov6Q1Laa2XvUTkdor5XNycVW5Y4SpiqHwGDAY2ujHnte5E241FEYVXfyJBgMLZwnBXFtZm38WENifVQEk1nDsfoRCMZWyg4KyPUNSCvnqq7BtjrDfGQm4X5pMoHm1MuKeL4bJP"
         }
      }
   },
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|participants|参与方列表|
|participantCount|参与方数量|
|metadata|元数据|
|settings|配置信息|
|consensusProvider|共识协议|
|consensusSetting|共识配置|


## 3 区块

### 3.1 获取最新区块

```http
GET /ledgers/{ledger}/blocks/latest
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型 |
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串 |

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/latest
```

#### 返回实例

```json
{
   "data" : {
      "timestamp" : 1615261725429,
      "transactionSetHash" : "j5ssSG7LQC2CgU7fLbMCs2m2JtwN26j5kj2buE2u8F9miP",
      "userAccountSetHash" : "j5hzkPPBJAqKs4rLWbEiFhbh1VW6Jc2xk878X5A6JywPnC",
      "previousHash" : "j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp",
      "hash" : "j5irFH5SJBaCSa6R3QYhbWYMUA2Wos3gvXYUyDYAHVhG23",
      "adminAccountHash" : "j5maavFZShDorv36Sj5W8ijm1wkm2z9GtfdiVvUcfzvwv8",
      "height" : 1,
      "ledgerHash" : "j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp",
      "dataAccountSetHash" : "j5poz1Ced486LxYbcotGvihT42CTiQym98Mg1By8DHZBxJ"
   },
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|hash|区块哈希|
|height|区块高度|
|timestamp|区块创建时间戳|
|ledgerHash|账本哈希|
|previousHash|前置区块哈希|
|transactionSetHash|交易集哈希|
|userAccountSetHash|用户集哈希|
|contractAccountSetHash|合约集哈希|
|adminAccountHash|管理员集哈希|
|dataAccountSetHash|数据账户集哈希|

### 3.2 根据区块哈希获取区块详细信息

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型 |
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_hash|是|区块哈希|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5irFH5SJBaCSa6R3QYhbWYMUA2Wos3gvXYUyDYAHVhG23
```

#### 返回实例

同 [3.1](3.1-获取最新区块)


### 3.3 根据区块高度获取区块详细信息

```http
GET /ledgers/{ledger}/blocks/height/{block_height}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_height|是|区块高度|数字|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/1
```

#### 返回实例

同 [3.1](3.1-获取最新区块)

## 4 交易

### 4.1 获取账本交易总数

```http
GET /ledgers/{ledger}/txs/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/txs/count
```

##### 返回实例

```json
{
  "data": 2,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|交易数量|

### 4.2 根据区块高度查询交易数量

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledgerHash}/blocks/height/{block_height}/txs/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_height|是|区块高度|数字|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/66/txs/count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|交易数量|

### 4.3 根据区块哈希查询交易数量

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/txs/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_hash|是|区块哈希|字符串|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5irFH5SJBaCSa6R3QYhbWYMUA2Wos3gvXYUyDYAHVhG23/txs/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|交易数量|

### 4.4 根据区块高度查询区块内的交易数量

**仅包含当前区块数据**

```http
GET /ledgers/{ledger}/blocks/height/{block_height}/txs/additional-count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_height|是|区块高度|数字|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/66/txs/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|交易数量|

### 4.5 根据区块哈希查询区块内的交易数量

**仅包含当前区块数据**

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/txs/additional-count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_hash|是|区块哈希|字符串|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5irFH5SJBaCSa6R3QYhbWYMUA2Wos3gvXYUyDYAHVhG23/txs/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|交易数量|

### 4.6 获取指定区块高度的交易列表

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledger}/blocks/height/{height}/txs?fromIndex={fromIndex}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|height|是|区块高度|数字|
|query|fromIndex|否|查询交易的起始序号，默认为0|数字|
|query|count|否|查询返回交易的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/66/txs?fromIndex=1&count=1
```

#### 返回实例

```json
{
   "data" : [
      {
         "request" : {
            "transactionContent" : {
               "timestamp" : 1615261725387,
               "ledgerHash" : "j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp",
               "operations" : [
                  {
                     "@type" : "com.jd.blockchain.ledger.DataAccountRegisterOperation",
                     "accountID" : {
                        "pubKey" : "7VeRBZLMWAxcgL9DTjGWn9FHWny54bzDzrgeAH6pCFvEJ5eT",
                        "address" : {
                           "value" : "LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn"
                        }
                     }
                  },
                  {
                     "writeSet" : [
                        {
                           "value" : {
                              "type" : "TEXT",
                              "bytes" : {
                                 "value" : "EMeAB7i"
                              }
                           },
                           "expectedVersion" : -1,
                           "key" : "key"
                        }
                     ],
                     "@type" : "com.jd.blockchain.ledger.DataAccountKVSetOperation",
                     "accountAddress" : {
                        "value" : "LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn"
                     }
                  }
               ]
            },
            "nodeSignatures" : [
               {
                  "pubKey" : "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn",
                  "digest" : "SMJHE64z77GSkBkC8Zw45r8zRhCq3KWgyGMxRo7KXZ1JWjnBBj1WkPGBdS3AUAX3UoWK5ymGxeqaskjTVgHxGtC768"
               }
            ],
            "transactionHash" : "j5siSjjv7H6s61zsMajkGfyhASFnKpthveNDptCrM2gmui",
            "endpointSignatures" : [
               {
                  "digest" : "SMJHE64z77GSkBkC8Zw45r8zRhCq3KWgyGMxRo7KXZ1JWjnBBj1WkPGBdS3AUAX3UoWK5ymGxeqaskjTVgHxGtC768",
                  "pubKey" : "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn"
               }
            ]
         },
         "result" : {
            "executionState" : "SUCCESS",
            "dataSnapshot" : {
               "adminAccountHash" : "j5maavFZShDorv36Sj5W8ijm1wkm2z9GtfdiVvUcfzvwv8",
               "userAccountSetHash" : "j5hzkPPBJAqKs4rLWbEiFhbh1VW6Jc2xk878X5A6JywPnC",
               "dataAccountSetHash" : "j5poz1Ced486LxYbcotGvihT42CTiQym98Mg1By8DHZBxJ"
            },
            "blockHeight" : 1,
            "transactionHash" : "j5siSjjv7H6s61zsMajkGfyhASFnKpthveNDptCrM2gmui"
         }
      }
   ],
   "success" : true
}
```


说明

|名称|说明|
|---|---|
|request|请求数据|
|request.transactionContent|交易内容|
|request.transactionContent.timestamp|交易时间戳，毫秒|
|request.transactionContent.ledgerHash|账本哈希|
|request.transactionContent.operations|操作列表|
|request.nodeSignatures|参与方签名列表|
|request.endpointSignatures|终端用户签名列表|
|request.transactionHash|交易哈希|
|result|交易结果|
|result.executionState|交易执行状态，SUCCESS（成功）|
|result.dataSnapshot|交易执行后数据快照|
|result.blockHeight|最新区块高度|
|result.transactionHash|交易哈希|

### 4.7 获取指定哈希的区块的交易列表

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/txs?fromIndex={fromIndex}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_hash|是|区块哈希|字符串|
|query|fromIndex|否|查询交易的起始序号，默认为0|数字|
|query|count|否|查询返回交易的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5irFH5SJBaCSa6R3QYhbWYMUA2Wos3gvXYUyDYAHVhG23/txs?fromIndex=1&count=1
```

#### 返回实例

[同 4.4](#4.4-获取指定区块高度的交易列表)


### 4.8 获取交易详细信息

```http
GET /ledgers/{ledger}/txs/hash/{tx_hash}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|tx_hash|是|交易哈希|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/txs/hash/j5siSjjv7H6s61zsMajkGfyhASFnKpthveNDptCrM2gmui
```

#### 返回实例

```json
{
   "success" : true,
   "data" : {
      "result" : {
         "dataSnapshot" : {
            "dataAccountSetHash" : "j5poz1Ced486LxYbcotGvihT42CTiQym98Mg1By8DHZBxJ",
            "userAccountSetHash" : "j5hzkPPBJAqKs4rLWbEiFhbh1VW6Jc2xk878X5A6JywPnC",
            "adminAccountHash" : "j5maavFZShDorv36Sj5W8ijm1wkm2z9GtfdiVvUcfzvwv8"
         },
         "transactionHash" : "j5siSjjv7H6s61zsMajkGfyhASFnKpthveNDptCrM2gmui",
         "blockHeight" : 1,
         "executionState" : "SUCCESS"
      },
      "request" : {
         "nodeSignatures" : [
            {
               "pubKey" : "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn",
               "digest" : "SMJHE64z77GSkBkC8Zw45r8zRhCq3KWgyGMxRo7KXZ1JWjnBBj1WkPGBdS3AUAX3UoWK5ymGxeqaskjTVgHxGtC768"
            }
         ],
         "endpointSignatures" : [
            {
               "pubKey" : "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn",
               "digest" : "SMJHE64z77GSkBkC8Zw45r8zRhCq3KWgyGMxRo7KXZ1JWjnBBj1WkPGBdS3AUAX3UoWK5ymGxeqaskjTVgHxGtC768"
            }
         ],
         "transactionHash" : "j5siSjjv7H6s61zsMajkGfyhASFnKpthveNDptCrM2gmui",
         "transactionContent" : {
            "timestamp" : 1615261725387,
            "operations" : [
               {
                  "@type" : "com.jd.blockchain.ledger.DataAccountRegisterOperation",
                  "accountID" : {
                     "address" : {
                        "value" : "LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn"
                     },
                     "pubKey" : "7VeRBZLMWAxcgL9DTjGWn9FHWny54bzDzrgeAH6pCFvEJ5eT"
                  }
               },
               {
                  "accountAddress" : {
                     "value" : "LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn"
                  },
                  "@type" : "com.jd.blockchain.ledger.DataAccountKVSetOperation",
                  "writeSet" : [
                     {
                        "value" : {
                           "bytes" : {
                              "value" : "EMeAB7i"
                           },
                           "type" : "TEXT"
                        },
                        "expectedVersion" : -1,
                        "key" : "key"
                     }
                  ]
               }
            ],
            "ledgerHash" : "j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp"
         }
      }
   }
}
```

说明

[同 4.4](#4.4-获取指定区块高度的交易列表)

## 5 用户

### 5.1 获取用户总数

```http
GET /ledgers/{ledger}/users/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/users/count
```

#### 返回实例

```json
{
  "data": 4,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|用户总数|


### 5.2 获取用户列表

```http
GET /ledgers/{ledger}/users?fromIndex={fromIndex}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|query|fromIndex|否|查询用户的起始序号，默认为0|数字|
|query|count|否|查询返回用户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/users?fromIndex=0&count=2
```

#### 返回实例

```json
{
   "success" : true,
   "data" : [
      {
         "pubKey" : "7VeRJpb2XX8XKAaC7G5zQg9DbgKM8gmLhUBtGFmerFbhJTZn",
         "address" : {
            "value" : "LdeNyibeafrAQXgHjBxgQxoLbna6hL4BcXZiw"
         }
      },
      {
         "pubKey" : "7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW",
         "address" : {
            "value" : "LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY"
         }
      }
   ]
}
```

说明

|名称|说明|
|---|---|
|address.value|用户地址|
|pubKey|用户公钥|


### 5.3 获取用户详细信息

```http
GET /ledgers/{ledger}/users/address/{address}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|用户地址|字符串|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/users/address/LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY
```

#### 返回实例

```json
{
   "success" : true,
   "data" : {
      "pubKey" : "7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW",
      "iD" : {
         "pubKey" : "7VeREmuT4fF9yRPEMbSSaNLKbLa3qoTpfGHRgwpnSWUn5tqW",
         "address" : {
            "value" : "LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY"
         }
      },
      "dataRootHash" : "j5uJfAqLw1ptaZYJyKVZm37zZybboqxMPpS6Mv59rNd4xF",
      "headerRootHash" : "j5oYeBmoBJ4jLpijwi6eoKAqh4BsQ3RWHzxdBnTvvTqSK6",
      "dataset" : {
         "dataCount" : 0,
         "updated" : false,
         "readonly" : false,
         "rootHash" : "j5uJfAqLw1ptaZYJyKVZm37zZybboqxMPpS6Mv59rNd4xF"
      },
      "address" : {
         "value" : "LdeNiXZbsBCsTc2ZGp1PGBX81aUxPekhwEwmY"
      }
   }
}
```

说明

|名称|说明|
|---|---|
|address.value|用户地址|
|pubKey|用户公钥|
|dataset.rootHash|用户根Hash|

### 5.4 获取指定高度的区块的用户总数

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledger}/blocks/height/{block_height}/users/count
```

#### 参数

| 请求类型 | 名称         | 是否必需 | 说明     | 数据类型 |
| -------- | ------------ | -------- | -------- | -------- |
| path     | ledger       | 是       | 账本哈希 | 字符串   |
| path     | block_height | 是       | 区块高度 | 数字     |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/users/height/1/users/count
```

#### 返回实例

```json
{
  "data": 4,
  "success": true
}
```

说明

| 名称 | 说明         |
| ---- | ------------ |
| data | 数据账户数量 |

### 5.5 获取指定哈希的区块的用户总数

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/users/count
```

#### 参数

| 请求类型 | 名称       | 是否必需 | 说明     | 数据类型 |
| -------- | ---------- | -------- | -------- | -------- |
| path     | ledger     | 是       | 账本哈希 | 字符串   |
| path     | block_hash | 是       | 区块哈希 | 字符串   |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5opzRbH2fB6YmjvEdMLQ8VHVeZoUuKyVWzExCtJF1wecw/users/count
```

#### 返回实例

```json
{
  "data": 4,
  "success": true
}
```

说明

| 名称 | 说明         |
| ---- | ------------ |
| data | 数据账户数量 |

### 5.6 根据区块高度查询区块内的用户数量

**仅包含当前区块数据**

```http
GET /ledgers/{ledger}/blocks/height/{block_height}/users/additional-count
```

#### 参数

| 请求类型 | 名称         | 是否必需 | 说明     | 数据类型 |
| -------- | ------------ | -------- | -------- | -------- |
| path     | ledger       | 是       | 账本哈希 | 字符串   |
| path     | block_height | 是       | 区块高度 | 数字     |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/2/users/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明     |
| ---- | -------- |
| data | 用户数量 |

### 5.7 根据区块哈希查询区块内的用户数量

**仅包含当前区块数据**

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/users/additional-count
```

#### 参数

| 请求类型 | 名称       | 是否必需 | 说明     | 数据类型 |
| -------- | ---------- | -------- | -------- | -------- |
| path     | ledger     | 是       | 账本哈希 | 字符串   |
| path     | block_hash | 是       | 区块哈希 | 字符串   |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5irFH5SJBaCSa6R3QYhbWYMUA2Wos3gvXYUyDYAHVhG23/users/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明     |
| ---- | -------- |
| data | 用户数量 |

### 5.8 查询最新区块新增用户数量

**仅包含最新区块数据**

```http
GET /ledgers/{ledger}/blocks/users/additional-count
```

#### 参数

| 请求类型 | 名称   | 是否必需 | 说明     | 数据类型 |
| -------- | ------ | -------- | -------- | -------- |
| path     | ledger | 是       | 账本哈希 | 字符串   |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/users/additional-count
```

#### 返回实例

```json
{
  "data": 0,
  "success": true
}
```

说明

| 名称 | 说明     |
| ---- | -------- |
| data | 用户数量 |

## 6 角色权限

有关用户/角色/权限说明参照[用户文档](user.md)

### 6.1 根据角色获取权限信息
```http
GET /ledgers/{ledger}/authorization/role/{roleName}
```

#### 参数

| 请求类型 | 名称     | 是否必需 | 说明     | 数据类型 |
| -------- | -------- | -------- | -------- | -------- |
| path     | ledger   | 是       | 账本哈希 | 字符串   |
| path     | roleName | 是       | 角色名   | 字符串   |


#### 请求实例
```http
http://localhost:8080/ledgers/j5pSJLyVpS8QG2wL95fiDWHHnweh2YdqNhgmnb64SBMjUh/authorization/role/DEFAULT
```

#### 返回实例

```json
{
   "success" : true,
   "data" : {
      "roleName" : "DEFAULT",
      "transactionPrivilege" : {
         "privilege" : [
            "DIRECT_OPERATION",
            "CONTRACT_OPERATION"
         ],
         "permissionCount" : 2
      },
      "version" : 0,
      "ledgerPrivilege" : {
         "privilege" : [
            "CONFIGURE_ROLES",
            "AUTHORIZE_USER_ROLES",
            "SET_CONSENSUS",
            "SET_CRYPTO",
            "REGISTER_PARTICIPANT",
            "REGISTER_USER",
            "REGISTER_DATA_ACCOUNT",
            "REGISTER_CONTRACT",
            "UPGRADE_CONTRACT",
            "SET_USER_ATTRIBUTES",
            "WRITE_DATA_ACCOUNT",
            "APPROVE_TX",
            "CONSENSUS_TX",
            "REGISTER_EVENT_ACCOUNT",
            "WRITE_EVENT_ACCOUNT"
         ],
         "permissionCount" : 15
      }
   }
}
```

说明

| 名称                                 | 说明              |
| ------------------------------------ | ----------------- |
| roleName                             | 角色名称          |
| transactionPrivilege                 | 交易权限          |
| transactionPrivilege.privilege       | 交易权限.权限类别 |
| transactionPrivilege.permissionCount | 交易权限.权限总数 |
| ledgerPrivilege                      | 账本权限          |
| ledgerPrivilege.privilege            | 账本权限.权限类别 |
| ledgerPrivilege.permissionCount      | 账本权限.权限总数 |

### 6.2 根据用户获取权限信息

```http
GET /ledgers/{ledger}/authorization/user/{userAddress}
```

#### 参数

| 请求类型 | 名称        | 是否必需 | 说明     | 数据类型 |
| -------- | ----------- | -------- | -------- | -------- |
| path     | ledger      | 是       | 账本哈希 | 字符串   |
| path     | userAddress | 是       | 用户地址 | 字符串   |


#### 请求实例
```http
http://localhost:8080/ledgers/j5pSJLyVpS8QG2wL95fiDWHHnweh2YdqNhgmnb64SBMjUh/authorization/user/LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6
```

#### 返回实例

```json
{
   "data" : {
      "userAddress" : {
         "value" : "LdeP2ji8PR1DPsLt5NoFeiBnhpckrLHgCJge6"
      },
      "userRole" : [
         "DEFAULT"
      ],
      "ledgerPrivilegesBitset" : {
         "privilege" : [
            "CONFIGURE_ROLES",
            "AUTHORIZE_USER_ROLES",
            "SET_CONSENSUS",
            "SET_CRYPTO",
            "REGISTER_PARTICIPANT",
            "REGISTER_USER",
            "REGISTER_DATA_ACCOUNT",
            "REGISTER_CONTRACT",
            "UPGRADE_CONTRACT",
            "SET_USER_ATTRIBUTES",
            "WRITE_DATA_ACCOUNT",
            "APPROVE_TX",
            "CONSENSUS_TX",
            "REGISTER_EVENT_ACCOUNT",
            "WRITE_EVENT_ACCOUNT"
         ],
         "permissionCount" : 15
      },
      "transactionPrivilegesBitset" : {
         "permissionCount" : 2,
         "privilege" : [
            "DIRECT_OPERATION",
            "CONTRACT_OPERATION"
         ]
      }
   },
   "success" : true
}
```

说明

| 名称                        | 说明       |
| --------------------------- | ---------- |
| userRole                    | 用户角色   |
| transactionPrivilegesBitset | 交易权限集 |
| ledgerPrivilegesBitset      | 账本权限集 |

> 请求链上不存在的用户地址，会返回DEFAULT角色权限列表

## 7 数据账户

### 7.1 获取账户列表

```http
GET /ledgers/{ledger}/accounts?fromIndex={fromIndex}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|query|fromIndex|否|查询数据账户的起始序号，默认为0|数字|
|query|count|否|查询返回数据账户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/accounts?fromIndex=0&count=-1
```

#### 返回实例

```json
{
   "data" : [
      {
         "address" : {
            "value" : "LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn"
         },
         "pubKey" : "7VeRBZLMWAxcgL9DTjGWn9FHWny54bzDzrgeAH6pCFvEJ5eT"
      }
   ],
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey|账户公钥|


### 7.2 获取账户详细信息

```http
GET /ledgers/{ledger}/accounts/address/{address}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|账户地址|字符串|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/accounts/address/LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn
```

#### 返回实例

```json
{
   "data" : {
      "dataset" : {
         "updated" : false,
         "readonly" : false,
         "dataCount" : 1,
         "rootHash" : "j5vjKBpdFncVW9dYnjefiKgs858QKNc98XAvy5pFXmgLKp"
      },
      "headerRootHash" : "j5jUf96A8678xdggUdAZUtADL43WFsFu76gWxT9KkknjLf",
      "dataRootHash" : "j5vjKBpdFncVW9dYnjefiKgs858QKNc98XAvy5pFXmgLKp",
      "pubKey" : "7VeRBZLMWAxcgL9DTjGWn9FHWny54bzDzrgeAH6pCFvEJ5eT",
      "iD" : {
         "pubKey" : "7VeRBZLMWAxcgL9DTjGWn9FHWny54bzDzrgeAH6pCFvEJ5eT",
         "address" : {
            "value" : "LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn"
         }
      },
      "address" : {
         "value" : "LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn"
      }
   },
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey|账户公钥|


### 7.3 获取账户总数

```http
GET /ledgers/{ledger}/accounts/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/accounts/count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|账户数量|

### 7.4 获取指定高度的区块的数据账户总数

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledger}/blocks/height/{block_height}/accounts/count
```

#### 参数

| 请求类型 | 名称         | 是否必需 | 说明     | 数据类型 |
| -------- | ------------ | -------- | -------- | -------- |
| path     | ledger       | 是       | 账本哈希 | 字符串   |
| path     | block_height | 是       | 区块高度 | 数字     |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/1/accounts/count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明         |
| ---- | ------------ |
| data | 数据账户数量 |

### 7.5 获取指定哈希的区块的数据账户总数

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/accounts/count
```

#### 参数

| 请求类型 | 名称       | 是否必需 | 说明     | 数据类型 |
| -------- | ---------- | -------- | -------- | -------- |
| path     | ledger     | 是       | 账本哈希 | 字符串   |
| path     | block_hash | 是       | 区块哈希 | 字符串   |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5opzRbH2fB6YmjvEdMLQ8VHVeZoUuKyVWzExCtJF1wecw/accounts/count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明         |
| ---- | ------------ |
| data | 数据账户数量 |

### 7.6 根据区块高度查询区块内的数据账户数量

**仅包含当前区块数据**

```http
GET /ledgers/{ledger}/blocks/height/{block_height}/accounts/additional-count
```

#### 参数

| 请求类型 | 名称         | 是否必需 | 说明     | 数据类型 |
| -------- | ------------ | -------- | -------- | -------- |
| path     | ledger       | 是       | 账本哈希 | 字符串   |
| path     | block_height | 是       | 区块高度 | 数字     |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/1/accounts/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明         |
| ---- | ------------ |
| data | 数据账户数量 |

### 7.7 根据区块哈希查询区块内的数据账户数量

**仅包含当前区块数据**

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/accounts/additional-count
```

#### 参数

| 请求类型 | 名称       | 是否必需 | 说明     | 数据类型 |
| -------- | ---------- | -------- | -------- | -------- |
| path     | ledger     | 是       | 账本哈希 | 字符串   |
| path     | block_hash | 是       | 区块哈希 | 字符串   |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5irFH5SJBaCSa6R3QYhbWYMUA2Wos3gvXYUyDYAHVhG23/accounts/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明         |
| ---- | ------------ |
| data | 数据账户数量 |

### 7.8 获取某数据账户KV总数

```http
  GET /ledgers/{ledger}/accounts/address/{address}/entries/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|账户地址|字符串|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/accounts/address/LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn/entries/count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|KV总数|


### 7.9 获取某数据账户KV详情

```http
GET /ledgers/{ledger}/accounts/address/{address}/entries?fromIndex={fromIndex}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|账户地址|字符串|
|query|fromIndex|否|查询数据账户对应KV的起始序号，默认为0|数字|
|query|count|否|查询返回数据账户对应KV的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|
#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/accounts/address/LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn/entries?fromIndex=0&count=1
```

#### 返回实例

```json
{
   "data" : [
      {
         "type" : "TEXT",
         "version" : 0,
         "key" : "key",
         "value" : "value"
      }
   ],
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|key|键|
|version|版本号|
|type|value类型|
|value|值|

> 数据类型参照[数据账户](data_account.md)中描述。

### 7.10 获取某数据账户KV详情

```http
GET/POST /ledgers/{ledger}/accounts/{address}/entries?keys={keys}
```

#### 参数

| 请求类型 | 名称    | 是否必需 | 说明            | 数据类型 |
| -------- | ------- | -------- | --------------- | -------- |
| path     | ledger  | 是       | 账本哈希        | 字符串   |
| path     | address | 是       | 账户地址        | 字符串   |
| form     | keys    | 是       | key详细内容列表 | 字符串   |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/accounts/LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn/entries?keys=key,key1
```


#### 返回实例

```json
{
   "success" : true,
   "data" : [
      {
         "key" : "key",
         "value" : 1024,
         "version" : 2,
         "type" : "INT64"
      },
      {
         "type" : "NIL",
         "version" : -1,
         "key" : "key1"
      }
   ]
}
```

说明

| 名称    | 说明      |
| ------- | --------- |
| key     | 键        |
| version | 版本号    |
| type    | value类型 |
| value   | 值        |


### 7.11 获取某数据账户KV整个历史详情

```http
  GET/POST /ledgers/{ledger}/accounts/{address}/entries-version
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|账户地址|字符串|
|body|kvInfoVO|是|Key相关信息|对象|

KVInfoVO对应格式如下：

```json

{
	"data": [{
		"key": "key",
		"version": [0, 1]
	}
}

```

kvInfoVO说明：
  - 支持多个Key作为入参；
  - 每个Key支持多个version；


#### 请求实例
```curl
curl -H 'Content-Type:application/json' --data '{"data":[{"key":"key","version":[0,1]}]}' 'http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/accounts/LdeNscE3MP9a1vgyVUg9LgxQx6yzkUEUS65Rn/entries-version'
```

#### 返回实例

```json
{
   "data" : [
      {
         "type" : "TEXT",
         "key" : "key",
         "value" : "value",
         "version" : 0
      },
      {
         "type" : "BYTES",
         "version" : 1,
         "value" : "Ynl0ZXM=",
         "key" : "key"
      }
   ],
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|key|键|
|version|版本号|
|type|value类型|
|value|值，BASE64编码|

## 8 合约

### 8.1 获取合约总数

```http
GET /ledgers/{ledger}/contracts/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|


#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/contracts/count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明     |
| ---- | -------- |
| data | 合约数量 |

### 8.2 获取指定区块高度的合约总数

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledger}/blocks/height/{blockHeight}/contracts/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|blockHeight|是|区块高度|数字|

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/7/contracts/count
```

#### 返回实例

```json
{
  "data": 2,
  "success": true
}
```

说明

| 名称 | 说明     |
| ---- | -------- |
| data | 合约数量 |



### 8.3 获取指定区块哈希的合约总数

**包含当前区块以及之前区块数据**

```http
GET /ledgers/{ledger}/blocks/hash/{blockHash}/contracts/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|blockHash|是|区块哈希|字符串|


#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5hhTQPyZQHqkvP5UBq3sAaqxq8QUda6asJLzZ2VFUhvQ8/contracts/count
```

#### 返回实例

```json
{
  "data": 2,
  "success": true
}
```

说明

| 名称 | 说明     |
| ---- | -------- |
| data | 合约数量 |

### 8.4 根据区块高度查询区块内的合约总数

**仅包含当前区块数据**

```http
GET /ledgers/{ledger}/blocks/height/{blockHeight}/contracts/additional-count
```

#### 参数

| 请求类型 | 名称        | 是否必需 | 说明     | 数据类型 |
| -------- | ----------- | -------- | -------- | -------- |
| path     | ledger      | 是       | 账本哈希 | 字符串   |
| path     | blockHeight | 是       | 区块高度 | 数字     |

#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/height/7/contracts/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明     |
| ---- | -------- |
| data | 合约数量 |



### 8.5 根据区块哈希查询区块内的合约总数

**仅包含当前区块数据**

```http
GET /ledgers/{ledger}/blocks/hash/{blockHash}/contracts/additional-count
```

#### 参数

| 请求类型 | 名称      | 是否必需 | 说明     | 数据类型 |
| -------- | --------- | -------- | -------- | -------- |
| path     | ledger    | 是       | 账本哈希 | 字符串   |
| path     | blockHash | 是       | 区块哈希 | 字符串   |


#### 请求实例

```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/blocks/hash/j5hhTQPyZQHqkvP5UBq3sAaqxq8QUda6asJLzZ2VFUhvQ8/contracts/additional-count
```

#### 返回实例

```json
{
  "data": 1,
  "success": true
}
```

说明

| 名称 | 说明     |
| ---- | -------- |
| data | 合约数量 |

### 8.6 获取合约列表

```http
GET /ledgers/{ledger}/contracts?fromIndex={fromIndex}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|query|fromIndex|否|查询合约的起始序号，默认为0|数字|
|query|count|否|查询返回合约的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/contracts?fromIndex=0&count=-1
```

#### 返回实例

```json
{
   "data" : [
      {
         "pubKey" : "7VeRCfSaoBW3uRuvTqVb26PYTNwvQ1iZ5HBY92YKpEVN7Qht",
         "address" : {
            "value" : "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye"
         }
      },
      {
         "pubKey" : "7VeRALfcPigCTSEPHSwz7U7TDNLoet85z3nRfr5cYspUWLAR",
         "address" : {
            "value" : "LdeNwApbVMHqTCzNKuynUtJXH7vN2rG8gxHN5"
         }
      }
   ],
   "success" : true
}

```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey|账户公钥|


### 8.7 获取合约详细信息

```http
GET /ledgers/{ledger}/contracts/address/{address}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|合约地址|字符串|

#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/contracts/address/LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye
```

#### 返回实例

```json
{
   "success" : true,
   "data" : {
      "dataRootHash" : "j5uJfAqLw1ptaZYJyKVZm37zZybboqxMPpS6Mv59rNd4xF",
      "chainCode" : "package com.jdchain.samples.contract;\n\nimport com.jd.blockchain.contract.*;\nimport utils.*;\nimport com.jd.blockchain.crypto.*;\nimport com.jd.blockchain.ledger.*;\n\npublic class SampleContractImpl implements EventProcessingAware, SampleContract\n{\n    private ContractEventContext eventContext;\n    \n    public void setKVWithVersion(final String address, final String key, final String value, final long version) {\n        this.eventContext.getLedger().dataAccount(Bytes.fromBase58(address)).setText(key, value, version);\n    }\n    \n    public void setKV(final String address, final String key, final String value) {\n        final TypedKVEntry[] entries = this.eventContext.getUncommittedLedger().getDataEntries(address, new String[] { key });\n        long version = -1L;\n        if (null != entries && entries.length > 0) {\n            version = entries[0].getVersion();\n        }\n        this.eventContext.getLedger().dataAccount(Bytes.fromBase58(address)).setText(key, value, version);\n    }\n    \n    public String registerUser(final String seed) {\n        final CryptoAlgorithm algorithm = Crypto.getAlgorithm(\"ed25519\");\n        final SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);\n        final AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());\n        final BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());\n        this.eventContext.getLedger().users().register(keypair.getIdentity());\n        return keypair.getAddress().toBase58();\n    }\n    \n    public String registerDataAccount(final String seed) {\n        final CryptoAlgorithm algorithm = Crypto.getAlgorithm(\"ed25519\");\n        final SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);\n        final AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());\n        final BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());\n        this.eventContext.getLedger().dataAccounts().register(keypair.getIdentity());\n        return keypair.getAddress().toBase58();\n    }\n    \n    public String registerEventAccount(final String seed) {\n        final CryptoAlgorithm algorithm = Crypto.getAlgorithm(\"ed25519\");\n        final SignatureFunction signFunc = Crypto.getSignatureFunction(algorithm);\n        final AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair(seed.getBytes());\n        final BlockchainKeypair keypair = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());\n        this.eventContext.getLedger().eventAccounts().register(keypair.getIdentity());\n        return keypair.getAddress().toBase58();\n    }\n    \n    public void publishEventWithSequence(final String address, final String topic, final String content, final long sequence) {\n        this.eventContext.getLedger().eventAccount(Bytes.fromBase58(address)).publish(topic, content, sequence);\n    }\n    \n    public void publishEvent(final String address, final String topic, final String content) {\n        final Event event = this.eventContext.getUncommittedLedger().getLatestEvent(address, topic);\n        long sequence = -1L;\n        if (null != event) {\n            sequence = event.getSequence();\n        }\n        this.eventContext.getLedger().eventAccount(Bytes.fromBase58(address)).publish(topic, content, sequence);\n    }\n    \n    public void beforeEvent(final ContractEventContext eventContext) {\n        this.eventContext = eventContext;\n    }\n    \n    public void postEvent(final ContractEventContext eventContext, final Exception error) {\n    }\n}\n",
      "address" : {
         "value" : "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye"
      },
      "headerRootHash" : "j5r72A4PMdDrAU4zhniJNRWM5nKV8kAKMdt6naNrtjRp3A",
      "chainCodeVersion" : 0,
      "pubKey" : "7VeRCfSaoBW3uRuvTqVb26PYTNwvQ1iZ5HBY92YKpEVN7Qht"
   }
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey|账户公钥|
|chainCode|合约源代码|



## 9 用户自定义事件

### 9.1 获取事件账户列表

```http
GET /ledgers/{ledger}/events/user/accounts?fromIndex={fromIndex}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|query|fromIndex|否|查询的起始序号，默认为0|数字|
|query|count|否|查询返回事件账户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/events/user/accounts?fromIndex=0&count=-1
```

#### 返回实例

```json
{
   "data" : [
      {
         "address" : {
            "value" : "LdeNqYND7M82fxrej7ffRPXZwahQZEoUSoUzz"
         },
         "pubKey" : "7VeRJe66QNfuacfSVPzTfXPooFcRmMJKXPYqkUsn4r9v8DqA"
      },
      {
         "pubKey" : "7VeRMwxWNcpMszstXtaxJ1fupauuJpwedB81nMJJQB93LiAJ",
         "address" : {
            "value" : "LdeNiAPuZ5tpYZVrrbELJNjqdvB51PBpNd8QA"
         }
      }
   ],
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey|账户公钥|

### 9.2 获取事件账户

```http
GET /ledgers/{ledger}/events/user/accounts/{address}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|address|账户地址|是|事件账户地址|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/events/user/accounts/LdeNqYND7M82fxrej7ffRPXZwahQZEoUSoUzz
```

#### 返回实例

```json
{
   "success" : true,
   "data" : {
      "address" : {
         "value" : "LdeNqYND7M82fxrej7ffRPXZwahQZEoUSoUzz"
      },
      "pubKey" : "7VeRJe66QNfuacfSVPzTfXPooFcRmMJKXPYqkUsn4r9v8DqA"
   }
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey|账户公钥|

### 9.3 获取事件账户总数

```http
GET /ledgers/{ledger}/events/user/accounts/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/events/user/accounts/count
```

#### 返回实例

```json
{
   "success" : true,
   "data" : 2
}
```

说明

|名称|说明|
|---|---|
|data|事件账户数量|

### 9.4 获取事件名数量

```http
GET /ledgers/{ledger}/events/user/accounts/{address}/names/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|事件账户地址|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/events/user/accounts/LdeNiAPuZ5tpYZVrrbELJNjqdvB51PBpNd8QA/names/count
```

#### 返回实例

```json
{
   "data" : 5,
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|data|事件名数量|

### 9.5 获取事件名列表

```http
GET /ledgers/{ledger}/events/user/accounts/{address}/names?fromIndex={fromIndex}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|事件账户地址|字符串|
|query|fromIndex|否|查询的起始序号，默认为0|数字|
|query|count|否|查询返回事件账户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/events/user/accounts/LdeNiAPuZ5tpYZVrrbELJNjqdvB51PBpNd8QA/names?fromIndex=0&count=-1
```

#### 返回实例

```json
{
   "data" : [
      "topic4",
      "topic3",
      "topic",
      "topic1",
      "topic2"
   ],
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|data|事件名数量数组|

### 9.6 获取最新事件

```http
GET /ledgers/{ledger}/events/user/accounts/{address}/names/{event_name}/latest
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|事件账户地址|字符串|
|path|event_name|是|事件名|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/events/user/accounts/LdeNiAPuZ5tpYZVrrbELJNjqdvB51PBpNd8QA/names/topic/latest
```

#### 返回实例

```json
{
   "success" : true,
   "data" : {
      "sequence" : 0,
      "blockHeight" : 8,
      "transactionSource" : "j5p868BwtU4w5BxG7gnuhQCFqpAgddVzTWNEKMAzZ8bnrF",
      "eventAccount" : {
         "value" : "LdeNiAPuZ5tpYZVrrbELJNjqdvB51PBpNd8QA"
      },
      "name" : "topic",
      "contractSource" : "",
      "content" : {
         "bytes" : {
            "value" : "4mZ4ZZRGDZ"
         },
         "nil" : false,
         "value" : "content",
         "type" : "TEXT"
      }
   }
}
```

说明

|名称|说明|
|---|---|
|sequence|事件序列|
|transactionSource|交易哈希|
|blockHeight|时间产生区块高度|
|contractSource|合约地址|
|eventAccount.value|事件账户地址|
|name|事件名|
|content.nil|事件内容是否为空|
|content.bytes.value|事件内容字节，BASE64编码|
|content.type|事件内容类型|
|content.value|事件内容|

### 9.7 获取事件数量

```http
GET /ledgers/{ledger}/events/user/accounts/{address}/names/{event_name}/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|事件账户地址|字符串|
|path|event_name|是|事件名|字符串|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/events/user/accounts/LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1/names/topic/count
```

#### 返回实例

```json
{
	"data": 1,
	"success": true
}
```

说明

|名称|说明|
|---|---|
|data|事件数量|

### 9.8 获取事件列表

```http
GET /ledgers/{ledger}/events/user/accounts/{address}/names/{event_name}?fromSequence={fromSequence}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|事件账户地址|字符串|
|path|event_name|是|事件名|字符串|
|query|fromSequence|否|查询的起始序号，默认为0|数字|
|query|count|否|查询返回事件的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost:8080/ledgers/j5hQrVB8y78xXbDR9vB92WBjiJShH36G7YLdQYsxtRxkpp/events/user/accounts/LdeNiAPuZ5tpYZVrrbELJNjqdvB51PBpNd8QA/names/topic?fromSequenct=0&count=-1
```

#### 返回实例

```json
{
   "data" : [
      {
         "content" : {
            "value" : "content",
            "nil" : false,
            "type" : "TEXT",
            "bytes" : {
               "value" : "4mZ4ZZRGDZ"
            }
         },
         "eventAccount" : {
            "value" : "LdeNiAPuZ5tpYZVrrbELJNjqdvB51PBpNd8QA"
         },
         "contractSource" : "",
         "name" : "topic",
         "sequence" : 0,
         "transactionSource" : "j5p868BwtU4w5BxG7gnuhQCFqpAgddVzTWNEKMAzZ8bnrF",
         "blockHeight" : 8
      }
   ],
   "success" : true
}
```

说明

|名称|说明|
|---|---|
|data|事件列表|
|sequence|事件序列|
|transactionSource|交易哈希|
|blockHeight|时间产生区块高度|
|contractSource|合约地址|
|eventAccount.value|事件账户地址|
|name|事件名|
|content.nil|事件内容是否为空|
|content.bytes.value|事件内容字节，BASE64编码|
|content.type|事件内容类型|
|content.value|事件内容|