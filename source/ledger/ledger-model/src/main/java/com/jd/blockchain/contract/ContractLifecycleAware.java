package com.jd.blockchain.contract;

/**
 * 合约实现此接口可以监听合约应用的生命周期事件；
 * 
 * 
 * @author huanghaiquan
 *
 */
public interface ContractLifecycleAware extends ContractAware {
	
	void postConstruct();
	
	void beforeDestroy();
	
}
