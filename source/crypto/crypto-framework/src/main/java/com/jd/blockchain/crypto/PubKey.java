package com.jd.blockchain.crypto;

/**
 * 公钥；
 * 
 * @author huanghaiquan
 *
 */
public class PubKey extends BaseCryptoKey {

	private static final long serialVersionUID = -2055071197736385328L;

	public PubKey(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(algorithm, rawCryptoBytes, CryptoKeyType.PUB_KEY);
	}

	public PubKey(byte[] cryptoBytes) {
		super(cryptoBytes);
	}
	
	@Override
	public CryptoKeyType getKeyType() {
		return CryptoKeyType.PUB_KEY;
	}
}