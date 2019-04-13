package com.jd.blockchain.crypto.asymmetric;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;

public class CryptoKeyPair {

	private PubKey pubKey;

	private PrivKey privKey;

	public CryptoAlgorithm getAlgorithm() {
		return pubKey.getAlgorithm();
	}

	public PubKey getPubKey() {
		return pubKey;
	}

	public PrivKey getPrivKey() {
		return privKey;
	}

	public CryptoKeyPair(PubKey pubKey, PrivKey privKey) {
		if (!CryptoAlgorithm.equals(pubKey.getAlgorithm(), privKey.getAlgorithm())) {
			throw new IllegalArgumentException("The algorithms of PubKey and PrivKey don't match!");
		}
		this.pubKey = pubKey;
		this.privKey = privKey;
	}

}
