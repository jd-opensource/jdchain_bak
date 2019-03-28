package com.jd.blockchain.crypto;

public enum CryptoKeyType {

	/**
	 * 非对称密码算法的公钥
	 */
	PUB_KEY((byte)0x01),

	/**
	 * 非对称密码算法的私钥；
	 */
	PRIV_KEY((byte)0x02),

	/**
	 * 对称密码算法的密钥；
	 */
	SYMMETRIC_KEY((byte)0x03);

	public final byte CODE;

	CryptoKeyType(byte code) {
		CODE = code;
	}

	public static CryptoKeyType valueOf(byte code) {
		for (CryptoKeyType alg : CryptoKeyType.values()) {
			if (alg.CODE == code) {
				return alg;
			}
		}
		throw new IllegalArgumentException("CryptoKeyType doesn't support enum code[" + code + "]!");
	}

	public byte getCODE() {
		return CODE;
	}
}
