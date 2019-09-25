package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.TypedBytesValue;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.UserAccountHeader;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.VersioningMap;

/**
 * 用户账户；
 * 
 * @author huanghaiquan
 *
 */
public class UserAccount extends MerkleAccount{ //implements UserInfo {

	private static final Bytes USER_INFO_PREFIX = Bytes.fromString("PROP" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes DATA_PUB_KEY = Bytes.fromString("DATA-PUBKEY");

//	private MerkleAccount baseAccount;

	public UserAccount(VersioningMap baseAccount) {
		this.baseAccount = baseAccount;
	}

	public PubKey getDataPubKey() {
		BytesValue pkBytes = baseAccount.getValue(DATA_PUB_KEY);
		if (pkBytes == null) {
			return null;
		}
		return new PubKey(pkBytes.getValue().toBytes());
	}

	public long setDataPubKey(PubKey pubKey) {
		byte[] pkBytes = pubKey.toBytes();
		return baseAccount.setValue(DATA_PUB_KEY, TypedBytesValue.fromBytes(pkBytes), -1);
	}

	public long setDataPubKey(PubKey pubKey, long version) {
		byte[] pkBytes = pubKey.toBytes();
		return baseAccount.setValue(DATA_PUB_KEY, TypedBytesValue.fromBytes(pkBytes), version);
	}

	public long setProperty(String key, String value, long version) {
		return setProperty(Bytes.fromString(key), value, version);
	}

	public long setProperty(Bytes key, String value, long version) {
		return baseAccount.setValue(encodePropertyKey(key), TypedBytesValue.fromText(value), version);
	}

	public String getProperty(Bytes key) {
		BytesValue value = baseAccount.getValue(encodePropertyKey(key));
		return value == null ? null : value.getValue().toUTF8String();
	}

	public String getProperty(Bytes key, long version) {
		BytesValue value = baseAccount.getValue(encodePropertyKey(key), version);
		return value == null ? null : value.getValue().toUTF8String();
	}

	private Bytes encodePropertyKey(Bytes key) {
		// return key.concatTo(USER_INFO_PREFIX);
		return USER_INFO_PREFIX.concat(key);
	}

}