package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.utils.Bytes;

public class EmptyAccountSet<T> implements AccountQuery<T> {
	
	private static final BlockchainIdentity[] EMPTY = {};

	@Override
	public HashDigest getRootHash() {
		return null;
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		return null;
	}

	@Override
	public BlockchainIdentity[] getHeaders(int fromIndex, int count) {
		return EMPTY;
	}

	@Override
	public long getTotal() {
		return 0;
	}

	@Override
	public boolean contains(Bytes address) {
		return false;
	}

	@Override
	public T getAccount(String address) {
		return null;
	}

	@Override
	public T getAccount(Bytes address) {
		return null;
	}

	@Override
	public T getAccount(Bytes address, long version) {
		return null;
	}

}
