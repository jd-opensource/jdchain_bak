package com.jd.blockchain.ledger.core.impl;

import com.jd.blockchain.binaryproto.DConstructor;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.binaryproto.FieldSetter;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.LedgerBlock;

public class LedgerBlockData implements LedgerBlock {

	static {
		DataContractRegistry.register(LedgerBlock.class);
	}

	private HashDigest hash;

	private long height;

	private HashDigest ledgerHash;

	private HashDigest previousHash;

	private HashDigest adminAccountHash;

	private HashDigest userAccountSetHash;

	// private HashDigest userPrivilegeHash;

	private HashDigest dataAccountSetHash;

	// private HashDigest dataPrivilegeHash;

	private HashDigest contractAccountSetHash;

	// private HashDigest contractPrivilegeHash;

	private HashDigest transactionSetHash;

	public LedgerBlockData() {
	}

	public LedgerBlockData(LedgerBlock block) {
		this.hash = block.getHash();
		this.height = block.getHeight();
		this.ledgerHash = block.getLedgerHash();
		this.previousHash = block.getPreviousHash();
		this.adminAccountHash = block.getAdminAccountHash();
		this.userAccountSetHash = block.getUserAccountSetHash();
		this.dataAccountSetHash = block.getDataAccountSetHash();
		this.contractAccountSetHash = block.getContractAccountSetHash();
		this.transactionSetHash = block.getTransactionSetHash();
	}

	public void setAdminAccountHash(HashDigest adminAccountHash) {
		this.adminAccountHash = adminAccountHash;
	}

	public void setUserAccountSetHash(HashDigest userAccountSetHash) {
		this.userAccountSetHash = userAccountSetHash;
	}

	// public void setUserPrivilegeHash(HashDigest userPrivilegeHash) {
	// this.userPrivilegeHash = userPrivilegeHash;
	// }

	public void setDataAccountSetHash(HashDigest dataAccountSetHash) {
		this.dataAccountSetHash = dataAccountSetHash;
	}

	// public void setDataPrivilegeHash(HashDigest dataPrivilegeHash) {
	// this.dataPrivilegeHash = dataPrivilegeHash;
	// }

	public void setContractAccountSetHash(HashDigest contractAccountSetHash) {
		this.contractAccountSetHash = contractAccountSetHash;
	}

	// public void setContractPrivilegeHash(HashDigest contractPrivilegeHash) {
	// this.contractPrivilegeHash = contractPrivilegeHash;
	// }

	public void setTransactionSetHash(HashDigest transactionSetHash) {
		this.transactionSetHash = transactionSetHash;
	}

	@DConstructor(name = "LedgerBlockData")
	public LedgerBlockData(@FieldSetter(name = "getHeight", type = "long") long height,
			@FieldSetter(name = "getLedgerHash", type = "HashDigest") HashDigest ledgerHash,
			@FieldSetter(name = "getPreviousHash", type = "HashDigest") HashDigest previousHash) {
		this.height = height;
		this.ledgerHash = ledgerHash;
		this.previousHash = previousHash;
	}

	@Override
	public HashDigest getHash() {
		return hash;
	}

	@Override
	public HashDigest getPreviousHash() {
		return previousHash;
	}

	@Override
	public HashDigest getLedgerHash() {
		return ledgerHash;
	}

	@Override
	public long getHeight() {
		return height;
	}

	@Override
	public HashDigest getAdminAccountHash() {
		return adminAccountHash;
	}

	@Override
	public HashDigest getUserAccountSetHash() {
		return userAccountSetHash;
	}

	// @Override
	// public HashDigest getUserPrivilegeHash() {
	// return userPrivilegeHash;
	// }

	@Override
	public HashDigest getDataAccountSetHash() {
		return dataAccountSetHash;
	}

	// @Override
	// public HashDigest getDataPrivilegeHash() {
	// return dataPrivilegeHash;
	// }

	@Override
	public HashDigest getContractAccountSetHash() {
		return contractAccountSetHash;
	}

	// @Override
	// public HashDigest getContractPrivilegeHash() {
	// return contractPrivilegeHash;
	// }

	@Override
	public HashDigest getTransactionSetHash() {
		return transactionSetHash;
	}

	public void setHash(HashDigest blockHash) {
		this.hash = blockHash;
	}

	public void setLedgerHash(HashDigest ledgerHash) {
		this.ledgerHash = ledgerHash;
	}

}