package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.TypedBytesValue;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.ledger.TypedBytesValue;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;
import com.jd.blockchain.utils.VersioningMap;

/**
 * Super Type of concrete accounts, e.g. UserAccount, DataAccount,
 * ContractAccount etc.;
 * 
 * @author huanghaiquan
 *
 * @param <H> Type of Account Header;
 */
public class GenericAccount implements MerkleAccountHeader, MerkleProvable, Transactional {

	private MerkleAccount merkleAccount;


	/**
	 * 头部类型；
	 * 
	 * @param headerType
	 */
	public GenericAccount(MerkleAccount merkleAccount) {
		this.merkleAccount = merkleAccount;
	}

	@Override
	public HashDigest getRootHash() {
		return merkleAccount.getRootHash();
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		key = buildInnerKey(key);
		return merkleAccount.getProof(key);
	}


	@Override
	public BlockchainIdentity getID() {
		return merkleAccount.getID();
	}

	public TypedMap getDataset() {
		VersioningMap<Bytes, BytesValue> state = merkleAccount.getDataset();
		return new TypedMap(state);
	}


	@Override
	public boolean isUpdated() {
		return merkleAccount.isUpdated();
	}

	boolean isReadonly() {
		return merkleAccount.isReadonly();
	}

	@Override
	public void commit() {
		merkleAccount.commit();
	}

	@Override
	public void cancel() {
		merkleAccount.cancel();
	}

	private Bytes buildInnerKey(Bytes key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
