package com.jd.blockchain.crypto;

import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC_KEY;

/**
 * 单密钥；
 * 
 * @author huanghaiquan
 *
 */
public class SymmetricKey extends BaseCryptoKey {

	private static final long serialVersionUID = 5055547663903904933L;

	public SymmetricKey(CryptoAlgorithm algorithm, byte[] rawKeyBytes) {
		super(algorithm, rawKeyBytes, SYMMETRIC_KEY);
	}

	public SymmetricKey(byte[] keyBytes) {
		super(keyBytes);
	}

	@Override
	public CryptoKeyType getKeyType() {
		return SYMMETRIC_KEY;
	}

}
