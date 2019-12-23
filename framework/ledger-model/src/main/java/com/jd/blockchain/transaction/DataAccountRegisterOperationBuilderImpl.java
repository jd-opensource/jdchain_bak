package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;

public class DataAccountRegisterOperationBuilderImpl implements DataAccountRegisterOperationBuilder{

	@Override
	public DataAccountRegisterOperation register(BlockchainIdentity userID) {
		return new DataAccountRegisterOpTemplate(userID);
	}
	
	

}
