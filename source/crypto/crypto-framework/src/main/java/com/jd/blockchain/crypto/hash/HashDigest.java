package com.jd.blockchain.crypto.hash;

import java.io.Serializable;

import com.jd.blockchain.crypto.BaseCryptoBytes;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoDigest;

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
	protected boolean support(CryptoAlgorithm algorithm) {
		return CryptoAlgorithm.isHashAlgorithm(algorithm);
	}

	@Override
	public byte[] getRawDigest() {
		return getRawCryptoBytes().getBytesCopy();
	}

}
