package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.utils.Bytes;

public class EmptyAccountSet<T> implements AccountQuery<T> {
	
	private static final AccountHeader[] EMPTY = {};

	@Override
	public HashDigest getRootHash() {
		return null;
	}

	@Override
	public MerkleProof getProof(Bytes key) {
		return null;
	}

	@Override
	public AccountHeader[] getHeaders(int fromIndex, int count) {
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
