package com.jd.blockchain.sdk;

/**
 * BlockchainEventHandle 维护了一个具体的事件监听实例的状态，提供了在不需要继续监听时进行取消的方法
 * {@link #cancel()}；
 * 
 * @author huanghaiquan
 *
 */
public interface BlockchainEventHandle {

	/**
	 * 要监听的事件类型；
	 * 
	 * @return
	 */
	int getFilteredEventTypes();

	/**
	 * 要监听的交易；如果为 null，则不进行交易过滤；
	 * 
	 * @return
	 */
	String getFilteredTxHash();

	/**
	 * 要监听的账户地址；如果为 null，这不进行账户地址过滤；
	 * 
	 * @return
	 */
	String getFilteredAccountAddress();

	/**
	 * 监听器实例；
	 * 
	 * @return
	 */
	BlockchainEventListener getListener();

	/**
	 * 取消监听；
	 */
	void cancel();

}
