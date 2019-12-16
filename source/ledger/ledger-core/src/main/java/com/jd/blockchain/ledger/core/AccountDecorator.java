package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.Account;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.HashProof;
import com.jd.blockchain.ledger.MerkleSnapshot;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Dataset;

public class AccountDecorator implements Account, HashProvable, MerkleSnapshot{
	
	private CompositeAccount mklAccount;
	
	public AccountDecorator(CompositeAccount mklAccount) {
		this.mklAccount = mklAccount;
	}
	
	protected Dataset<String, TypedValue> getHeaders() {
		return mklAccount.getHeaders();
	}


	@Override
	public HashDigest getRootHash() {
		return mklAccount.getRootHash();
	}

	@Override
	public HashProof getProof(Bytes key) {
		return mklAccount.getProof(key);
	}

	@Override
	public BlockchainIdentity getID() {
		return mklAccount.getID();
	}

	@Override
	public Dataset<String, TypedValue> getDataset() {
		return mklAccount.getDataset();
	}

}
