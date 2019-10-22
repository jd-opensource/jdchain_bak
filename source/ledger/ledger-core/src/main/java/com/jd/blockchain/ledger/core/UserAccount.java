package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.UserInfo;
import com.jd.blockchain.utils.Bytes;

/**
 * 用户账户；
 * 
 * @author huanghaiquan
 *
 */
public class UserAccount extends AccountDecorator implements UserInfo { // implements UserInfo {

	private static final Bytes USER_INFO_PREFIX = Bytes.fromString("PROP" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes DATA_PUB_KEY = Bytes.fromString("DATA-PUBKEY");

	public UserAccount(MerkleAccount mklAccount) {
		super(mklAccount);
	}

	private PubKey dataPubKey;
	

	@Override
	public Bytes getAddress() {
		return getID().getAddress();
	}

	@Override
	public PubKey getPubKey() {
		return getID().getPubKey();
	}
	
	@Override
	public PubKey getDataPubKey() {
		if (dataPubKey == null) {
			BytesValue pkBytes = getHeaders().getValue(DATA_PUB_KEY);
			if (pkBytes == null) {
				return null;
			}
			dataPubKey = new PubKey(pkBytes.getValue().toBytes());
		}
		return dataPubKey;
	}

	public void setDataPubKey(PubKey pubKey) {
		long version = getHeaders().getVersion(DATA_PUB_KEY);
		setDataPubKey(pubKey, version);
	}

	public void setDataPubKey(PubKey pubKey, long version) {
		TypedValue value = TypedValue.fromPubKey(dataPubKey);
		long newVersion = getHeaders().setValue(DATA_PUB_KEY, value, version);
		if (newVersion > -1) {
			dataPubKey = pubKey;
		} else {
			throw new LedgerException("Data public key was updated failed!");
		}
	}

	public long setProperty(String key, String value, long version) {
		return setProperty(Bytes.fromString(key), value, version);
	}

	public long setProperty(Bytes key, String value, long version) {
		return getHeaders().setValue(encodePropertyKey(key), TypedValue.fromText(value), version);
	}

	public String getProperty(Bytes key) {
		BytesValue value = getHeaders().getValue(encodePropertyKey(key));
		return value == null ? null : value.getValue().toUTF8String();
	}

	public String getProperty(Bytes key, long version) {
		BytesValue value = getHeaders().getValue(encodePropertyKey(key), version);
		return value == null ? null : value.getValue().toUTF8String();
	}

	private Bytes encodePropertyKey(Bytes key) {
		return USER_INFO_PREFIX.concat(key);
	}


}