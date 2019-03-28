package com.jd.blockchain.sdk;

public interface BlockchainEventService {

	/**
	 * 注册区块链事件监听器；
	 * 
	 * @param filteredEventTypes
	 *            要监听的事件类型；
	 * @param filteredTxHash
	 *            要监听的交易；如果为 null，则不进行交易过滤；
	 * @param filteredAccountAddress
	 *            要监听的账户地址；如果为 null，这不进行账户地址过滤；
	 * @param listener
	 *            监听器实例；
	 */
	BlockchainEventHandle addBlockchainEventListener(int filteredEventTypes, String filteredTxHash,
			String filteredAccountAddress, BlockchainEventListener listener);

}