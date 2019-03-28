package com.jd.blockchain.crypto.symmetric;

import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC_KEY;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoKeyType;
import com.jd.blockchain.crypto.base.BaseCryptoKey;

public class SymmetricKey extends BaseCryptoKey {

	private static final long serialVersionUID = 5055547663903904933L;

	public SymmetricKey(CryptoAlgorithm algorithm, byte[] rawKeyBytes) {
		super(algorithm, rawKeyBytes, SYMMETRIC_KEY);
	}

	public SymmetricKey(byte[] keyBytes) {
		super(keyBytes);
	}
	
	@Override
	protected boolean support(CryptoKeyType keyType) {
		return SYMMETRIC_KEY == keyType;
	}

	@Override
	public CryptoKeyType getKeyType() {
		return SYMMETRIC_KEY;
	}

}
