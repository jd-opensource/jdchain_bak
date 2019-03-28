package com.jd.blockchain.crypto.asymmetric;

import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;

public class CryptoKeyPair {
	
	private PubKey pubKey;
	
	private PrivKey privKey;

	public PubKey getPubKey() {
		return pubKey;
	}

	public PrivKey getPrivKey() {
		return privKey;
	}

	public CryptoKeyPair(PubKey pubKey, PrivKey privKey) {
		this.pubKey = pubKey;
		this.privKey = privKey;
	}
	
}
