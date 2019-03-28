package com.jd.blockchain.ledger.data;

public interface EventOperator {
	
	/**
	 * 部署合约；
	 * @return
	 */
	ContractEventSendOperationBuilder contractEvents();

}