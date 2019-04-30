package com.jd.blockchain.transaction;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.DigitalSignature;

public class DataAccountRegisterOpTemplate implements DataAccountRegisterOperation {
	static {
		DataContractRegistry.register(DataAccountKVSetOperation.class);
	}

	private BlockchainIdentity accountID;

	public DataAccountRegisterOpTemplate() {
	}

	public DataAccountRegisterOpTemplate(BlockchainIdentity accountID) {
		this.accountID = accountID;
	}

	@Override
	public BlockchainIdentity getAccountID() {
		return accountID;
	}

	@Override
	public DigitalSignature getAddressSignature() {
		// TODO Auto-generated method stub
		return null;
	}

}
