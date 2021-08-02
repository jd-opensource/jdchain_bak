## 事件

`JD Chain`账本中设计了事件数据集，用以存储事件账户，事件数据。

`事件账户`与`用户`，`数据账户`，`合约账户`是相互独立的，所承载的数据也是相互隔离的。

`JD Chain`事件分为两类：[系统事件](#系统事件)，[用户事件](#用户事件)

`JD Chain SDK` 针对事件数据集开发了[事件发布](#事件发布)，[事件监听](#事件监听)实现。

### 1. 系统事件

系统运行期间产生的事件，目前仅定义了`新区块产生`这一个：

```java
/**
 * 系统事件类型
 */
public enum SystemEvent {
    // 新区块
    NEW_BLOCK_CREATED("new_block_created");
}
```

### 2. 用户事件

用户自定义事件类型，需要创建`事件账户`，用户自定义事件名

`事件账户`数量没有限制，一个事件账户内`Topic`（事件名）数量没有限制

同一个`事件账户`的同一个`Topic`所指向的`Content`（事件内容）有`Sequence`（事件序号）的概念，以`Topic`作为唯一标识，同一个`Topic`的更新发布需要提供当前该`Topic`的最高序号，`Topic`不存在时为`-1`。

### 3. 事件结构

```java
/**
 * 事件；
 *
 */
@DataContract(code = DataCodes.EVENT_MESSAGE)
public interface Event {
	/**
	 * 事件名；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.TEXT)
	String getName();

	/**
	 * 事件序号；
	 * 
	 * @return
	 */
	@DataField(order = 2, primitiveType = PrimitiveType.INT64)
	long getSequence();

	/**
	 * 事件内容；
	 * 
	 * @return
	 */
	@DataField(order=3, refContract = true)
	BytesValue getContent();

	/**
	 * 产生事件的交易哈希； 
	 * 
	 * @return
	 */
	@DataField(order = 4, primitiveType = PrimitiveType.BYTES)
	HashDigest getTransactionSource();

	/**
	 * 产生事件的合约地址；
	 * 
	 * @return
	 */
	@DataField(order = 5, primitiveType = PrimitiveType.TEXT)
	String getContractSource();

	/**
	 * 产生事件的区块高度
	 *
	 * @return
	 */
	@DataField(order = 6, primitiveType = PrimitiveType.INT64)
	long getBlockHeight();

	/**
	 * 事件账户地址，系统事件此字段为空
	 *
	 * @return
	 */
	@DataField(order = 7, primitiveType = PrimitiveType.BYTES)
	Bytes getEventAccount();
}
```

### 4. SDK

`JD Chain`事件监听是`SDK`端以`拉`的方式实现，消息可重复消费，需要使用者自行保存消费位置。

以下只描述主要步骤，完整示例代码可参照[JD Chain Samples](samples.md)事件相关部分。

#### 4.1 生成事件账户

> 用户事件才有事件账户

```java
// 新建交易
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);
// 生成事件账户
BlockchainKeypair eventAccount = BlockchainKeyGenerator.getInstance().generate();
System.out.println("事件账户地址：" + eventAccount.getAddress());
// 注册事件账户
txTemp.eventAccounts().register(eventAccount.getIdentity());
```

#### 4.2 事件发布

> 系统事件由系统运行期间自动产生，用户事件可通过SDK发布

```java
// 新建交易
TransactionTemplate txTemp = blockchainService.newTransaction(ledger);

txTemp.eventAccount(Bytes.fromBase58("LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye"))
        .publish("topic1", "content1", -1)
        .publish("topic1", "content2", 0)
        .publish("topic1", "content3", 1)
        .publish("topic2", "content", -1)
        .publish("topic3", 1, -1)
        .publish("topic4", Bytes.fromInt(1), -1);
```

支持发布数据类型：

- `Text`，字符类型
- `Int64`，长整型
- `JSON`，`JSON`串
- `Bytes`，字节数组
- `Timestamp`，时间戳，长整型
- `XML`，`XML`文本
- `Image`，图片字节数据

> 本质上仅支持`String/Long/[]byte`这三种数据类型，`JSON/XML/Image/Timestamp`等起标识作用，用于扩展差异化数据展示等场景需求

`publish("topic1", "content1", -1)`中第三个参数即为事件序号，需要传入`JD Chain`网络中当前`topic1`的最高序号，首次写入时传入`-1`。

#### 4.3 事件监听

- 监听系统事件
```java
// 目前仅有新区快产生事件
blockchainService.monitorSystemEvent(ledger,
        SystemEvent.NEW_BLOCK_CREATED, 0, (eventMessages, eventContext) -> {
            for (Event eventMessage : eventMessages) {
                // content中存放的是当前链上最新高度
                System.out.println("New block:" + eventMessage.getSequence() + ":" + BytesUtils.toLong(eventMessage.getContent().getBytes().toBytes()));
            }
        });
```

- 监听用户事件：
```java
blockchainService.monitorUserEvent(ledger, "LdeNr7H1CUbqe3kWjwPwiqHcmd86zEQz2VRye", "sample-event", 0, (eventMessage, eventContext) -> {

    BytesValue content = eventMessage.getContent();
    switch (content.getType()) {
        case TEXT:
        case XML:
        case JSON:
            System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + content.getBytes().toUTF8String());
            break;
        case INT64:
        case TIMESTAMP:
            System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + BytesUtils.toLong(content.getBytes().toBytes()));
            break;
        default: // byte[], Bytes
            System.out.println(eventMessage.getName() + ":" + eventMessage.getSequence() + ":" + new String(content.getBytes().toBytes()));
            break;
    }
});
```