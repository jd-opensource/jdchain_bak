package com.jd.blockchain.crypto;

import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC;

/**
 * 对称密钥；
 * 
 * @author huanghaiquan
 *
 */
public class SymmetricKey extends BaseCryptoKey {

	private static final long serialVersionUID = 5055547663903904933L;

	public SymmetricKey(CryptoAlgorithm algorithm, byte[] rawKeyBytes) {
		super(algorithm, rawKeyBytes, SYMMETRIC);
	}

	public SymmetricKey(byte[] keyBytes) {
		super(keyBytes);
	}

	@Override
	public CryptoKeyType getKeyType() {
		return SYMMETRIC;
	}

}
