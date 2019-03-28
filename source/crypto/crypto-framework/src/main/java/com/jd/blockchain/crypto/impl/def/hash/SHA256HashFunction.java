package com.jd.blockchain.crypto.impl.def.hash;

import static com.jd.blockchain.crypto.CryptoAlgorithm.SHA256;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_BYTES;

import java.util.Arrays;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.utils.security.ShaUtils;

public class SHA256HashFunction implements HashFunction {

	private static final int DIGEST_BYTES = 256 / 8;

	private static final int DIGEST_LENGTH = ALGORYTHM_BYTES + DIGEST_BYTES;

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return SHA256;
	}

	@Override
	public HashDigest hash(byte[] data) {
		byte[] digestBytes = ShaUtils.hash_256(data);
		return new HashDigest(SHA256, digestBytes);
	}

	@Override
	public boolean verify(HashDigest digest, byte[] data) {
		HashDigest hashDigest = hash(data);
		return Arrays.equals(hashDigest.toBytes(), digest.toBytes());
	}

	@Override
	public boolean supportHashDigest(byte[] digestBytes) {
		// 验证输入字节数组长度=算法标识长度+摘要长度，以及算法标识；
		return SHA256.CODE == digestBytes[0] && DIGEST_LENGTH == digestBytes.length;
	}

	@Override
	public HashDigest resolveHashDigest(byte[] hashDigestBytes) {
		// 由框架调用 support 方法检查有效性，在此不做重复检查；
		return new HashDigest(hashDigestBytes);
	}

}

