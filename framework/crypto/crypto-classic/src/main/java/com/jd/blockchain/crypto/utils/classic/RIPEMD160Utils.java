package com.jd.blockchain.crypto.utils.classic;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;

/**
 * @author zhanglin33
 * @title: RIPEMD160Utils
 * @description: RIPEMD160 hash algorithm
 * @date 2019-04-10, 16:51
 */
public class RIPEMD160Utils {

	// The length of RIPEMD160 output is 20 bytes
	public static final int RIPEMD160DIGEST_LENGTH = 160 / 8;

	public static byte[] hash(byte[] data) {

		byte[] result = new byte[RIPEMD160DIGEST_LENGTH];
		RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();

		ripemd160Digest.update(data, 0, data.length);
		ripemd160Digest.doFinal(result, 0);
		return result;
	}

	public static byte[] hash(byte[] data, int offset, int len) {

		byte[] result = new byte[RIPEMD160DIGEST_LENGTH];
		RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();

		ripemd160Digest.update(data, offset, len);
		ripemd160Digest.doFinal(result, 0);
		return result;
	}
}
