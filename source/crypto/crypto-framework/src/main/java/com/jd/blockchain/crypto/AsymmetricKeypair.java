package com.jd.blockchain.crypto;

/**
 * 非对称密钥对；
 * 
 * @author huanghaiquan
 *
 */
public class AsymmetricKeypair {

	private PubKey pubKey;

	private PrivKey privKey;

	public short getAlgorithm() {
		return pubKey.getAlgorithm();
	}

	public PubKey getPubKey() {
		return pubKey;
	}

	public PrivKey getPrivKey() {
		return privKey;
	}

	public AsymmetricKeypair(PubKey pubKey, PrivKey privKey) {
		if (pubKey.getAlgorithm() != privKey.getAlgorithm()) {
			throw new IllegalArgumentException("The algorithms of PubKey and PrivKey don't match!");
		}
		this.pubKey = pubKey;
		this.privKey = privKey;
	}

}
