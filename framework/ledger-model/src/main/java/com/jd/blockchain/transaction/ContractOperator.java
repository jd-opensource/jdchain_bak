package com.jd.blockchain.transaction;

public interface ContractOperator {
	
	/**
	 * 部署合约；
	 * @return
	 */
	ContractCodeDeployOperationBuilder contracts();

}