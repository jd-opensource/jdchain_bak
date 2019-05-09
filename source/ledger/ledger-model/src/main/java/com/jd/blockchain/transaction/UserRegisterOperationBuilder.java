package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.UserRegisterOperation;

public interface UserRegisterOperationBuilder {

	/**
	 * 注册；
	 * 
	 * @param id
	 *            区块链身份；
	 * @param stateType
	 *            负载类型；
	 * @return
	 */
	UserRegisterOperation register(BlockchainIdentity userID);

}
