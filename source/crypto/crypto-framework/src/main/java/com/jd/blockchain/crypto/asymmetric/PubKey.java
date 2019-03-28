package com.jd.blockchain.crypto.asymmetric;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoKeyType;
import com.jd.blockchain.crypto.base.BaseCryptoKey;

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
	public PubKey() {
		super();
	}

	public PubKey(byte[] cryptoBytes) {
		super(cryptoBytes);
	}

	@Override
	protected boolean support(CryptoKeyType keyType) {
		return CryptoKeyType.PUB_KEY == keyType;
	}
}