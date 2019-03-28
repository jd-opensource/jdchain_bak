package com.jd.blockchain.crypto.asymmetric;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoKeyType;
import com.jd.blockchain.crypto.base.BaseCryptoKey;

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
	protected boolean support(CryptoKeyType keyType) {
		return CryptoKeyType.PRIV_KEY == keyType;
	}
}