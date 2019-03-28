package com.jd.blockchain.contract;

public interface ContractServiceProvider {

	String getName();

	/**
	 * Return the contract code execution engine instance;
	 * 
	 * @return
	 */
	ContractEngine getEngine();
	
}
