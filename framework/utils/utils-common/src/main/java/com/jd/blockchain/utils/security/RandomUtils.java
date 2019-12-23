package com.jd.blockchain.utils.security;

import java.security.SecureRandom;

/**
 * 随机数生成工具类；
 * 
 * @author haiq
 *
 */
public class RandomUtils {

	public static byte[] generateRandomBytes(int sizeOfRandom) {
		return generateRandomBytes(sizeOfRandom, null);
	}

	/**
	 * 用指定的种子生成秘钥；
	 * <p>
	 * 
	 * 注：需要注意，同一个种子有可能产生不同的随机数序列；
	 * 
	 * @param sizeOfRandom
	 *            要输出的随机数的长度；（单位：字节）
	 * @param seed
	 *            随机种子；
	 * @return 随机数；长度等于 sizeOfRandom 参数指定的值；
	 */
	public static byte[] generateRandomBytes(int sizeOfRandom, byte[] seed) {
		SecureRandom sr = null;
		if (seed == null || seed.length == 0) {
			// 随机；
			sr = new SecureRandom();
		} else {
			sr = new SecureRandom(seed);
		}
		byte[] randomBytes = new byte[sizeOfRandom];
		sr.nextBytes(randomBytes);
		return randomBytes;
	}

}
