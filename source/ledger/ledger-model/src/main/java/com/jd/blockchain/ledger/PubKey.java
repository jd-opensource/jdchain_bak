package com.jd.blockchain.ledger;

import com.jd.blockchain.ledger.data.CryptoKeyEncoding;
import com.jd.blockchain.utils.io.ByteArray;

/**
 * 密钥；
 * 
 * @author huanghaiquan
 *
 */
//public class PubKey implements CryptoKey {
//
//	private CryptoKeyType type;
//
//	private ByteArray value;
//
//	public PubKey(CryptoKeyType type, ByteArray value) {
//		this.type = type;
//		this.value = value;
//	}
//
//	public CryptoKeyType getType() {
//		return type;
//	}
//
//	public ByteArray getValue() {
//		return value;
//	}
//
//	public ByteArray toBytes() {
//		return CryptoKeyEncoding.toBytes(this);
//	}
//
//	@Override
//	public String toString() {
//		return toBytes().toString();
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (!(o instanceof PubKey)) return false;
//
//		PubKey pubKey = (PubKey) o;
//
//		if (getType() != pubKey.getType()) return false;
//		return getValue().equals(pubKey.getValue());
//	}
//}