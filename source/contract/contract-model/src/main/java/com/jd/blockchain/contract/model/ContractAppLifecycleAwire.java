package com.jd.blockchain.contract.model;

/**
 * The contract implements this interface to monitor the life cycle events of the contract application.
 * 
 * 
 * @author huanghaiquan
 *
 */
public interface ContractAppLifecycleAwire extends ContractRuntimeAwire {
	
	void postConstruct();
	
	void beforeDestroy();
	
}
