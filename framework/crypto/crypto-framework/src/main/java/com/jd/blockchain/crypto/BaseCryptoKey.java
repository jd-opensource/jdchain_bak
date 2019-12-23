package com.jd.blockchain.crypto;

import java.io.Serializable;

public abstract class BaseCryptoKey extends BaseCryptoBytes implements CryptoKey, Serializable {

	public static final int KEY_TYPE_BYTES = 1;
	private static final long serialVersionUID = 4543074827807908363L;

	public BaseCryptoKey() {
	    super();
	}

	protected BaseCryptoKey(short algorithm, byte[] rawKeyBytes, CryptoKeyType keyType) {
		super(algorithm, CryptoBytesEncoding.encodeKeyBytes(rawKeyBytes, keyType));
	}

	protected BaseCryptoKey(CryptoAlgorithm algorithm, byte[] rawKeyBytes, CryptoKeyType keyType) {
		super(algorithm, CryptoBytesEncoding.encodeKeyBytes(rawKeyBytes, keyType));
	}

	public BaseCryptoKey(byte[] cryptoBytes) {
		super(cryptoBytes);
		CryptoKeyType keyType = CryptoBytesEncoding.decodeKeyType(getRawCryptoBytes());
		if (getKeyType() != keyType) {
			throw new CryptoException("CryptoKey doesn't support keyType[" + keyType + "]!");
		}
	}

	@Override
	protected boolean support(short algorithm) {
		return CryptoAlgorithm.hasAsymmetricKey(algorithm) || CryptoAlgorithm.hasSymmetricKey(algorithm);
	}

	@Override
	public byte[] getRawKeyBytes() {
		return getRawCryptoBytes().getBytesCopy(1);
	}
}
