package com.jd.blockchain.crypto;

import java.util.Arrays;

import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesSlice;
import com.jd.blockchain.utils.io.BytesUtils;

public abstract class BaseCryptoBytes extends Bytes implements CryptoBytes {

	private CryptoAlgorithm algorithm;

	public BaseCryptoBytes() {
		super();
	}

	public BaseCryptoBytes(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(encodeBytes(algorithm, rawCryptoBytes));
		this.algorithm = algorithm;
	}

	public BaseCryptoBytes(byte[] cryptoBytes) {
		super(cryptoBytes);
		CryptoAlgorithm algorithm = decodeAlgorithm(cryptoBytes);
		if (!support(algorithm)) {
			throw new CryptoException("Not supported algorithm[" + algorithm.toString() + "]!");
		}
		this.algorithm = algorithm;
	}

	static byte[] encodeBytes(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		return BytesUtils.concat(CryptoAlgorithm.toBytes(algorithm), rawCryptoBytes);
	}

	static CryptoAlgorithm decodeAlgorithm(byte[] cryptoBytes) {
		short algorithmCode = BytesUtils.toShort(cryptoBytes, 0);
		CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm(algorithmCode);
		if (algorithm == null) {
			throw new CryptoException("The algorithm with code[" + algorithmCode + "] is not supported!");
		}
		return algorithm;
	}

	protected abstract boolean support(CryptoAlgorithm algorithm);

	protected byte[] resolveRawCryptoBytes(byte[] cryptoBytes) {
		return Arrays.copyOfRange(cryptoBytes, 1, cryptoBytes.length);
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return algorithm;
	}

	protected BytesSlice getRawCryptoBytes() {
		return new BytesSlice(getDirectBytes(), 2);
	}
}
