package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.TypedBytesValue;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.ContractInfo;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.utils.Bytes;

public class ContractAccount implements ContractInfo {

	private static final Bytes CONTRACT_INFO_PREFIX = Bytes.fromString("INFO" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes CHAIN_CODE_KEY = Bytes.fromString("CHAIN-CODE");

	private MerkleAccount accBase;

	public ContractAccount(MerkleAccount accBase) {
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
		BytesValue bytesValue = TypedBytesValue.fromBytes(chaincode);
		return accBase.setValue(CHAIN_CODE_KEY, bytesValue, version);
	}

	public byte[] getChainCode() {
		return accBase.getValue(CHAIN_CODE_KEY).getValue().toBytes();
	}

	public byte[] getChainCode(long version) {
		return accBase.getValue(CHAIN_CODE_KEY, version).getValue().toBytes();
	}

	public long getChaincodeVersion() {
		return accBase.getVersion(CHAIN_CODE_KEY);
	}

	public long setProperty(Bytes key, String value, long version) {
		BytesValue bytesValue = TypedBytesValue.fromText(value);
		return accBase.setValue(encodePropertyKey(key), bytesValue, version);
	}

	public String getProperty(Bytes key) {
		BytesValue bytesValue = accBase.getValue(encodePropertyKey(key));
		return TypedBytesValue.toText(bytesValue);
	}

	public String getProperty(Bytes key, long version) {
		BytesValue bytesValue = accBase.getValue(encodePropertyKey(key), version);
		return TypedBytesValue.toText(bytesValue);
	}

	private Bytes encodePropertyKey(Bytes key) {
		return CONTRACT_INFO_PREFIX.concat(key);
	}

}