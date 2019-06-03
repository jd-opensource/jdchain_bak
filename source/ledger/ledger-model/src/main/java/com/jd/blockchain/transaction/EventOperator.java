package com.jd.blockchain.transaction;

import com.jd.blockchain.utils.Bytes;

public interface EventOperator {

	// /**
	// * 合约事件；
	// *
	// * @return
	// */
	// @Deprecated
	// ContractEventSendOperationBuilder contractEvents();

	/**
	 * 创建调用合约的代理实例；
	 * 
	 * @param address
	 * @param contractIntf
	 * @return
	 */
	<T> T contract(String address, Class<T> contractIntf);
	
	/**
	 * 创建调用合约的代理实例；
	 * 
	 * @param address
	 * @param contractIntf
	 * @return
	 */
	<T> T contract(Bytes address, Class<T> contractIntf);

}