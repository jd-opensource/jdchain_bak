package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerDataSnapshot;

public class TransactionStagedSnapshot implements LedgerDataSnapshot {

	private HashDigest adminAccountHash;
	private HashDigest userAccountSetHash;
	private HashDigest dataAccountSetHash;
	private HashDigest contractAccountSetHash;

	@Override
	public HashDigest getAdminAccountHash() {
		return adminAccountHash;
	}

	@Override
	public HashDigest getUserAccountSetHash() {
		return userAccountSetHash;
	}

	@Override
	public HashDigest getDataAccountSetHash() {
		return dataAccountSetHash;
	}

	@Override
	public HashDigest getContractAccountSetHash() {
		return contractAccountSetHash;
	}

	public void setAdminAccountHash(HashDigest adminAccountHash) {
		this.adminAccountHash = adminAccountHash;
	}

	public void setUserAccountSetHash(HashDigest userAccountSetHash) {
		this.userAccountSetHash = userAccountSetHash;
	}

	public void setDataAccountSetHash(HashDigest dataAccountSetHash) {
		this.dataAccountSetHash = dataAccountSetHash;
	}

	public void setContractAccountSetHash(HashDigest contractAccountSetHash) {
		this.contractAccountSetHash = contractAccountSetHash;
	}

}
