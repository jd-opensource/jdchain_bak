package com.jd.blockchain.crypto.impl.sm.hash;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoBytes;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.crypto.smutils.hash.SM3Utils;

import java.util.Arrays;

import static com.jd.blockchain.crypto.CryptoAlgorithm.SM3;

public class SM3HashFunction implements HashFunction {

	private static final int DIGEST_BYTES = 256/8;

	private static final int DIGEST_LENGTH = CryptoBytes.ALGORYTHM_BYTES + DIGEST_BYTES;

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return SM3;
	}

	@Override
	public HashDigest hash(byte[] data) {
		byte[] digestBytes = SM3Utils.hash(data);
		return new HashDigest(SM3,digestBytes);
	}

	@Override
	public boolean verify(HashDigest digest, byte[] data) {
		HashDigest hashDigest = hash(data);
		return Arrays.equals(hashDigest.toBytes(), digest.toBytes());
	}

	@Override
	public boolean supportHashDigest(byte[] digestBytes) {
		// 验证输入字节数组长度=算法标识长度+摘要长度，以及算法标识；
		return SM3.CODE == digestBytes[0] && DIGEST_LENGTH == digestBytes.length;
	}

	@Override
	public HashDigest resolveHashDigest(byte[] hashDigestBytes) {
		// 由框架调用 support 方法检查有效性，在此不做重复检查；
		return new HashDigest(hashDigestBytes);
	}
}
