package com.jd.blockchain.crypto;

import java.io.Serializable;

public class HashDigest extends BaseCryptoBytes implements CryptoDigest,Serializable {

	private static final long serialVersionUID = 693895170514236428L;

	public HashDigest(CryptoAlgorithm algorithm, byte[] rawDigestBytes) {
		super(algorithm, rawDigestBytes);
	}

	public HashDigest() {
		super();
	}

	public HashDigest(byte[] cryptoBytes) {
		super(cryptoBytes);
	}
	
	@Override
	protected boolean support(short algorithm) {
		return CryptoAlgorithm.isHashAlgorithm(algorithm);
	}

	@Override
	public byte[] getRawDigest() {
		return getRawCryptoBytes().getBytesCopy();
	}

}
