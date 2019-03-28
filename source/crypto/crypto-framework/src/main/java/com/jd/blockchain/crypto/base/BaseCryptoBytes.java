package com.jd.blockchain.crypto.base;

import java.util.Arrays;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoBytes;
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
			throw new IllegalArgumentException("Not supported algorithm[" + algorithm.toString() + "]!");
		}
		this.algorithm = algorithm;
	}

	static byte[] encodeBytes(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		return BytesUtils.concat(new byte[] { algorithm.CODE }, rawCryptoBytes);
	}

	static CryptoAlgorithm decodeAlgorithm(byte[] cryptoBytes) {
		return CryptoAlgorithm.valueOf(cryptoBytes[0]);
	}

	protected abstract boolean support(CryptoAlgorithm algorithm);

	protected byte[] resolveRawCryptoBytes(byte[] cryptoBytes) {
		return Arrays.copyOfRange(cryptoBytes, 1, cryptoBytes.length);
	}

	public CryptoAlgorithm getAlgorithm() {
		// return resolveAlgorithm(encodedBytes);
		return algorithm;
	}

	protected BytesSlice getRawCryptoBytes() {
		// return resolveRawCryptoBytes(encodedBytes);
		return new BytesSlice(getDirectBytes(), 1);
	}
}
