package com.jd.blockchain.crypto.base;

import java.io.Serializable;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.CryptoKey;
import com.jd.blockchain.crypto.CryptoKeyType;
import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesUtils;

public abstract class BaseCryptoKey extends BaseCryptoBytes implements CryptoKey,Serializable {

	public static final int KEY_TYPE_BYTES = 1;
	private static final long serialVersionUID = 4543074827807908363L;
	
	private CryptoKeyType keyType;

	public BaseCryptoKey() {
		super();
	}

	public BaseCryptoKey(CryptoAlgorithm algorithm, byte[] rawCryptoBytes, CryptoKeyType keyType) {
		super(algorithm, encodeKeyBytes(rawCryptoBytes, keyType));
		this.keyType = keyType;
	}

	public BaseCryptoKey(byte[] cryptoBytes) {
		super(cryptoBytes);
		CryptoKeyType keyType = decodeKeyType(getRawCryptoBytes());
		if (!support(keyType)) {
			throw new CryptoException("CryptoKey doesn't support keyType[" + keyType + "]!");
		}
		this.keyType = keyType;
	}
	
	@Override
	public CryptoKeyType getKeyType() {
		return keyType;
	}

	private static byte[] encodeKeyBytes(byte[] rawCryptoBytes, CryptoKeyType keyType ) {
		return BytesUtils.concat(new byte[] {keyType.CODE }, rawCryptoBytes);
	}
	
	private static CryptoKeyType decodeKeyType(BytesSlice cryptoBytes) {
		return CryptoKeyType.valueOf(cryptoBytes.getByte());
	}

	protected abstract boolean support(CryptoKeyType keyType);
	
	@Override
	protected boolean support(CryptoAlgorithm algorithm) {
		return algorithm.isSymmetric() || algorithm.isAsymmetric();
	}
	
	
	@Override
	public byte[] getRawKeyBytes() {
		return getRawCryptoBytes().getBytesCopy(1);
	}
}
