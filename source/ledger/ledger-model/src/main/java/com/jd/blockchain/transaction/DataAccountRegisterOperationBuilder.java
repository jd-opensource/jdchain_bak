package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;

public interface DataAccountRegisterOperationBuilder {

	/**
	 * @param id
	 * @return
	 */
	DataAccountRegisterOperation register(BlockchainIdentity id);

}
