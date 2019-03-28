package com.jd.blockchain.crypto;

/**
 * 私钥；
 * 
 * @author huanghaiquan
 *
 */
public class PrivKey extends BaseCryptoKey {
	private static final long serialVersionUID = 6265440395252295646L;

	public PrivKey(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(algorithm, rawCryptoBytes, CryptoKeyType.PRIV_KEY);
	}

	public PrivKey(byte[] cryptoBytes) {
		super(cryptoBytes);
	}
	
	@Override
	public CryptoKeyType getKeyType() {
		return CryptoKeyType.PRIV_KEY;
	}
}