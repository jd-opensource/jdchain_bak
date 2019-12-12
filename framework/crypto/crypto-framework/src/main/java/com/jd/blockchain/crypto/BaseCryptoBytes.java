package com.jd.blockchain.crypto;

import java.util.Arrays;

import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesSlice;

public abstract class BaseCryptoBytes extends Bytes implements CryptoBytes {

	private short algorithm;
	
	public BaseCryptoBytes() {
		super();
	}
	
	public BaseCryptoBytes(short algorithm, byte[] rawCryptoBytes) {
		super(CryptoBytesEncoding.encodeBytes(algorithm, rawCryptoBytes));
		this.algorithm = algorithm;
	}

	public BaseCryptoBytes(CryptoAlgorithm algorithm, byte[] rawCryptoBytes) {
		super(CryptoBytesEncoding.encodeBytes(algorithm, rawCryptoBytes));
		this.algorithm = algorithm.code();
	}

	public BaseCryptoBytes(byte[] cryptoBytes) {
		super(cryptoBytes);
		short algorithm = CryptoBytesEncoding.decodeAlgorithm(cryptoBytes);
		if (!support(algorithm)) {
			throw new CryptoException("Not supported algorithm [code:" + algorithm + "]!");
		}
		this.algorithm = algorithm;
	}

	protected abstract boolean support(short algorithm);

	protected byte[] resolveRawCryptoBytes(byte[] cryptoBytes) {
		return Arrays.copyOfRange(cryptoBytes, 1, cryptoBytes.length);
	}

	@Override
	public short getAlgorithm() {
		return algorithm;
	}

	protected BytesSlice getRawCryptoBytes() {
		return new BytesSlice(getDirectBytes(), CryptoAlgorithm.CODE_SIZE);
	}
}
