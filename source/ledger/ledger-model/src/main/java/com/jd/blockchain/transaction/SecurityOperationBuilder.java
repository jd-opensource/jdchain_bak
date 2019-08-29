package com.jd.blockchain.transaction;

public interface SecurityOperationBuilder {

	/**
	 * 注册；
	 * 
	 * @param id
	 *            区块链身份；
	 * @param stateType
	 *            负载类型；
	 * @return
	 */
	RolesConfigurer roles();

}
