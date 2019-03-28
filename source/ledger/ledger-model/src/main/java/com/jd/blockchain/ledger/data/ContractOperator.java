package com.jd.blockchain.ledger.data;

public interface ContractOperator {
	
	/**
	 * 部署合约；
	 * @return
	 */
	ContractCodeDeployOperationBuilder contracts();

}