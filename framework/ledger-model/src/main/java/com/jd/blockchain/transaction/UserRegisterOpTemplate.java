package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.UserRegisterOperation;

public class UserRegisterOpTemplate implements UserRegisterOperation {

	static {
		DataContractRegistry.register(UserRegisterOperation.class);
	}

	private BlockchainIdentity userID;

	public UserRegisterOpTemplate() {
	}

	public UserRegisterOpTemplate(BlockchainIdentity userID) {
		this.userID = userID;
	}

	@Override
	public BlockchainIdentity getUserID() {
		return userID;
	}

}
