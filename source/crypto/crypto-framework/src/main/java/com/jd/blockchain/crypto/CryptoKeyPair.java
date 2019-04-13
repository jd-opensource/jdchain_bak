package com.jd.blockchain.crypto;

public class CryptoKeyPair {

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

	public CryptoKeyPair(PubKey pubKey, PrivKey privKey) {
		if (pubKey.getAlgorithm() != privKey.getAlgorithm()) {
			throw new IllegalArgumentException("The algorithms of PubKey and PrivKey don't match!");
		}
		this.pubKey = pubKey;
		this.privKey = privKey;
	}

}
