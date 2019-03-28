package com.jd.blockchain.crypto.asymmetric;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.base.BaseCryptoBytes;

public class AsymmetricCiphertext extends BaseCryptoBytes implements Ciphertext {

	public AsymmetricCiphertext(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(algorithm, rawCryptoBytes);
	}

	public AsymmetricCiphertext(byte[] cryptoBytes) {
		super(cryptoBytes);
	}
	
	@Override
	protected boolean support(CryptoAlgorithm algorithm) {
		return algorithm.isAsymmetric() && algorithm.isEncryptable();
	}

	@Override
	public byte[] getRawCiphertext() {
		return getRawCryptoBytes().getBytesCopy();
	}
}
