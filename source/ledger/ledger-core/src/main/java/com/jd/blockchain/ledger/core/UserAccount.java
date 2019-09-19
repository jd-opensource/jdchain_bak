package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesData;
import com.jd.blockchain.ledger.UserInfo;
import com.jd.blockchain.utils.Bytes;

/**
 * 用户账户；
 * 
 * @author huanghaiquan
 *
 */
public class UserAccount implements UserInfo {

	private static final Bytes USER_INFO_PREFIX = Bytes.fromString("PROP" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes DATA_PUB_KEY = Bytes.fromString("DATA-PUBKEY");

	private MerkleAccount baseAccount;

	@Override
	public Bytes getAddress() {
		return baseAccount.getAddress();
	}

	@Override
	public PubKey getPubKey() {
		return baseAccount.getPubKey();
	}

	@Override
	public HashDigest getRootHash() {
		return baseAccount.getRootHash();
	}

	public UserAccount(MerkleAccount baseAccount) {
		this.baseAccount = baseAccount;
	}

	public PubKey getDataPubKey() {
		BytesValue pkBytes = baseAccount.getBytes(DATA_PUB_KEY);
		if (pkBytes == null) {
			return null;
		}
		return new PubKey(pkBytes.getValue().toBytes());
	}

	public long setDataPubKey(PubKey pubKey) {
		byte[] pkBytes = pubKey.toBytes();
		return baseAccount.setBytes(DATA_PUB_KEY, BytesData.fromBytes(pkBytes), -1);
	}

	public long setDataPubKey(PubKey pubKey, long version) {
		byte[] pkBytes = pubKey.toBytes();
		return baseAccount.setBytes(DATA_PUB_KEY, BytesData.fromBytes(pkBytes), version);
	}

	public long setProperty(String key, String value, long version) {
		return setProperty(Bytes.fromString(key), value, version);
	}

	public long setProperty(Bytes key, String value, long version) {
		return baseAccount.setBytes(encodePropertyKey(key), BytesData.fromText(value), version);
	}

	public String getProperty(Bytes key) {
		BytesValue value = baseAccount.getBytes(encodePropertyKey(key));
		return value == null ? null : value.getValue().toUTF8String();
	}

	public String getProperty(Bytes key, long version) {
		BytesValue value = baseAccount.getBytes(encodePropertyKey(key), version);
		return value == null ? null : value.getValue().toUTF8String();
	}

	private Bytes encodePropertyKey(Bytes key) {
		// return key.concatTo(USER_INFO_PREFIX);
		return USER_INFO_PREFIX.concat(key);
	}

}