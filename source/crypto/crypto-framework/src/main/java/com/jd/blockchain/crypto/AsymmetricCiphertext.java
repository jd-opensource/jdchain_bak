package com.jd.blockchain.crypto;

public class AsymmetricCiphertext extends BaseCryptoBytes implements Ciphertext {

	public AsymmetricCiphertext(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(algorithm, rawCryptoBytes);
	}

	public AsymmetricCiphertext(byte[] cryptoBytes) {
		super(cryptoBytes);
	}

	@Override
	protected boolean support(short algorithm) {
		return CryptoAlgorithm.isAsymmetricEncryptionAlgorithm(algorithm);
	}

	@Override
	public byte[] getRawCiphertext() {
		return getRawCryptoBytes().getBytesCopy();
	}
}
