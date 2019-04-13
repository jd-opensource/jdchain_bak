package com.jd.blockchain.crypto;

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
	protected boolean support(short algorithm) {
		return CryptoAlgorithm.isSignatureAlgorithm(algorithm);
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
