package com.jd.blockchain.crypto;

public enum CryptoKeyType {

	/**
	 * 非对称密钥的公钥
	 */
	PUBLIC((byte)0x01),

	/**
	 * 非对称密钥的私钥；
	 */
	PRIVATE((byte)0x02),

	/**
	 * 对称密钥；
	 */
	SYMMETRIC((byte)0x03);

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
		throw new CryptoException("CryptoKeyType doesn't support enum code[" + code + "]!");
	}

	public byte getCODE() {
		return CODE;
	}
}
