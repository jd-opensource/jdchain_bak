package com.jd.blockchain.crypto.asymmetric;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoDigest;
import com.jd.blockchain.crypto.base.BaseCryptoBytes;

public class SignatureDigest extends BaseCryptoBytes implements CryptoDigest {
	public SignatureDigest() {
		super();
	}

	public SignatureDigest(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(algorithm, rawCryptoBytes);
	}

	public SignatureDigest(byte[] cryptoBytes) {
		super(cryptoBytes);
	}
	
	@Override
	protected boolean support(CryptoAlgorithm algorithm) {
		return algorithm.isAsymmetric() && algorithm.isSignable();
	}

	/**
	 * 返回原始签名摘要；
	 *
	 * @return
	 */
	@Override
	public byte[] getRawDigest() {
		return getRawCryptoBytes().getBytesCopy();
	}

}
