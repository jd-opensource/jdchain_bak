package com.jd.blockchain.utils.security;

import com.jd.blockchain.utils.io.BytesUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES 算法的工具类；
 * 
 * @author haiq
 *
 */
public class DESUtils {

	private static final String DES = "DES";
	private static final String DEFAULT_CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";

	/**
	 * 加密
	 * 
	 * @param code
	 * @param keyBytes
	 *            {key+adress}.length=8*n
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(String code, byte[] keyBytes) throws Exception {
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
		DESKeySpec keySpec = new DESKeySpec(keyBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(BytesUtils.toBytes(code));
	}

	/** DES解密 */
	public static byte[] decrypt(byte[] codeBytes, byte[] keyBytes) throws Exception {
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
		DESKeySpec keySpec = new DESKeySpec(keyBytes);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(codeBytes);
	}

	/**
	 * 生成key
	 * 
	 * @return
	 * @throws Exception
	 */
	public static byte[] initKey() throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(DES);
		kg.init(56);
		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}
}
