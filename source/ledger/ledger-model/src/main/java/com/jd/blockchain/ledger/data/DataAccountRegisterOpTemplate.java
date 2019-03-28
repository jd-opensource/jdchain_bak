package com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.DConstructor;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.binaryproto.FieldSetter;
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

	@DConstructor(name="DataAccountRegisterOpTemplate")
	public DataAccountRegisterOpTemplate(@FieldSetter(name="getAccountID", type="BlockchainIdentity") BlockchainIdentity accountID) {
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
