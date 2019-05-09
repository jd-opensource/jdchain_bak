package com.jd.blockchain.transaction;

public interface EventOperator {

	/**
	 * 部署合约；
	 * 
	 * @return
	 */
	@Deprecated
	ContractEventSendOperationBuilder contractEvents();
	
	/**
	 * 创建调用合约的代理实例；
	 * 
	 * @param address
	 * @param contractIntf
	 * @return
	 */
	<T> T contract(String address, Class<T> contractIntf);

}