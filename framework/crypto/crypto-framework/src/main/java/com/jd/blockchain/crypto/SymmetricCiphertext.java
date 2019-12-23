package com.jd.blockchain.crypto;

public class SymmetricCiphertext extends BaseCryptoBytes implements Ciphertext {

	public SymmetricCiphertext(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(algorithm, rawCryptoBytes);
	}

	public SymmetricCiphertext(byte[] cryptoBytes) {
		super(cryptoBytes);
	}

	@Override
	protected boolean support(short algorithm) {
		return CryptoAlgorithm.isEncryptionAlgorithm(algorithm) && CryptoAlgorithm.hasSymmetricKey(algorithm);
	}
	
	@Override
	public byte[] getRawCiphertext() {
		return getRawCryptoBytes().getBytesCopy();
	}

}
