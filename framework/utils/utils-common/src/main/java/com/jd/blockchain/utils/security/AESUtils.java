package com.jd.blockchain.utils.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.jd.blockchain.utils.codec.HexUtils;

/**
 * AES 加密算法工具类；
 *
 * @author haiq
 *
 */
public class AESUtils {

	/**
	 * 用指定的种子生成 128 位的密钥； <br>
	 *
	 * 如果指定的种子为空（null 或长度为 0 ），则生成随机的密钥；
	 *
	 * @param seed
	 *            种子；
	 * @return
	 */
	public static String generateKey128_Hex(byte[] seed) {
		byte[] keyBytes = generateKey128_Bytes(seed);
		return HexUtils.encode(keyBytes);
	}

	/**
	 * 用指定的种子生成 128 位的密钥；
	 *
	 * @param seed
	 *            种子；
	 * @return
	 */
	public static byte[] generateKey128_Bytes(byte[] seed) {
		SecretKey key = generateKey128(seed);
		return key.getEncoded();
	}

	/**
	 * 用指定的种子生成 128 位的密钥；
	 *
	 * @param seed
	 *            种子； 不允许为空；
	 * @return
	 */
	public static SecretKey generateKey128(byte[] seed) {
		if (seed == null || seed.length == 0) {
			throw new IllegalArgumentException("Empty seed!");
		}
		// 注：AES 算法只支持 128 位，不支持 192, 256 位的密钥加密；
		byte[] hashBytes = ShaUtils.hash_128(seed);
		return new SecretKeySpec(hashBytes, "AES");

		//注：由于同一个种子有可能产生不同的随机数序列，不能基于随机数机制来生成；by huanghaiquan at 2017-08-25；
//		byte[] random = RandomUtils.generateRandomBytes(16, seed);
//		return new SecretKeySpec(random, "AES");
	}

	/**
	 * 生成 128 位的随机密钥；
	 *
	 * @return
	 */
	public static SecretKey generateKey128() {
		byte[] randBytes = RandomUtils.generateRandomBytes(16);
		return new SecretKeySpec(randBytes, "AES");
	}

	/**
	 * 生成以 16 进制编码的 128 位的随机密钥；
	 *
	 * @return
	 */
	public static String generateKey128_Hex() {
		byte[] keyBytes = generateKey128_Bytes();
		return HexUtils.encode(keyBytes);
	}

	public static byte[] generateKey128_Bytes() {
		SecretKey key = generateKey128();
		return key.getEncoded();
	}

	/**
	 * 用指定的 16 进制的AES密钥进行加密；
	 *
	 * @param content
	 * @param key
	 *            16进制编码的 AES 密钥；
	 * @return
	 */
	public static byte[] encrypt(byte[] content, String key) {
		return encrypt(content, HexUtils.decode(key));
	}

	public static byte[] encrypt(byte[] content, byte[] secretKey) {
		SecretKey aesKey = new SecretKeySpec(secretKey, "AES");
		return encrypt(content, aesKey);
	}

	/**
	 *
	 * @param plainBytes
	 * @param key
	 * @return
	 */
	public static byte[] encrypt(byte[] plainBytes, SecretKey key) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(plainBytes);
		} catch (InvalidKeyException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptionException(e.getMessage(), e);
		} catch (BadPaddingException e) {
			throw new EncryptionException(e.getMessage(), e);
		}
	}

	/**
	 * 解密；
	 *
	 * @param encryptedBytes
	 * @param key
	 * @return
	 */
	public static byte[] decrypt(byte[] encryptedBytes, String key) {
		return decrypt(encryptedBytes, HexUtils.decode(key));
	}

	public static byte[] decrypt(byte[] encryptedBytes, byte[] key) {
		SecretKey aesKey = new SecretKeySpec(key, "AES");
		return decrypt(encryptedBytes, aesKey);
	}

	public static byte[] decrypt(byte[] encryptedBytes, SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(encryptedBytes);
		} catch (InvalidKeyException e) {
			throw new DecryptionException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new DecryptionException(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			throw new DecryptionException(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			throw new DecryptionException(e.getMessage(), e);
		} catch (BadPaddingException e) {
			throw new DecryptionException(e.getMessage(), e);
		}
	}

}