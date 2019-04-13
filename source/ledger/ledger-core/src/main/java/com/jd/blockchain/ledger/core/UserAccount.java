package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.UserInfo;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * 用户账户；
 * 
 * @author huanghaiquan
 *
 */
public class UserAccount implements UserInfo {

	private static final Bytes USER_INFO_PREFIX = Bytes.fromString("PROP" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes DATA_PUB_KEY = Bytes.fromString("DATA-PUBKEY");

	private BaseAccount baseAccount;

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

	public UserAccount(BaseAccount baseAccount) {
		this.baseAccount = baseAccount;
	}

	public PubKey getDataPubKey() {
		byte[] pkBytes = baseAccount.getBytes(DATA_PUB_KEY);
		if (pkBytes == null) {
			return null;
		}
		return CryptoUtils.crypto().asymmetricCryptography().resolvePubKey(pkBytes);
	}

	public long setDataPubKey(PubKey pubKey) {
		byte[] pkBytes = pubKey.toBytes();
		return baseAccount.setBytes(DATA_PUB_KEY, pkBytes, -1);
	}

	public long setDataPubKey(PubKey pubKey, long version) {
		byte[] pkBytes = pubKey.toBytes();
		return baseAccount.setBytes(DATA_PUB_KEY, pkBytes, version);
	}

	public long setProperty(String key, String value, long version) {
		return setProperty(Bytes.fromString(key), value, version);
	}

	public long setProperty(Bytes key, String value, long version) {
		return baseAccount.setBytes(encodePropertyKey(key), BytesUtils.toBytes(value), version);
	}

	public String getProperty(Bytes key) {
		return BytesUtils.toString(baseAccount.getBytes(encodePropertyKey(key)));
	}

	public String getProperty(Bytes key, long version) {
		return BytesUtils.toString(baseAccount.getBytes(encodePropertyKey(key), version));
	}

	private Bytes encodePropertyKey(Bytes key) {
//		return key.concatTo(USER_INFO_PREFIX);
		 return USER_INFO_PREFIX.concat(key);
	}

}