# 京东区块链浏览器API文档参考 V_1.4

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
```

说明

    - success 值为 false 表明api调用成功
    - errorCode 为异常代码
    - errorMessage 为错误提示

## 2 账本

### 2.1 获取账本总数

```http
GET /ledgers/count
```

#### 参数
无


#### 请求实例
```http
http://localhost/ledgers/count
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
GET /ledgers?fromIndex={start_index}&count={count}
```

#### 参数
|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|query|start_index|否|查询账本的起始序号，默认为0|数字
|query|count|否|查询返回账本的数量限制，默认最大限制为100，小于0或大于100均返回最大可返回结果集|数字


#### 请求实例
```http
http://localhost/ledgers?fromIndex=0&count=-1
```

#### 返回实例

```json
{
  "data": [
    {
      "value": "657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs"
    }
  ],
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|账本哈希列表|
|value|账户哈希|

### 2.3 获取账本详细信息

```http
GET /ledgers/{ledger}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs
```

#### 返回实例

```json
{
  "data": {
    "hash": {
      "value": "657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs"
    },
    "latestBlockHash": {
      "value": "67XsKWgqZTBz1NsytKGpyNWHMbMRENWcBj8PEDYQnWiDL"
    },
    "latestBlockHeight": 66
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|账本信息|
|hash.value|账本哈希|
|latestBlockHash.value|最新区块哈希
|latestBlockHeight|账本高度


### 2.4 获取账本成员总数

```http
GET /ledgers/{ledger}/participants/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/participants/count
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
|data|账本成员总数|


### 2.5 获取账本成员列表

```http
GET /ledgers/{ledger}/participants?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|query|start_index|否|查询成员起始序号，默认为0|数字
|query|count|否|查询成员返回数量，默认最大返回100，小于0或大于100均返回最大可返回结果集|数字


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/participants?fromIndex=0&count=-1
```

#### 返回实例

```json
{
  "data": [
    {
      "address": "5SmFzgFtHtpbJwMCsmWTwjNGTk6SeMKU1522",
      "name": "jd.com",
      "id": 0,
      "pubKey": {
        "value": "mb5kbwzACnhK9P1dVxgMPB2ySJLFyJKQbHpH7T9oRK3LpS"
      }
    },
    {
      "address": "5SmA98VknTbZ1Z7fmbNPHBuN2pbD89ogy8Ha",
      "name": "at.com",
      "id": 1,
      "pubKey": {
        "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
      }
    },
    {
      "address": "5SmMWsqV2kbgrRMjyQFtSq1wvYuPzeRVepHG",
      "name": "bt.com",
      "id": 2,
      "pubKey": {
        "value": "mb4AtiGAH7vtPufMDuap2oca2Ww9X6KTkp59Eh5nZjXA5H"
      }
    },
    {
      "address": "5Sm5QFyvN1dVB4GHFxWhDCp8vsJbNkdx31Ds",
      "name": "xt.com",
      "id": 3,
      "pubKey": {
        "value": "mb7pGhmmjqYUhxrJJ57C1YxXr9h1AWXv8QVosETyuLhVvH"
      }
    }
  ],
  "success": true
}
```

说明

|名称|说明|
|---|---|
|id|成员唯一标识|
|name|成员名称|
|address|成员地址|
|pubKey.value|成员公钥|



### 2.6 获取账本元数据信息

```http
GET /ledgers/{ledger}/metadata
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/metadata
```

#### 返回实例

```json
{
	"data": {
		"participantsHash": {
			"value": "j5hQErg4epzNh38FR3EABx8YJqPkLYZoY828giAyKpCXMd"
		},
		"seed": "ky3+I/4jIy8oPzL63TKqdoMiyi9WI2zacTazIssyP/4=",
		"setting": {
			"consensusProvider": "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider",
			"cryptoSetting": {
				"supportedProviders": [{
					"algorithms": [{}, {}, {}, {}, {}, {}, {}],
					"name": "com.jd.blockchain.crypto.service.classic.ClassicCryptoService"
				}, {
					"algorithms": [{}, {}, {}],
					"name": "com.jd.blockchain.crypto.service.sm.SMCryptoService"
				}],
				"autoVerifyHash": true,
				"hashAlgorithm": 8216
			},
			"consensusSetting": {
				"value": "112ky33NcTKBkV..."
			}
		}
	},
	"success": true
}

```

说明

|名称|说明|
|---|---|
|seed|账本生成种子|
|consensusSetting|共识配置，每种共识不同，需独立解析|




### 2.7 获取账本配置信息

```http
GET /ledgers/{ledger}/settings
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/settings
```

#### 返回实例

```json

{
	"data": {
		"consensusProtocol": "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider",
		"consensusSettings": {
			"nodes": [{
				"address": "LdeNgXM4J1SybrdUn71KdPhwBYvJzZ6xUG5Rd",
				"id": 0,
				"networkAddress": {
					"host": "127.0.0.1",
					"port": 26000,
					"secure": false
				},
				"pubKey": {
					"value": "7VeRMXAkf3zoqr4N2RgLZ83xAv7wurqu6Vxak1V1GHv4Kfe3"
				}
			}, {
				"address": "LdeNwbLhiAHQCVxnXsyKLhgcmUi2fuwYd6jkh",
				"id": 1,
				"networkAddress": {
					"host": "127.0.0.1",
					"port": 26010,
					"secure": false
				},
				"pubKey": {
					"value": "7VeRAt79WPnMQ7TsM5cfhy2ERwVLu8fXbDezfFb6bT9BgYaZ"
				}
			}, {
				"address": "LdeNgVjFaUTzsSHySB3ZrBaLiEeZebLkkyWFP",
				"id": 2,
				"networkAddress": {
					"host": "127.0.0.1",
					"port": 26020,
					"secure": false
				},
				"pubKey": {
					"value": "7VeR9rnFNzgECCYGF8V3G36xhF3X9rr6YzqfN8h6CojgLpEY"
				}
			}, {
				"address": "LdeNgVAkBAAdKLD1z3Nb7n5vtGujfqWdhx8G7",
				"id": 3,
				"networkAddress": {
					"host": "127.0.0.1",
					"port": 26030,
					"secure": false
				},
				"pubKey": {
					"value": "7VeRPkXMyHFvpPf2jayHjiLX8H7CAcTJpF9F1FARogiroSK2"
				}
			}],
			"systemConfigs": [{
				"name": "system.bft",
				"value": "true"
			}, {
				"name": "system.communication.defaultkeys",
				"value": "true"
			}, {
				"name": "system.communication.inQueueSize",
				"value": "500000"
			}, {
				"name": "system.communication.outQueueSize",
				"value": "500000"
			}, {
				"name": "system.communication.useMACs",
				"value": "1"
			}, {
				"name": "system.communication.useSenderThread",
				"value": "true"
			}, {
				"name": "system.communication.useSignatures",
				"value": "0"
			}, {
				"name": "system.debug",
				"value": "0"
			}, {
				"name": "system.initial.view",
				"value": "0,1,2,3"
			}, {
				"name": "system.servers.f",
				"value": "1"
			}, {
				"name": "system.servers.num",
				"value": "4"
			}, {
				"name": "system.shutdownhook",
				"value": "true"
			}, {
				"name": "system.totalordermulticast.checkpoint_period",
				"value": "1000"
			}, {
				"name": "system.totalordermulticast.checkpoint_to_disk",
				"value": "false"
			}, {
				"name": "system.totalordermulticast.global_checkpoint_period",
				"value": "120000"
			}, {
				"name": "system.totalordermulticast.highMark",
				"value": "10000"
			}, {
				"name": "system.totalordermulticast.log",
				"value": "true"
			}, {
				"name": "system.totalordermulticast.log_parallel",
				"value": "false"
			}, {
				"name": "system.totalordermulticast.log_to_disk",
				"value": "false"
			}, {
				"name": "system.totalordermulticast.maxbatchsize",
				"value": "400"
			}, {
				"name": "system.totalordermulticast.nonces",
				"value": "10"
			}, {
				"name": "system.totalordermulticast.revival_highMark",
				"value": "10"
			}, {
				"name": "system.totalordermulticast.state_transfer",
				"value": "true"
			}, {
				"name": "system.totalordermulticast.sync_ckp",
				"value": "false"
			}, {
				"name": "system.totalordermulticast.sync_log",
				"value": "false"
			}, {
				"name": "system.totalordermulticast.timeout",
				"value": "2000"
			}, {
				"name": "system.totalordermulticast.timeout_highMark",
				"value": "200"
			}, {
				"name": "system.totalordermulticast.verifyTimestamps",
				"value": "false"
			}, {
				"name": "system.ttp.id",
				"value": "7002"
			}]
		},
		"cryptoSetting": {
			"autoVerifyHash": true,
			"hashAlgorithm": 8216,
			"supportedProviders": [{
				"algorithms": [{}, {}, {}, {}, {}, {}, {}],
				"name": "com.jd.blockchain.crypto.service.classic.ClassicCryptoService"
			}, {
				"algorithms": [{}, {}, {}],
				"name": "com.jd.blockchain.crypto.service.sm.SMCryptoService"
			}]
		},
		"participantNodes": [{
			"address": "LdeNgXM4J1SybrdUn71KdPhwBYvJzZ6xUG5Rd",
			"id": 0,
			"name": "a.com",
			"pubKey": {
				"value": "7VeRMXAkf3zoqr4N2RgLZ83xAv7wurqu6Vxak1V1GHv4Kfe3"
			}
		}, {
			"address": "LdeNwbLhiAHQCVxnXsyKLhgcmUi2fuwYd6jkh",
			"id": 1,
			"name": "b.com",
			"pubKey": {
				"value": "7VeRAt79WPnMQ7TsM5cfhy2ERwVLu8fXbDezfFb6bT9BgYaZ"
			}
		}, {
			"address": "LdeNgVjFaUTzsSHySB3ZrBaLiEeZebLkkyWFP",
			"id": 2,
			"name": "c.com",
			"pubKey": {
				"value": "7VeR9rnFNzgECCYGF8V3G36xhF3X9rr6YzqfN8h6CojgLpEY"
			}
		}, {
			"address": "LdeNgVAkBAAdKLD1z3Nb7n5vtGujfqWdhx8G7",
			"id": 3,
			"name": "d.com",
			"pubKey": {
				"value": "7VeRPkXMyHFvpPf2jayHjiLX8H7CAcTJpF9F1FARogiroSK2"
			}
		}],
		"participantsHash": {
			"value": "j5hQErg4epzNh38FR3EABx8YJqPkLYZoY828giAyKpCXMd"
		},
		"seed": "932dfe23-fe23232f-283f32fa-dd32aa76-8322ca2f-56236cda-7136b322-cb323ffe"
	},
	"success": true
}


```

说明

|名称|说明|
|---|---|
|seed|账本种子信息|
|consensusProtocol|共识协议，以字符串方式显示|
|consensusSettings|共识配置，不同共识协议内容不同，上述示例为BFTSmart相关配置|
|cryptoSetting|密码相关配置|
|cryptoSetting.hashAlgorithm|Hash算法Code（8216代表SHA256）|
|cryptoSetting.autoVerifyHash|是否自动校验Hash|
|cryptoSetting.supportedProviders|支持的算法库|
|participantNodes|参与方列表信息|
|participantNodes.id|参与方序号|
|participantNodes.address|参与方地址|
|participantNodes.name|参与方名称|
|participantNodes.pubKey.value|参与方公钥信息|
|participantsHash.value|参与方根Hash|



## 3 区块

### 3.1 获取最新区块

```http
GET /ledgers/{ledger}/blocks/latest
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/latest
```

#### 返回实例

```json
{
  "data": {
    "ledgerHash": {
      "value": "657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs"
    },
    "previousHash": {
      "value": "6EJZnMc9464DCSU2kgi96RyngEv8YeEfVoJNhH3yZ2v5T"
    },
    "transactionSetHash": {
      "value": "6LmZtDpMM7xE8FPChACEmLj1PLhfaoVM2rEHRsrV3ohPN"
    },
    "userAccountSetHash": {
      "value": "67jx7SctrwdSczxxuYjwBocA8fER7V8qcRZUzWamSav5p"
    },
    "contractAccountSetHash": {
      "value": "67ftaBhPDez24NEB9wiiTM3SNcn1XFz5rb7boYhpbbLXN"
    },
    "adminAccountHash": {
      "value": "69KEFp9m5iFyAiyGmJ2qPcVxuT79gMChMf9JkStBZe8aa"
    },
    "dataAccountSetHash": {
      "value": "6LB9gosVWEPG3uvWXkxTcWq22mcwMHVehbiXkavFtr5fZ"
    },
    "hash": {
      "value": "67XsKWgqZTBz1NsytKGpyNWHMbMRENWcBj8PEDYQnWiDL"
    },
    "height": 66
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|hash|区块哈希|
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

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_hash|是|区块哈希|字符串|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/hash/67XsKWgqZTBz1NsytKGpyNWHMbMRENWcBj8PEDYQnWiDL
```

#### 返回实例

[参考](#block-detail)


### 3.3 根据区块高度获取区块详细信息

```http
GET /ledgers/{ledger}/blocks/height/{block_height}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_height|是|区块高度|数字|

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/height/66
```

#### 返回实例

[参考](#block-detail)


### 3.4 根据哈希查询区块总数

```http
  GET /ledgers/{ledger}/blocks/count/search?keyword={keyword}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本，需要完整的账本哈希|string|
|**query**|**keyword**|是| 区块哈希的全部或者一部分|string|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/count/search?keyword=6D5M
```

#### 返回实例

```json
{
  "data": 26,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|查询到的区块总数|

### 3.5 根据哈希查询区块

```http
  GET /ledgers/{ledger}/blocks/search?keyword={keyword}&fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本，需要完整的账本哈希|string|
|**query**|**keyword**|是| 区块哈希的全部或者一部分|string|
|**query**|**start_index**|否| 查询区块结果起始序号，默认为0|string|
|**query**|**count**|否| 查询区块结果返回数量，默认最大值为100，小于0或大于100均返回最大可返回结果集|string|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/search?keyword=6D5M&fromIndex=0&count=-1
```

<a name="query-blocks-result" id="query-blocks-result"></a>
#### 返回实例

```json
{
  "data": {
    "blocks": [
      {
        "hash": "6D5MJZnybT69bXET5QdCZdLGT16rZBJEjxLkANmDuykcb"
      }
    ]
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|blocks|查询到的区块列表|
|hash|区块哈希值|
|height|区块高度|
|txCount|区块内交易数量|

## 4 交易

### 4.1 获取账本交易总数

```http
GET /ledgers/{ledger}/txs/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/txs/count
```

##### 返回实例

```json
{
  "data": 688,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|交易数量|

### 4.2 根据区块高度查询区块内的交易数量

```http
GET /ledgers/{ledger}/blocks/height/{block_height}/txs/additional-count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_height|是|区块高度|数字|

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/height/66/txs/additional-count

http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/hash/6D5MJZnybT69bXET5QdCZdLGT16rZBJEjxLkANmDuykcb/txs/additional-count
```

#### 返回实例

```json
{
  "data": 86,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|交易数量|


### 4.3 根据区块哈希查询区块内的交易数量

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/txs/additional-count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|block_hash|是|区块哈希|字符串|

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/hash/6D5MJZnybT69bXET5QdCZdLGT16rZBJEjxLkANmDuykcb/txs/additional-count
```

#### 返回实例

```json
{
  "data": 86,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|交易数量|


### 4.4 获取指定高度的区块交易列表

```http
GET /ledgers/{ledger}/blocks/height/{height}/txs?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|height|是|区块高度|数字|
|query|start_index|否|查询交易的起始序号，默认为0|数字|
|query|count|否|查询返回交易的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/height/66/txs?fromIndex=0&count=-1
```

#### 返回实例

```json
{
  "data": [
    {
      "blockHeight": 1,
      "executionState": "SUCCESS",
      "transactionContent": {
        "ledgerHash": {
          "value": "657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs"
        },
        "operations": [
          {
            "userID": {
              "address": {
                "value": "5SmBgzsrnY6u9Y7DgSSkXfTkCgp83hiFin3v"
              },
              "pubKey": {
                "value": "mb5kukaqjWtXyAerfHU1JDtVwabSeBU5c3khMZbNh7R8VJ"
              }
            }
          },
          {
            "accountID": {
              "address": {
                "value": "5SmA98VknTbZ1Z7fmbNPHBuN2pbD89ogy8Ha"
              },
              "pubKey": {
                "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
              }
            }
          },
          {
            "contractID": {
              "address": {
                "value": "5SmA98VknTbZ1Z7fmbNPHBuN2pbD89ogy8Ha"
              },
              "pubKey": {
                "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
              }
            },
            "chainCode": "----------"
          },
          {
            "contractAddress": {
              "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
            },
            "event": "----------",
            "args": "----------"
          },
          {
            "writeSet": [{
              "key": "jdchain",
              "value": {
                "type": "TEXT",
                "value": {
                  "value": "----------"
                }
              },
              "expectedVersion": 0
            }],
            "accountAddress": {
              "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
            }
          }
        ],
        "hash": {
          "value": "6BLtM1agb7ERKoN5AJgZKiTjzdS7BpjgzQNYK8ZeDqotA"
        }
      },
      "endpointSignatures": [
        {
          "digest": {
            "value": "42pbfM5YKnf39Gitr4UsjTCzhhnJjwNyi8MnLFYgP4VKewTLzHitzArHEMrCt3hZYUe5ex9XvqtmiCoWpeAbdc31F"
          },
          "pubKey": {
            "value": "mb5kbwzACnhK9P1dVxgMPB2ySJLFyJKQbHpH7T9oRK3LpS"
          }
        }
      ],
      "nodeSignatures": [
        {
          "digest": {
            "value": "66SQ95SbDaApAJhN2NsFx5sfAQTxsWhMW26D5iPqXc1jZU9rJEhRnqT1nzt62ZAcCvsfrjEsay3MxqXYA5tWPoA2U"
          },
          "pubKey": {
            "value": "mb5kbwzACnhK9P1dVxgMPB2ySJLFyJKQbHpH7T9oRK3LpS"
          }
        }
      ]
    }
  ],
  "success": true
}
```


说明

|名称|说明|
|---|---|
|executionState|交易执行结果|
|transactionContent.hash|交易的哈希|
|transactionContent.operations|交易的操作列表|
|endpointSignatures|终端签名列表|
|nodeSignatures|节点的签名列表|

### 4.5 获取指定哈希的区块的交易列表

```http
GET /ledgers/{ledger}/blocks/hash/{block_hash}/txs?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|block_hash|是|区块哈希|字符串|
|query|start_index|否|查询交易的起始序号，默认为0|数字|
|query|count|否|查询返回交易的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/blocks/hash/6D5MJZnybT69bXET5QdCZdLGT16rZBJEjxLkANmDuykcb/txs?fromIndex=0&count=-1
```

#### 返回实例

[参考](#tx-list)


### 4.6 获取交易详细信息

```http
GET /ledgers/{ledger}/txs/hash/{tx_hash}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|tx_hash|是|交易哈希|字符串|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/txs/hash/6BLtM1agb7ERKoN5AJgZKiTjzdS7BpjgzQNYK8ZeDqotA
```

#### 返回实例

```json
{
  "data": {
    "blockHeight": 1,
    "executionState": "SUCCESS",
    "transactionContent": {
      "ledgerHash": {
        "value": "657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs"
      },
      "operations": [
        {
          "userID": {
            "address": {
              "value": "5SmBgzsrnY6u9Y7DgSSkXfTkCgp83hiFin3v"
            },
            "pubKey": {
              "value": "mb5kukaqjWtXyAerfHU1JDtVwabSeBU5c3khMZbNh7R8VJ"
            }
          }
        },
        {
          "accountID": {
            "address": {
              "value": "5SmA98VknTbZ1Z7fmbNPHBuN2pbD89ogy8Ha"
            },
            "pubKey": {
              "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
            }
          }
        },
        {
          "contractID": {
            "address": {
              "value": "5SmA98VknTbZ1Z7fmbNPHBuN2pbD89ogy8Ha"
            },
            "pubKey": {
              "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
            }
          },
          "chainCode": "----------"
        },
        {
          "contractAddress": {
            "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
          },
          "event": "----------",
          "args": "----------"
        },
        {
          "writeSet": [{
            "key": "jdchain",
            "value": {
              "type": "TEXT",
              "value": {
                "value": "----------"
              }
            },
            "expectedVersion": 0
          }],
          "accountAddress": {
            "value": "mbC8hzmYBz2SsLLqwoBXAJiGeHrCnByBEvcaUZWscAiPqR"
          }
        }
      ],
      "hash": {
        "value": "6BLtM1agb7ERKoN5AJgZKiTjzdS7BpjgzQNYK8ZeDqotA"
      }
    },
    "endpointSignatures": [
      {
        "digest": {
          "value": "42pbfM5YKnf39Gitr4UsjTCzhhnJjwNyi8MnLFYgP4VKewTLzHitzArHEMrCt3hZYUe5ex9XvqtmiCoWpeAbdc31F"
        },
        "pubKey": {
          "value": "mb5kbwzACnhK9P1dVxgMPB2ySJLFyJKQbHpH7T9oRK3LpS"
        }
      }
    ],
    "nodeSignatures": [
      {
        "digest": {
          "value": "66SQ95SbDaApAJhN2NsFx5sfAQTxsWhMW26D5iPqXc1jZU9rJEhRnqT1nzt62ZAcCvsfrjEsay3MxqXYA5tWPoA2U"
        },
        "pubKey": {
          "value": "mb5kbwzACnhK9P1dVxgMPB2ySJLFyJKQbHpH7T9oRK3LpS"
        }
      }
    ]
  },
  "success": true
}
```

说明

[参考](#tx-keyword)


### 4.7 根据哈希查询交易总数

```http
  GET /ledgers/{ledgers}/txs/count/search?keyword={keyword}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledgers**|是|所要搜索的账本范围，需要完整的账本哈希|string|
|**query**|**keyword**|是|交易哈希，签名者公钥，或者节点公钥的全部或者部分的|string|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/txs/search?keyword=6BLt
```

#### 返回实例

```json
{
  "data": 36,
  "success": true
}
```


说明

|名称|说明|
|---|---|
|data|指定交易数量|


### 4.8 根据哈希查询交易

```http
  GET /ledgers/{ledgers}/txs/search?keyword={keyword}&fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledgers**|是|所要搜索的账本范围，需要完整的账本哈希|string|
|**query**|**keyword**|是|交易哈希，签名者公钥，或者节点公钥的全部或者部分的|string|
|**query**|**start_index**|否|查询交易的起始序号，默认为0|数字|
|**query**|**count**|否|查询返回交易的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/txs/search?keyword=6BLt
```

<a name="query-txs-result" id="query-txs-result"> </a>
#### 返回

```json
{
  "data": {
    "txs": [
      {
        "hash": "6L3ehswCmC1jqBfvGJP9vaPx8qxkLsieu2aRgYepmkiw3"
      }
    ]
  },
  "success": true
}
```

## 5 用户

### 5.1 获取用户总数

```http
GET /ledgers/{ledger}/users/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/users/count
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
GET /ledgers/{ledger}/users?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|query|start_index|否|查询用户的起始序号，默认为0|数字|
|query|count|否|查询返回用户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/users?fromIndex=0&count=-1
```

#### 返回实例

```json
{
  "data":[{
    "address": {
      "value": "5SmFzgFtHtpbJwMCsmWTwjNGTk6SeMKU1522"
    },
    "pubKey": {
      "value": "mb5kbwzACnhK9P1dVxgMPB2ySJLFyJKQbHpH7T9oRK3LpS"
    },
    "rootHash": {
      "value": "5SmFzgFtHtpbJwMCsmWTwjNGTk6SeMKU1522"
    }
  }],
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|用户地址|
|pubKey.value|用户公钥|


### 5.3 获取用户详细信息

```http
GET /ledgers/{ledger}/users/address/{address}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|address|是|用户地址|字符串

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/users/address/55SmFzgFtHtpbJwMCsmWTwjNGTk6SeMKU1522
```

#### 返回实例

```json
{
  "data": {
    "address": {
      "value": "5SmFzgFtHtpbJwMCsmWTwjNGTk6SeMKU1522"
    },
    "pubKey": {
      "value": "mb5kbwzACnhK9P1dVxgMPB2ySJLFyJKQbHpH7T9oRK3LpS"
    },
    "rootHash": {
      "value": "5SmFzgFtHtpbJwMCsmWTwjNGTk6SeMKU1522"
    }
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|用户地址|
|pubKey.value|用户公钥|
|rootHash.value|用户根Hash|


### 5.4 用户查询数量

```http
  GET /ledgers/{ledger}/users/count/search?keyword={keyword}
```

#### 说明

用户有公钥和地址两个属性，可以通过公钥或者地址查找特定用户数量，也可以返回全部用户的数量

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本，需要完整的账本哈希|string|
|**query**|**keyword**|是| 用户的公钥或者地址的全部或者部分|string|
|**query**|**start_index**|否|查询用户的起始序号，默认为0|数字|
|**query**|**count**|否|查询返回用户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/users/count/search?keyword=5Sm
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
|data|用户数量|

### 5.5 用户查询

```http
  GET /ledgers/{ledger}/users/search?keyword={keyword}&fromIndex={start_index}&count={count}
```

#### 说明

用户有公钥和地址两个属性，可以通过公钥或者地址查找特定用户，也可以返回全部用户的列表

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本，需要完整的账本哈希|string|
|**query**|**keyword**|是| 用户的公钥或者地址的全部或者部分|string|
|**query**|**start_index**|否|查询用户的起始序号，默认为0|数字|
|**query**|**count**|否|查询返回用户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/users/search?keyword=5Sm&fromIndex=0&count=-1
```

<a name="query-users-result" id="query-users-result"></a>
#### 返回实例

```json
{
  "data": {
    "users": [
      {
        "address": {
          "value": "5SmAGKgmXyj5VsVvJgHbYCJ67iTizwSkNpw1"
        },
        "pubKey": {
          "value": "mb97eG4bba2EjrgjXYiD9chAstjg4HaNuV5xgCtSHc5TeB"
        }
      }
    ]
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|用户地址|
|pubKey.value|用户公钥|
|rootHash.value|用户根Hash|

## 6 数据账户

### 6.1 获取账户列表

```http
GET /ledgers/{ledger}/accounts?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|query|start_index|否|查询数据账户的起始序号，默认为0|数字|
|query|count|否|查询返回数据账户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts?fromIndex=0&count=-1
```

#### 返回实例

```json
{
  "data":[{
    "address": {
      "value": "5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa"
    },
    "rootHash": {
      "value": "6GiAH2PBRLnoE724ia83bKVijkKsNuNU5danA4AAi5qMM"
    },
    "pubKey": {
      "value": "mavweXqvKGUAJzSxE9S15pV7c7qe9bgUn5R1HwpqmXVTUs"
    }
  }],
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey.value|账户公钥|
|rootHash.value|默克尔树根哈希|


### 6.2 获取账户详细信息

```http
GET /ledgers/{ledger}/accounts/address/{address}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|address|是|账户地址|字符串

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/address/5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa
```

#### 返回实例

```json
{
  "data": {
    "address": {
      "value": "5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa"
    },
    "rootHash": {
      "value": "6GiAH2PBRLnoE724ia83bKVijkKsNuNU5danA4AAi5qMM"
    },
    "pubKey": {
      "value": "mavweXqvKGUAJzSxE9S15pV7c7qe9bgUn5R1HwpqmXVTUs"
    }
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey.value|账户公钥|
|rootHash.value|默克尔树根哈希|


### 6.3 获取账户总数

```http
GET /ledgers/{ledger}/accounts/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/count
```

#### 返回实例

```json
{
  "data": 18,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|账户数量|


### 6.4 查询数据账户匹配的数量

```http
GET /ledgers/{ledger}/accounts/count/search?keyword={keyword}
```

#### 说明

通过账户的公钥和地址的全部或者部分查询特定账户的总数量，也可以通过KV值的Key来查询含有该Key的账户的总数量


#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本，需要完整的账本哈希|字符串
|**query**|**keyword**|是|数据账户的公钥或者地址的全部或者部分，或者是KV值的key|字符串


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/count/search?keyword=jd
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
|data|账户数量|


### 6.5 查询数据账户

```http
  GET /ledgers/{ledger}/accounts/search?keyword={keyword}&fromIndex={start_index}&count={count}
```

#### 说明

通过账户的公钥和地址的全部或者部分查询特定账户，也可以通过KV值的Key来查询含有该Key的账户

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本，需要完整的账本哈希|string|
|**query**|**keyword**|是| 数据账户的公钥或者地址的全部或者部分，或者是KV值的key|string|
|**query**|**start_index**|否|查询数据账户的起始序号，默认为0|数字|
|**query**|**count**|否|查询返回数据账户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/search?keyword=5Sm5V&fromIndex=0&count=-1
```

<a name="query-accounts-result" id="query-accounts-result"> </a>
#### 返回实例

```json
{
  "data": {
    "accounts": [
      {
        "address": {
          "value": "5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa"
        },
        "pubKey": {
          "value": "mavweXqvKGUAJzSxE9S15pV7c7qe9bgUn5R1HwpqmXVTUs"
        }
      }
    ]
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey.value|账户公钥|
|rootHash.value|数据账户根Hash|


### 6.6 获取某数据账户KV总数

```http
  GET /ledgers/{ledger}/accounts/address/{address}/entries/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|address|是|账户地址|字符串

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/address/5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa/entries/count
```

#### 返回实例

```json
{
  "data": 66,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|KV总数|


### 6.7 获取某数据账户KV详情

```http
  GET/POST /ledgers/{ledger}/accounts/address/{address}/entries?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|address|是|账户地址|字符串
|form|keys|是|key详细内容列表|字符串
|query|start_index|否|查询数据账户对应KV的起始序号，默认为0|数字|
|query|count|否|查询返回数据账户对应KV的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


> keys说明：
  1）keys使用表单方式提交，且keys为需要查询Key的列表，列表中每个Key都需要为完整Key
  2）Key提交方式使用GET或POST均可

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/address/5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa/entries
```

说明：表单提交参数为keys={"jd", "jdchain"}


#### 返回实例

```json
{
  "data": [
  {
    "key": "jd",
    "version": 0,
    "type": "TEXT",
    "value": "www.jd.com"
  },
  {
    "key": "jdchain",
    "version": 0,
    "type": "TEXT",
    "value": "www.blockchain.com"
  }],
  "success": true
}
```

说明

|名称|说明|
|---|---|
|key|键|
|version|版本号|
|type|value类型|
|value|值|




### 6.8 获取某数据账户KV整个历史详情

```http
  GET/POST /ledgers/{ledger}/accounts/{address}/entries-version
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|address|是|账户地址|字符串
|form|KVInfoVO|是|Key相关信息|对象

KVInfoVO对应格式如下：

```json

{
	"data": [{
		"key": "zhangsan",
		"version": [0, 1, 2]
	}, {
		"key": "lisi",
		"version": [0, 1]
	}]
}

```

KVInfoVO说明：
  + 1）支持多个Key作为入参；

  + 2）每个Key支持多个version；


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa/entries-version
```


#### 返回实例

```json
{
  "data": [
  {
    "key": "jd",
    "version": 0,
    "type": "TEXT",
    "value": "www.jd.com"
  },
  {
    "key": "jdchain",
    "version": 0,
    "type": "TEXT",
    "value": "www.blockchain.com"
  }],
  "success": true
}
```

说明

|名称|说明|
|---|---|
|key|键|
|version|版本号|
|type|value类型|
|value|值|

### 6.9 查询某数据账户键数量
```
GET /ledgers/{ledger}/accounts/address/{address}/keys/count/search?keyword={keyword}
```
#### 参数
请求类型 | 名称 | 是否必需 | 说明 | 数据类型
--- | --- | --- | --- | ---
path | ledger | 是 | 所要搜索的账本，需要完整的账本哈希 | string
path | address | 是 | 所要搜索的数据账户地址，需要完整的数据账户地址 | string
query | keyword | 否 | 键的部分字符，空表示全部 | string

#### 请求实例
```
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/address/5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa/keys/count/search?keyword=j
```
#### 返回实例
```
{ "data": 66, "success": true }
```
说明

名称 | 说明
--- | ---
data | 条件查询键总数

### 6.10 查询某数据账户键
```
GET /ledgers/{ledger}/accounts/address/{address}/keys/search?keyword={keyword}&fromIndex={start_index}&count={count}
```
#### 参数
请求类型 | 名称 | 是否必需 | 说明 | 数据类型
--- | --- | --- | --- | ---
path | ledger | 是 | 所要搜索的账本，需要完整的账本哈希 | string
path | address | 是 | 所要搜索的数据账户地址，需要完整的数据账户地址 | string
query | keyword | 否 | 键的部分字符，空表示全部 | string
query | start_index | 否 | 查询数据账户对应Key的起始序号，默认为0 | 数字
query | count | 否 | 查询返回数据账户对应Key的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集 | 数字

#### 请求实例
```
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/accounts/address/5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa/keys/search?keyword=j&fromIndex=0&count=-1
```
#### 返回实例
```
{ "data": [ { "key": "jd" }, { "key": "jdchain" }], "success": true }
```
说明

名称 | 说明
--- | ---
key | 键

## 7 搜索

### 7.1 搜索区块链

```http
  GET /ledgers/{ledger}/all/search?keyword={keyword}&fromIndex={start_index}&count={count}
```

#### 说明

通过关键字搜索区块数据，支持区块哈希，交易哈希，用户公钥和地址，合约公钥和地址，数据账户哈希和地址的搜索

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本，需要完整的账本哈希|string|
|**query**|**keyword**|是|关键字|string|
|**query**|**start_index**|否|查询匹配结果的起始序号，默认为0|数字|
|**query**|**count**|否|查询匹配结果的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/all/search?keyword=5Sm5V&fromIndex=0&count=-1
```

#### 返回实例

```json
{
  "message": "OK",
  "code": 0,
  "data": {
    "blocks": ...,
    "txs": ...,
    "users": ...,
    "accounts": ...,
    "contracts": ...,
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|blocks|[参考](#query-blocks-result)|
|txs|[参考](#query-txs-result)|
|users|[参考](#query-users-result)|
|accounts|[参考](#query-accounts-result)|
|contracts|[参考](#query-contracts-result)|

## 8 合约

### 8.1 获取合约列表

```http
GET /ledgers/{ledger}/contracts?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|query|start_index|否|查询合约的起始序号，默认为0|数字|
|query|count|否|查询返回合约的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/contracts?fromIndex=0&count=-1
```

#### 返回实例

```json
{
  "data": [{
    "address": {
      "value": "5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa"
    },
    "rootHash": {
      "value": "6GiAH2PBRLnoE724ia83bKVijkKsNuNU5danA4AAi5qMM"
    },
    "pubKey": {
      "value": "mavweXqvKGUAJzSxE9S15pV7c7qe9bgUn5R1HwpqmXVTUs"
    }
  }],
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey.value|账户公钥|
|rootHash.value|默克尔树根哈希|


### 8.2 获取合约详细信息

```http
GET /ledgers/{ledger}/contracts/address/{address}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串
|path|address|是|合约地址|字符串

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/contracts/address/5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa
```

#### 返回实例

```json
{
  "data": {
    "address": {
      "value": "5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa"
    },
    "rootHash": {
      "value": "6GiAH2PBRLnoE724ia83bKVijkKsNuNU5danA4AAi5qMM"
    },
    "pubKey": {
      "value": "mavweXqvKGUAJzSxE9S15pV7c7qe9bgUn5R1HwpqmXVTUs"
    }
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey.value|账户公钥|
|rootHash.value|默克尔树根哈希|

### 8.3 获取合约总数

```http
GET /ledgers/{ledger}/contracts/count
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/contracts/count
```

#### 返回实例

```json
{
  "data": 27,
  "success": true
}
```

说明

|名称|说明|
|---|---|
|data|合约数量|


### 8.4 查询指定合约数量

```http
GET /ledgers/{ledger}/contracts/count/search?keyword={keyword}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本范围，需要完整的账本哈希|string|
|**query**|**keyword**|是| 合约的公钥或者地址的全部或者一部分|string|

#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/contracts/count/search?keyword=5Sm2
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
|data|合约数量|

### 8.5 合约查询

```http
  GET /ledgers/{ledger}/contracts/search?keyword={keyword}&fromIndex={start_index}&count={count}
```

#### 说明

合约有公钥和地址两个属性，可以通过合约的这两个属性查询特定合约，也可以返回一个当前所有合约的列表

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|**path**|**ledger**|是|所要搜索的账本范围，需要完整的账本哈希|string|
|**query**|**keyword**|是| 合约的公钥或者地址的全部或者一部分|string|
|**query**|**start_index**|否|查询合约的起始序号，默认为0|数字|
|**query**|**count**|否|查询返回合约的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/contracts/earch?keyword=5Sm2&fromIndex=0&count=-1
```

<a name="query-contracts-result" id="query-contracts-result"></a>
#### 返回

```json
{
  "data": {
    "contracts": [
      {
        "address": {
          "value": "5Sm4gWXrNpDWW9Boi4xZCzZMHboRvEDm29Fa"
        },
        "rootHash": {
          "value": "6GiAH2PBRLnoE724ia83bKVijkKsNuNU5danA4AAi5qMM"
        },
        "pubKey": {
          "value": "mavweXqvKGUAJzSxE9S15pV7c7qe9bgUn5R1HwpqmXVTUs"
        }
      }
    ]
  },
  "success": true
}
```

说明

|名称|说明|
|---|---|
|address.value|合约地址|
|pubKey.value|合约公钥|
|rootHash|合约根Hash|

## 9 用户自定义事件

### 9.1 获取事件账户列表

```http
GET /ledgers/{ledger}/events/user/accounts?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|query|start_index|否|查询的起始序号，默认为0|数字|
|query|count|否|查询返回事件账户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/events/user/accounts?fromIndex=0&count=-1
```

#### 返回实例

```json
{
    "data":[
        {
            "address":{
                "value":"LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1"
            },
            "pubKey":{
                "value":"7VeRBi3xDfT1E11vFs9q5Q9gFo23RR7SoobPzivqxw9Uubzq"
            }
        }
    ],
    "success":true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey.value|账户公钥|

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
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/events/user/accounts/LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1
```

#### 返回实例

```json
{
    "data":[
        {
            "address":{
                "value":"LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1"
            },
            "pubKey":{
                "value":"7VeRBi3xDfT1E11vFs9q5Q9gFo23RR7SoobPzivqxw9Uubzq"
            }
        }
    ],
    "success":true
}
```

说明

|名称|说明|
|---|---|
|address.value|账户地址|
|pubKey.value|账户公钥|

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
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/events/user/accounts/count
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
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/events/user/accounts/LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1/names/count
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
|data|事件名数量|

### 9.5 获取事件名列表

```http
GET /ledgers/{ledger}/events/user/accounts/{address}/names?fromIndex={start_index}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|事件账户地址|字符串|
|query|start_index|否|查询的起始序号，默认为0|数字|
|query|count|否|查询返回事件账户的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/events/user/accounts/LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1/names?fromIndex=0&count=100
```

#### 返回实例

```json
{
	"data": ["test1", "test2"],
	"success": true
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
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/events/user/accounts/LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1/names/test1/latest
```

#### 返回实例

```json
{
	"data": {
		"sequence": 0,
		"transactionSource": {
			"value": "j5rENX3rsdEgi5toeNUUv7ycUUivjNxAUb9Fme6oLCU851"
		},
		"blockHeight": 10,
		"contractSource": "",
		"eventAccount": {
			"value": "LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1"
		},
		"name": "test1",
		"content": {
			"nil": false,
			"bytes": {
				"value": "Ctt6Eur"
			},
			"type": "TEXT",
			"value": "imuge"
		}
	},
	"success": true
}
```

说明

|名称|说明|
|---|---|
|sequence|事件序列|
|transactionSource.value|交易哈希|
|blockHeight|时间产生区块高度|
|contractSource|合约地址|
|eventAccount.value|事件账户地址|
|name|事件名|
|content.nil|事件内容是否为空|
|content.bytes.value|事件内容字节|
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
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/events/user/accounts/LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1/names/test1/count
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
GET /ledgers/{ledger}/events/user/accounts/{address}/names/{event_name}?fromSequence={from_sequence}&count={count}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型|
|---|---|---|---|---|
|path|ledger|是|账本哈希|字符串|
|path|address|是|事件账户地址|字符串|
|path|event_name|是|事件名|字符串|
|query|from_sequence|否|查询的起始序号，默认为0|数字|
|query|count|否|查询返回事件的数量，默认最大返回值为100，小于0或大于100均返回最大可返回结果集|数字|


#### 请求实例
```http
http://localhost/ledgers/657TQAw6ssVoeKniWGwbovk7njvCTvikPambM9eBv6ezs/events/user/accounts/LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1/names/test1?fromSequenct=0&count=100
```

#### 返回实例

```json
{
	"data": [{
		"sequence": 0,
		"transactionSource": {
			"value": "j5rENX3rsdEgi5toeNUUv7ycUUivjNxAUb9Fme6oLCU851"
		},
		"blockHeight": 10,
		"contractSource": "",
		"eventAccount": {
			"value": "LdeP1yuk8Medq3Sph5ur9y1yE6nJ71XRVPPx1"
		},
		"name": "test1",
		"content": {
			"nil": false,
			"bytes": {
				"value": "Ctt6Eur"
			},
			"type": "TEXT",
			"value": "imuge"
		}
	}],
	"success": true
}
```

说明

|名称|说明|
|---|---|
|data|事件列表|
|sequence|事件序列|
|transactionSource.value|交易哈希|
|blockHeight|时间产生区块高度|
|contractSource|合约地址|
|eventAccount.value|事件账户地址|
|name|事件名|
|content.nil|事件内容是否为空|
|content.bytes.value|事件内容字节|
|content.type|事件内容类型|
|content.value|事件内容|

## 10.权限对外提供的API接口使用
### 10.1根据角色获取权限信息
```http
GET /ledgers/{ledgerHash}/authorization/role/{roleName}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|get|ledger|是|账本哈希|字符串
|--|roleName|是|角色名|字符串


#### 请求实例
```http
http://localhost:11000/ledgers/j5pSJLyVpS8QG2wL95fiDWHHnweh2YdqNhgmnb64SBMjUh/authorization/role/DEFAULT
```

#### 返回实例

```json
{
    "data": {
        "roleName": "DEFAULT",
        "transactionPrivilege": {
            "privilege": [
                "DIRECT_OPERATION",
                "CONTRACT_OPERATION"
            ],
            "permissionCount": 2
        },
        "ledgerPrivilege": {
            "privilege": [
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
            "permissionCount": 15
        },
        "version": 0
    },
    "success": true
}
```

说明

|名称|说明|
|---|---|
|roleName|角色名称|
|transactionPrivilege|交易权限|
|transactionPrivilege -> privilege|交易权限->权限类别|
|transactionPrivilege -> permissionCount|交易权限->权限总数|
|ledgerPrivilege|账本权限|
|ledgerPrivilege -> privilege|账本权限->权限类别|
|ledgerPrivilege -> permissionCount|账本权限->权限总数|

### 10.2根据用户获取权限信息

```http
GET /ledgers/{ledgerHash}/authorization/user/{userAddress}
```

#### 参数

|请求类型|名称|是否必需|说明|数据类型
|---|---|---|---|---|
|get|ledger|是|账本哈希|字符串
|--|userAddress|是|用户地址|字符串


#### 请求实例
```http
http://localhost:11000/ledgers/j5pSJLyVpS8QG2wL95fiDWHHnweh2YdqNhgmnb64SBMjUh/authorization/user/LdeNwH71wxtbf1UM8ExRG8qbPnu17MdnRSVva
```

#### 返回实例

```json
{
    "data": {
        "userAddress": {
            "value": "LdeNwH71wxtbf1UM8ExRG8qbPnu17MdnRSVva"
        },
        "transactionPrivilegesBitset": {
            "privilege": [
                "DIRECT_OPERATION"
            ],
            "permissionCount": 1
        },
        "userRole": [
            "MANAGER1",
            "MANAGER0"
        ],
        "ledgerPrivilegesBitset": {
            "privilege": [
                "CONFIGURE_ROLES",
                "REGISTER_USER"
            ],
            "permissionCount": 2
        }
    },
    "success": true
}
```

说明

|名称|说明|
|---|---|
|userRole|用户角色|
|transactionPrivilegesBitset|交易权限集|
|ledgerPrivilegesBitset|账本权限集|
