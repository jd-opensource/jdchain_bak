package com.jd.blockchain.ledger;

import com.jd.blockchain.ledger.data.CryptoKeyEncoding;
import com.jd.blockchain.utils.io.ByteArray;

import java.io.Serializable;

/**
 * 密钥；
 * 
 * @author huanghaiquan
 *
 */
//public class PrivKey implements CryptoKey,Serializable {
//
//	private CryptoKeyType type;
//
//	private ByteArray value;
//
//	public PrivKey(CryptoKeyType type, ByteArray value) {
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
//
//
//}