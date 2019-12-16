package com.jd.blockchain.crypto.service.sm;

import java.util.Arrays;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoBytes;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.utils.sm.SM3Utils;

public class SM3HashFunction implements HashFunction {

	private static final CryptoAlgorithm SM3 = SMAlgorithm.SM3;

	private static final int DIGEST_BYTES = 256 / 8;

	private static final int DIGEST_LENGTH = CryptoBytes.ALGORYTHM_CODE_SIZE + DIGEST_BYTES;

	SM3HashFunction() {
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return SM3;
	}

	@Override
	public HashDigest hash(byte[] data) {
		if (data == null) {
			throw new CryptoException("data is null!");
		}

		byte[] digestBytes = SM3Utils.hash(data);
		return new HashDigest(SM3, digestBytes);
	}

	@Override
	public HashDigest hash(byte[] data, int offset, int len) {
		if (data == null) {
			throw new CryptoException("data is null!");
		}

		byte[] digestBytes = SM3Utils.hash(data, offset, len);
		return new HashDigest(SM3, digestBytes);
	}

	@Override
	public boolean verify(HashDigest digest, byte[] data) {
		HashDigest hashDigest = hash(data);
		return Arrays.equals(hashDigest.toBytes(), digest.toBytes());
	}

	@Override
	public boolean supportHashDigest(byte[] digestBytes) {
		// 验证输入字节数组长度=算法标识长度+摘要长度，以及算法标识；
		return CryptoAlgorithm.match(SM3, digestBytes) && DIGEST_LENGTH == digestBytes.length;
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
