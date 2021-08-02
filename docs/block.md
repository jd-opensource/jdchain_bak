## 区块

采用`BFT-SMaRt`共识协议，即时出块，单个区块交易数限制默认为`2000`（`bftsmart.config`中参数`system.totalordermulticast.maxbatchsize`）

### 结构

- `LedgerBlock`: 

```java
@DataContract(code = DataCodes.BLOCK)
public interface LedgerBlock extends BlockBody {

	/**
	 * 区块哈希；
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	HashDigest getHash();

}
```

- `BlockBody`: 

```java
@DataContract(code= DataCodes.BLOCK_BODY)
public interface BlockBody extends  LedgerDataSnapshot{
	
	// 上一个区块哈希
	@DataField(order=2, primitiveType = PrimitiveType.BYTES)
	HashDigest getPreviousHash();

    // 账本哈希
	@DataField(order=3, primitiveType = PrimitiveType.BYTES)
	HashDigest getLedgerHash();
	
	// 区块高度
	@DataField(order=4, primitiveType= PrimitiveType.INT64)
	long getHeight();

	// 交易数据集哈希
	@DataField(order=5, primitiveType = PrimitiveType.BYTES)
	HashDigest getTransactionSetHash();
	
	// 区块时间戳，毫秒
	@DataField(order=6, primitiveType = PrimitiveType.INT64)
	long getTimestamp();
}
```

- `LedgerDataSnapshot`: 

```java
@DataContract(code=DataCodes.DATA_SNAPSHOT)
public interface LedgerDataSnapshot {

	// 管理数据集哈希
	@DataField(order=1, primitiveType = PrimitiveType.BYTES)
	HashDigest getAdminAccountHash();
	
	// 用户集哈希
	@DataField(order=2, primitiveType = PrimitiveType.BYTES)
	HashDigest getUserAccountSetHash();
	
	// 数据账户集哈希
	@DataField(order=3, primitiveType = PrimitiveType.BYTES)
	HashDigest getDataAccountSetHash();
	
	// 合约集哈希
	@DataField(order=4, primitiveType = PrimitiveType.BYTES)
	HashDigest getContractAccountSetHash();
	
	// 系统事件集哈希
	@DataField(order=5, primitiveType = PrimitiveType.BYTES)
	HashDigest getSystemEventSetHash();
	
	// 用户事件集哈希
	@DataField(order=6, primitiveType = PrimitiveType.BYTES)
	HashDigest getUserEventSetHash();
}
```