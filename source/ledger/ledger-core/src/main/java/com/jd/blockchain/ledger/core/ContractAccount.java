package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

public class ContractAccount implements AccountHeader {

	// private static final String CONTRACT_INFO_PREFIX = "INFO" +
	// LedgerConsts.KEY_SEPERATOR;
	private static final Bytes CONTRACT_INFO_PREFIX = Bytes.fromString("INFO" + LedgerConsts.KEY_SEPERATOR);

	// private static final String CHAIN_CODE_KEY = "CHAIN-CODE";
	private static final Bytes CHAIN_CODE_KEY = Bytes.fromString("CHAIN-CODE");

	private BaseAccount accBase;

	public ContractAccount(BaseAccount accBase) {
		this.accBase = accBase;
	}

	@Override
	public Bytes getAddress() {
		return accBase.getAddress();
	}

	@Override
	public PubKey getPubKey() {
		return accBase.getPubKey();
	}

	@Override
	public HashDigest getRootHash() {
		return accBase.getRootHash();
	}

	public MerkleProof getChaincodeProof() {
		return accBase.getProof(CHAIN_CODE_KEY);
	}

	public MerkleProof getPropertyProof(Bytes key) {
		return accBase.getProof(encodePropertyKey(key));
	}

	public long setChaincode(byte[] chaincode, long version) {
		return accBase.setBytes(CHAIN_CODE_KEY, chaincode, version);
	}

	public byte[] getChainCode() {
		return accBase.getBytes(CHAIN_CODE_KEY);
	}

	public byte[] getChainCode(long version) {
		return accBase.getBytes(CHAIN_CODE_KEY, version);
	}

	public long getChaincodeVersion() {
		return accBase.getKeyVersion(CHAIN_CODE_KEY);
	}

	public long setProperty(Bytes key, String value, long version) {
		return accBase.setBytes(encodePropertyKey(key), BytesUtils.toBytes(value), version);
	}

	public String getProperty(Bytes key) {
		return BytesUtils.toString(accBase.getBytes(encodePropertyKey(key)));
	}

	public String getProperty(Bytes key, long version) {
		return BytesUtils.toString(accBase.getBytes(encodePropertyKey(key), version));
	}

	private Bytes encodePropertyKey(Bytes key) {
		return CONTRACT_INFO_PREFIX.concat(key);
//		return key.concatTo(CONTRACT_INFO_PREFIX);
	}

}