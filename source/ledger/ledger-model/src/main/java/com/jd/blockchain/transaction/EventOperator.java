package com.jd.blockchain.transaction;

import com.jd.blockchain.utils.Bytes;

public interface EventOperator {

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

//	/**
//	 * 执行合约异步等待应答结果
//	 *
//	 * @param execute
//	 * @return
//	 */
//	<T> EventResult<T> result(ContractEventExecutor execute);
}