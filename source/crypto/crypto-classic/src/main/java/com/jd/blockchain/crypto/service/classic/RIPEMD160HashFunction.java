package com.jd.blockchain.crypto.service.classic;

import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_CODE_SIZE;

import java.util.Arrays;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.utils.classic.RIPEMD160Utils;

public class RIPEMD160HashFunction implements HashFunction {

	private static final CryptoAlgorithm RIPEMD160 = ClassicAlgorithm.RIPEMD160;

	private static final int DIGEST_BYTES = 160 / 8;

	private static final int DIGEST_LENGTH = ALGORYTHM_CODE_SIZE + DIGEST_BYTES;

	RIPEMD160HashFunction() {
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return RIPEMD160;
	}

	@Override
	public HashDigest hash(byte[] data) {
		if (data == null) {
			throw new CryptoException("data is null!");
		}

		byte[] digestBytes = RIPEMD160Utils.hash(data);
		return new HashDigest(RIPEMD160, digestBytes);
	}

	@Override
	public HashDigest hash(byte[] data, int offset, int len) {
		if (data == null) {
			throw new CryptoException("data is null!");
		}

		byte[] digestBytes = RIPEMD160Utils.hash(data, offset, len);
		return new HashDigest(RIPEMD160, digestBytes);
	}

	@Override
	public boolean verify(HashDigest digest, byte[] data) {
		HashDigest hashDigest = hash(data);
		return Arrays.equals(hashDigest.toBytes(), digest.toBytes());
	}

	@Override
	public boolean supportHashDigest(byte[] digestBytes) {
		// 验证输入字节数组长度=算法标识长度+摘要长度，以及算法标识；
		return DIGEST_LENGTH == digestBytes.length && CryptoAlgorithm.match(RIPEMD160, digestBytes);
	}

	@Override
	public HashDigest resolveHashDigest(byte[] digestBytes) {
		if (supportHashDigest(digestBytes)) {
			return new HashDigest(digestBytes);
		} else {
			throw new CryptoException("digestBytes is invalid!");
		}
	}

}
