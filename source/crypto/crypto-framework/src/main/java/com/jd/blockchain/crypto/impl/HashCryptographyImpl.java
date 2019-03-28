package com.jd.blockchain.crypto.impl;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.hash.HashCryptography;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.crypto.hash.HashFunction;
import com.jd.blockchain.crypto.impl.def.hash.RIPEMD160HashFunction;
import com.jd.blockchain.crypto.impl.def.hash.SHA256HashFunction;
import com.jd.blockchain.crypto.impl.sm.hash.SM3HashFunction;

public class HashCryptographyImpl implements HashCryptography {

	private static final HashFunction SHA256_FUNC = new SHA256HashFunction();
	private static final HashFunction RIPEMD160_FUNC = new RIPEMD160HashFunction();
	private static final HashFunction SM3_FUNC = new SM3HashFunction();

//	private static final HashFunction JNISHA256_FUNC = new JNISHA256HashFunction();
//	private static final HashFunction JNIRIPEMD160_FUNC = new JNIRIPEMD160HashFunction();

	@Override
	public HashFunction getFunction(CryptoAlgorithm algorithm) {

		// 遍历哈希算法，如果满足，则返回实例
		switch (algorithm) {
		case SHA256:
			return SHA256_FUNC;
		case RIPEMD160:
			return RIPEMD160_FUNC;
		case SM3:
			return SM3_FUNC;
//		case JNISHA256:
//				return JNISHA256_FUNC;
//		case JNIRIPEMD160:
//				return JNIRIPEMD160_FUNC;
		default:
			break;
		}
		throw new IllegalArgumentException("The specified algorithm is not hash algorithm!");
	}

	@Override
	public boolean verify(byte[] digestBytes, byte[] data) {
		HashDigest hashDigest = resolveHashDigest(digestBytes);
		return verify(hashDigest,data);
	}

	@Override
	public boolean verify(HashDigest digest, byte[] data) {
		CryptoAlgorithm algorithm = digest.getAlgorithm();
		return getFunction(algorithm).verify(digest, data);
	}

	@Override
	public HashDigest resolveHashDigest(byte[] digestBytes) {
		HashDigest hashDigest = tryResolveHashDigest(digestBytes);
		if (hashDigest == null)
			throw new IllegalArgumentException("This digestBytes cannot be resolved!");
		else return hashDigest;
	}

	@Override
	public HashDigest tryResolveHashDigest(byte[] digestBytes) {
		//遍历哈希函数，如果满足，则返回解析结果
		if (SHA256_FUNC.supportHashDigest(digestBytes)) {
			return SHA256_FUNC.resolveHashDigest(digestBytes);
		}
		if (RIPEMD160_FUNC.supportHashDigest(digestBytes)) {
			return RIPEMD160_FUNC.resolveHashDigest(digestBytes);
		}
		if (SM3_FUNC.supportHashDigest(digestBytes)) {
			return SM3_FUNC.resolveHashDigest(digestBytes);
		}
//		if (JNISHA256_FUNC.supportHashDigest(digestBytes)) {
//			return JNISHA256_FUNC.resolveHashDigest(digestBytes);
//		}
//		if (JNIRIPEMD160_FUNC.supportHashDigest(digestBytes)) {
//			return JNIRIPEMD160_FUNC.resolveHashDigest(digestBytes);
//		}
		//否则返回null
		return null;
	}
}