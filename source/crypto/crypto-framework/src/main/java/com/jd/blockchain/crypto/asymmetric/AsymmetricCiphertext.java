package com.jd.blockchain.crypto.asymmetric;

import com.jd.blockchain.crypto.BaseCryptoBytes;
import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;

public class AsymmetricCiphertext extends BaseCryptoBytes implements Ciphertext {

	public AsymmetricCiphertext(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(algorithm, rawCryptoBytes);
	}

	public AsymmetricCiphertext(byte[] cryptoBytes) {
		super(cryptoBytes);
	}

	@Override
	protected boolean support(CryptoAlgorithm algorithm) {
		return CryptoAlgorithm.isAsymmetricEncryptionAlgorithm(algorithm);
	}

	@Override
	public byte[] getRawCiphertext() {
		return getRawCryptoBytes().getBytesCopy();
	}
}
