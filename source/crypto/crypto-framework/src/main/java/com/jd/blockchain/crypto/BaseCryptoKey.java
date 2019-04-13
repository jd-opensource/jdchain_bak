package com.jd.blockchain.crypto;

import java.io.Serializable;

import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesUtils;

public abstract class BaseCryptoKey extends BaseCryptoBytes implements CryptoKey, Serializable {

	public static final int KEY_TYPE_BYTES = 1;
	private static final long serialVersionUID = 4543074827807908363L;

//	public BaseCryptoKey() {
//		super();
//	}
	
	protected BaseCryptoKey(short algorithm, byte[] rawKeyBytes, CryptoKeyType keyType) {
		super(algorithm, encodeKeyBytes(rawKeyBytes, keyType));
	}

	protected BaseCryptoKey(CryptoAlgorithm algorithm, byte[] rawKeyBytes, CryptoKeyType keyType) {
		super(algorithm, encodeKeyBytes(rawKeyBytes, keyType));
	}

	public BaseCryptoKey(byte[] cryptoBytes) {
		super(cryptoBytes);
		CryptoKeyType keyType = decodeKeyType(getRawCryptoBytes());
		if (getKeyType() != keyType) {
			throw new CryptoException("CryptoKey doesn't support keyType[" + keyType + "]!");
		}
	}

	private static byte[] encodeKeyBytes(byte[] rawKeyBytes, CryptoKeyType keyType) {
		return BytesUtils.concat(new byte[] { keyType.CODE }, rawKeyBytes);
	}

	private static CryptoKeyType decodeKeyType(BytesSlice cryptoBytes) {
		return CryptoKeyType.valueOf(cryptoBytes.getByte());
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
