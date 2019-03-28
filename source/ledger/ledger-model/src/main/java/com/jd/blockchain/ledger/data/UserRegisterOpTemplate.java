package com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.DConstructor;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.binaryproto.FieldSetter;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.UserRegisterOperation;

public class UserRegisterOpTemplate implements UserRegisterOperation {

	static {
		DataContractRegistry.register(UserRegisterOperation.class);
	}
	
	private BlockchainIdentity userID;

	public UserRegisterOpTemplate() {
	}

	@DConstructor(name="UserRegisterOpTemplate")
	public UserRegisterOpTemplate(@FieldSetter(name="getUserId", type="BlockchainIdentity") BlockchainIdentity userID) {
		this.userID = userID;
	}
	
	@Override
	public BlockchainIdentity getUserID() {
		return userID;
	}

}
