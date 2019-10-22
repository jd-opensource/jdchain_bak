package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.HashProof;
import com.jd.blockchain.ledger.MerkleSnapshot;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.VersioningMap;

public class AccountDecorator implements LedgerAccount, HashProvable, MerkleSnapshot{
	
	private MerkleAccount mklAccount;
	
	public AccountDecorator(MerkleAccount mklAccount) {
		this.mklAccount = mklAccount;
	}
	
	protected VersioningMap<Bytes, BytesValue> getHeaders() {
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
	public VersioningMap<Bytes, BytesValue> getDataset() {
		return mklAccount.getDataset();
	}

}
