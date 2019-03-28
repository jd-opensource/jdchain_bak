package com.jd.blockchain.ledger;

/**
 * 区块链事件类型；<p>
 * 
 * 每一种事件类型都包含一个事件码；<p>
 * 
 * 在一次事件消息中，可以包含多种事件，而且事件之间具有嵌套关系；<br>
 * 
 * 例如：<p>
 * 
 * 一个区块生成事件 {@link #BLOCK_GENERATED} 含了交易提交事件
 * {@link #TRANSACTION_COMMITED};<p>
 * 
 * 交易提交事件 {@link #TRANSACTION_COMMITED} 必然包含账户更新事件 {@link #ACCOUNT_UPDATED};<p>
 * 
 * 更进一步，账户更新事件 {@link #ACCOUNT_UPDATED} 也必然包含了权限更新事件
 * {@link #PRIVILEGE_UPDATED}、负载数据更新事件 {@link #PAYLOAD_UPDATED}
 * 、合约脚本更新事件{@link #SCRIPT_UPDATED} 、合约脚本执行事件{@link #SCRIPT_INVOKED} 这4种事件中的一种或者多种事件；<p>
 * 
 * 这种嵌套关系，表现在事件的编码中是子事件码的比特位中包含了上级事件码；
 * 
 * @author huanghaiquan
 *
 */
public enum BlockchainEventType {

	/**
	 * 生成新区块；<br>
	 * 
	 * 事件码：1 (0x01)
	 * 
	 */
	BLOCK_GENERATED(1),

	/**
	 * 成功提交新交易；<br>
	 * 
	 * 事件码：3 (0x03)
	 */
	TRANSACTION_COMMITED(3),

	/**
	 * 账户的版本已更新；<br>
	 * 
	 * 事件码：259 (0x103)
	 */
	ACCOUNT_UPDATED(259),

	/**
	 * 账户权限已被更新；<br>
	 * 
	 * 事件码：65795 (0x10103)
	 */
	PRIVILEGE_UPDATED(65795),

	/**
	 * 账户负载数据已被更新；<br>
	 * 
	 * 事件码：131331 (0x20103)
	 */
	PAYLOAD_UPDATED(131331),

	/**
	 * 合约脚本已被更新；<br>
	 * 
	 * 事件码：262403 (0x40103)
	 */
	SCRIPT_UPDATED(262403),

	/**
	 * 合约脚本已被调用；<br>
	 * 
	 * 事件码：524547 (0x80103)
	 */
	SCRIPT_INVOKED(524547);

	public final int CODE;

	private BlockchainEventType(int code) {
		this.CODE = code;
	}
}
