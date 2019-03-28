package com.jd.blockchain.sdk;

import java.util.BitSet;

import com.jd.blockchain.ledger.BlockchainEventType;

public interface BlockchainEventMessage {

	/**
	 * 事件代码；<br>
	 * 
	 * 事件代码是本次事件的所有类型事件码的按位或的结果；<p>
	 * 
	 * 可以按以下方法检查是否包含某个特定事件：<br>
	 * <code>
	 * 
	 * ({@link BlockchainEventType#PAYLOAD_UPDATED} & {@link #getEventCode()}) == {@link BlockchainEventType#PAYLOAD_UPDATED}
	 * 
	 * </code>
	 * @return
	 */
	int getEventCode();

	/**
	 * 区块高度；
	 * 
	 * @return
	 */
	long getLedgerNumber();

	/**
	 * 包含本次事件中的所有成功交易的布隆过滤器（BloomFilter）的值；
	 * 
	 * @return
	 */
	BitSet getBloomFilterOfTxs();

	/**
	 * 包含本次事件中的所有变更账户的布隆过滤器（BloomFilter）的值；
	 * 
	 * @return
	 */
	BitSet getBloomFilterOfAccounts();

}
