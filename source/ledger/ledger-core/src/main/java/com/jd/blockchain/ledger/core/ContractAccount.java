package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.ContractInfo;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.utils.Bytes;

public class ContractAccount extends AccountDecorator implements ContractInfo {

	private static final Bytes CONTRACT_INFO_PREFIX = Bytes.fromString("INFO" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes CHAIN_CODE_KEY = Bytes.fromString("CHAIN-CODE");

	public ContractAccount(MerkleAccount mklAccount) {
		super(mklAccount);
	}

	@Override
	public Bytes getAddress() {
		return getID().getAddress();
	}

	@Override
	public PubKey getPubKey() {
		return getID().getPubKey();
	}

//	public MerkleProof getChaincodeProof() {
//		return getHeaders().getProof(CHAIN_CODE_KEY);
//	}
//
//	public MerkleProof getPropertyProof(Bytes key) {
//		return getHeaders().getProof(encodePropertyKey(key));
//	}

	public long setChaincode(byte[] chaincode, long version) {
		BytesValue bytesValue = TypedValue.fromBytes(chaincode);
		return getHeaders().setValue(CHAIN_CODE_KEY, bytesValue, version);
	}

	public byte[] getChainCode() {
		return getHeaders().getValue(CHAIN_CODE_KEY).getValue().toBytes();
	}

	public byte[] getChainCode(long version) {
		return getHeaders().getValue(CHAIN_CODE_KEY, version).getValue().toBytes();
	}

	public long getChaincodeVersion() {
		return getHeaders().getVersion(CHAIN_CODE_KEY);
	}

	public long setProperty(Bytes key, String value, long version) {
		BytesValue bytesValue = TypedValue.fromText(value);
		return getHeaders().setValue(encodePropertyKey(key), bytesValue, version);
	}

	public String getProperty(Bytes key) {
		BytesValue bytesValue = getHeaders().getValue(encodePropertyKey(key));
		return TypedValue.wrap(bytesValue).stringValue();
	}

	public String getProperty(Bytes key, long version) {
		BytesValue bytesValue = getHeaders().getValue(encodePropertyKey(key), version);
		return TypedValue.wrap(bytesValue).stringValue();
	}

	private Bytes encodePropertyKey(Bytes key) {
		return CONTRACT_INFO_PREFIX.concat(key);
	}

}