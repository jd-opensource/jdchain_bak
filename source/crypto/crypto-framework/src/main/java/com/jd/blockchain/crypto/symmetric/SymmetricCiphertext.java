package com.jd.blockchain.crypto.symmetric;

import com.jd.blockchain.crypto.BaseCryptoBytes;
import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;

public class SymmetricCiphertext extends BaseCryptoBytes implements Ciphertext {

	public SymmetricCiphertext(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(algorithm, rawCryptoBytes);
	}

	public SymmetricCiphertext(byte[] cryptoBytes) {
		super(cryptoBytes);
	}

//	@Override
//	protected void support(CryptoAlgorithm algorithm) {
//		if (!algorithm.isSymmetric()) {
//			throw new CryptoException("SymmetricCiphertext doesn't support algorithm[" + algorithm + "]!");
//		}
//	}

	@Override
	protected boolean support(CryptoAlgorithm algorithm) {
		return CryptoAlgorithm.isEncryptionAlgorithm(algorithm) && CryptoAlgorithm.hasSymmetricKey(algorithm);
	}
	
	@Override
	public byte[] getRawCiphertext() {
		return getRawCryptoBytes().getBytesCopy();
	}

}
