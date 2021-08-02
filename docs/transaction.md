## 交易



### 1. 请求

> `JD Chain`限制单笔交易数据大小不得超过 `4M`

```java
/**
 * 交易请求；
 */
@DataContract(code= DataCodes.TX_REQUEST)
public interface TransactionRequest {

	/**
	 * 交易哈希；
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	HashDigest getTransactionHash();

	/**
	 * 交易内容；
	 */
	@DataField(order = 2, refContract = true)
	TransactionContent getTransactionContent();

	/**
	 * 终端用户的签名列表；
	 */
	@DataField(order = 3, list = true, refContract = true)
	DigitalSignature[] getEndpointSignatures();
	
	/**
	 * 接入交易的节点的签名；
	 */

	@DataField(order=4, list=true, refContract=true)
	DigitalSignature[] getNodeSignatures();

}
```

交易内容：
```java
/**
 * 交易内容；
 */
@DataContract(code = DataCodes.TX_CONTENT)
public interface TransactionContent {
	
	/**
	 * 执行交易的账本地址；
	 * 
	 * 注：除了账本的创世交易之外，任何交易的账本地址都不允许为 null;
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	HashDigest getLedgerHash();

	/**
	 * 操作列表；
	 */
	@DataField(order = 2, list = true, refContract = true, genericContract = true)
	Operation[] getOperations();

	/**
	 * 生成交易的时间；<br>
	 * 以毫秒为单位，表示距离 1970-1-1 00:00:00 (UTC) 的毫秒数；<br>
	 */
	@DataField(order = 3, primitiveType = PrimitiveType.INT64)
	long getTimestamp();

}
```

### 2. 操作

`Operation`接口实现类均为`JD Chain`交易中支持的操作类型

#### 2.1 共识信息变更

```java
@DataContract(code= DataCodes.TX_OP_CONSENSUS_SETTINGS_UPDATE)
public interface ConsensusSettingsUpdateOperation extends Operation{

	/**
	 * 配置列表
	 */
    @DataField(order = 0, primitiveType = PrimitiveType.BYTES, list = true)
    Property[] getProperties();
}
```

#### 2.2 合约部署
```java
@DataContract(code= DataCodes.TX_OP_CONTRACT_DEPLOY)
public interface ContractCodeDeployOperation extends Operation {
	/**
	 * 合约账户信息
	 */
	@DataField(order=2, refContract = true)
	BlockchainIdentity getContractID();
	
	/**
	 * 合约代码字节
	 */
	@DataField(order=3, primitiveType=PrimitiveType.BYTES)
	byte[] getChainCode();

	/**
	 * 合约版本
	 */
	@DataField(order=5, primitiveType=PrimitiveType.INT64)
	long getChainCodeVersion();
}
```

#### 2.3 合约调用

```java
@DataContract(code = DataCodes.TX_OP_CONTRACT_EVENT_SEND)
public interface ContractEventSendOperation extends Operation {

	/**
	 * 合约地址；
	 */
	@DataField(order = 2, primitiveType = PrimitiveType.BYTES)
	Bytes getContractAddress();

	/**
	 * 合约方法名；
	 * 
	 * @return
	 */
	@DataField(order = 3, primitiveType = PrimitiveType.TEXT)
	String getEvent();

	/**
	 * 合约方法调用参数；
	 * 
	 * @return
	 */
	@DataField(order = 4, refContract = true)
	BytesValueList getArgs();

	/**
	 * 合约版本；
	 */
	@DataField(order = 5, primitiveType = PrimitiveType.INT64)
	long getVersion();
}
```

#### 2.4 注册数据账户

```java
@DataContract(code= DataCodes.TX_OP_DATA_ACC_REG)
public interface DataAccountRegisterOperation extends Operation {
	
	/**
     * 数据账户信息；
     */
    @DataField(order=1, refContract = true)
	BlockchainIdentity getAccountID();

}
```

#### 2.5 写KV操作

```java
@DataContract(code= DataCodes.TX_OP_DATA_ACC_SET)
public interface DataAccountKVSetOperation extends Operation {

	/**
	 * 数据账户
	 */
	@DataField(order=2, primitiveType=PrimitiveType.BYTES)
	Bytes getAccountAddress();

	/**
	 * KV列表
	 */
	@DataField(order=3, list=true, refContract=true)
	KVWriteEntry[] getWriteSet();
	
	
	@DataContract(code=DataCodes.TX_OP_DATA_ACC_SET_KV)
	public static interface KVWriteEntry{

		@DataField(order=1, primitiveType=PrimitiveType.TEXT)
		String getKey();

		@DataField(order=2, refContract = true)
		BytesValue getValue();

		@DataField(order=3, primitiveType=PrimitiveType.INT64)
		long getExpectedVersion();
	}

}
```

#### 2.6 注册事件账户

```java
@DataContract(code = DataCodes.TX_OP_EVENT_ACC_REG)
public interface EventAccountRegisterOperation extends Operation {

    @DataField(order = 2, refContract = true)
    BlockchainIdentity getEventAccountID();

}
```

#### 2.7 发布事件

```java
@DataContract(code = DataCodes.TX_OP_EVENT_PUBLISH)
public interface EventPublishOperation extends Operation {

    /**
     * 事件地址
     */
    @DataField(order = 1, primitiveType = PrimitiveType.BYTES)
    Bytes getEventAddress();

    /**
     * 事件列表
     */
    @DataField(order = 2, list = true, refContract = true)
    EventEntry[] getEvents();


    @DataContract(code = DataCodes.TX_OP_EVENT_PUBLISH_ENTITY)
    interface EventEntry {

        @DataField(order = 1, primitiveType = PrimitiveType.TEXT)
        String getName();

        @DataField(order = 2, refContract = true)
        BytesValue getContent();

        @DataField(order = 3, primitiveType = PrimitiveType.INT64)
        long getSequence();
    }
}
```

#### 2.8 账本初始化

```java
@DataContract(code= DataCodes.TX_OP_LEDGER_INIT)
public interface LedgerInitOperation extends Operation{

	/**
	 * 账本初始化配置
	 */
	@DataField(order=1, refContract=true)
	LedgerInitSetting getInitSetting();
	
}
```

#### 2.9 参与方状态变更

```java
@DataContract(code= DataCodes.TX_OP_PARTICIPANT_STATE_UPDATE)
public interface ParticipantStateUpdateOperation extends Operation {
	/**
	* 参与方身份
	*/
    @DataField(order = 0, refContract = true)
    BlockchainIdentity getParticipantID();

	/**
	* 新状态
	*/
    @DataField(order = 1, refEnum = true)
    ParticipantNodeState getState();
}
```

#### 2.10 角色赋权

```java
@DataContract(code = DataCodes.TX_OP_ROLE_CONFIGURE)
public interface RolesConfigureOperation extends Operation {

	/**
	 * 角色权限列表
	 */
	@DataField(order = 2, refContract = true, list = true)
	RolePrivilegeEntry[] getRoles();

	@DataContract(code = DataCodes.TX_OP_ROLE_CONFIGURE_ENTRY)
	public static interface RolePrivilegeEntry {

		/**
		 * 角色名
		 */
		@DataField(order = 1, primitiveType = PrimitiveType.TEXT)
		String getRoleName();

		/**
		 * 开启的账本权限列表
		 */
		@DataField(order = 2, refEnum = true, list = true)
		LedgerPermission[] getEnableLedgerPermissions();

		/**
		 * 关闭的账本权限列表
		 */
		@DataField(order = 3, refEnum = true, list = true)
		LedgerPermission[] getDisableLedgerPermissions();

		/**
		 * 开启的交易权限列表
		 */
		@DataField(order = 4, refEnum = true, list = true)
		TransactionPermission[] getEnableTransactionPermissions();

		/**
		 * 关闭的交易权限列表
		 */
		@DataField(order = 5, refEnum = true, list = true)
		TransactionPermission[] getDisableTransactionPermissions();

	}
}
```

#### 2.11 用户赋权

```java
@DataContract(code = DataCodes.TX_OP_USER_ROLES_AUTHORIZE)
public interface UserAuthorizeOperation extends Operation {

	/**
	 * 用户角色列表
	 */
	@DataField(order = 2, refContract = true, list = true)
	UserRolesEntry[] getUserRolesAuthorizations();

	@DataContract(code = DataCodes.TX_OP_USER_ROLE_AUTHORIZE_ENTRY)
	public static interface UserRolesEntry {

		/**
		 * 用户地址；
		 */
		@DataField(order = 0, primitiveType = PrimitiveType.BYTES, list = true)
		Bytes[] getUserAddresses();

		/**
		 * 要更新的多角色权限策略；
		 */
		@DataField(order = 2, refEnum = true)
		RolesPolicy getPolicy();

		/**
		 * 授权的角色清单；
		 */
		@DataField(order = 3, primitiveType = PrimitiveType.TEXT, list = true)
		String[] getAuthorizedRoles();

		/**
		 * 取消授权的角色清单；
		 */
		@DataField(order = 4, primitiveType = PrimitiveType.TEXT, list = true)
		String[] getUnauthorizedRoles();

	}
}
```

#### 2.12 注册用户

```java
@DataContract(code = DataCodes.TX_OP_USER_REG)
public interface UserRegisterOperation extends Operation {

	/**
	 * 用户身份信息
	 */
	@DataField(order = 2, refContract = true)
	BlockchainIdentity getUserID();
	
}
```

### 3. 结果

交易执行结果数据结构如下：

```java
@DataContract(code = DataCodes.TX_RESULT)
public interface TransactionResult {

	/**
	 * 交易哈希；
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	HashDigest getTransactionHash();

	/**
	 * 交易被包含的区块高度；
	 */
	@DataField(order = 2, primitiveType = PrimitiveType.INT64)
	long getBlockHeight();

	/**
	 * 交易的执行结果；
	 */
	@DataField(order = 3, refEnum = true)
	TransactionState getExecutionState();

	/**
	 * 交易中操作的返回结果；顺序与操作列表的顺序一致；
	 */
	@DataField(order = 4, list = true, refContract = true)
	OperationResult[] getOperationResults();

	/**
	 * 账本数据快照；
	 */
	@DataField(order = 5, refContract = true)
	LedgerDataSnapshot getDataSnapshot();
}
```

### 4. 查询

`SDK`查询交易详情数据使用`LedgerTransaction`接口实现

#### 4.1 结构

```java
@DataContract(code = DataCodes.TX_RECORD)
public interface LedgerTransaction {

	/**
	 * 交易请求；
	 */
	@DataField(order = 1, refContract = true)
	TransactionRequest getRequest();

	/**
	 * 交易结果；
	 */
	@DataField(order = 2, refContract = true)
	TransactionResult getResult();
}
```

#### 4.2 解析

- 成功/失败：
```java
getResult().getExecutionState();
```
`TransactionState.SUCCESS`为成功，其他失败。

- 操作解析
```java
for(Operation operation : tx.getRequest().getTransactionContent().getOperations()) {
	// 注册用户
    if(operation instanceof UserRegisterOperation) {
        UserRegisterOperation userRegisterOperation = (UserRegisterOperation) operation;
        // ...
    // 注册数据账户
    } else if(operation instanceof DataAccountRegisterOperation) {
        DataAccountRegisterOperation dataAccountRegisterOperation = (DataAccountRegisterOperation) operation;
        // ...
    } // ...
}
```

上诉仅以注册用户/注册数据账户为例，其他操作类型嗯参照[所有操作类型](#2-操作)进行解析。