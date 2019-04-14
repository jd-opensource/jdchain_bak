package com.jd.blockchain.crypto;

import java.io.InputStream;
import java.io.OutputStream;

public interface SymmetricEncryptionFunction extends SymmetricKeyGenerator, CryptoFunction {

	/**
	 * 加密；
	 *
	 * @param key 密钥；
	 * @param data 明文；
	 * @return
	 */
	Ciphertext encrypt(SymmetricKey key, byte[] data);
	
	/**
	 * 加密明文的输入流，把密文写入输出流；
	 * 
	 * @param key 密钥；
	 * @param in 明文的输入流；
	 * @param out 密文的输出流；
	 */
	void encrypt(SymmetricKey key, InputStream in, OutputStream out);

	/**
	 * 解密；
	 * 
	 * @param key 密钥；
	 * @param ciphertext 密文；
	 * @return
	 */
	byte[] decrypt(SymmetricKey key, Ciphertext ciphertext);
	
	/**
	 * 解密密文的输入流，把明文写入输出流；<br>
	 * 
	 * 注：实现者不应在方法内部关闭参数指定的输入输出流；
	 * 
	 * @param key 密钥；
	 * @param in 密文的输入流；
	 * @param out 明文的输出流；
	 */
	void decrypt(SymmetricKey key, InputStream in, OutputStream out);

	/**
	 * 校验对称密钥格式是否满足要求；
	 *
	 * @param symmetricKeyBytes 包含算法标识、密钥掩码和对称密钥的字节数组
	 * @return 是否满足指定算法的对称密钥格式
	 */
	boolean supportSymmetricKey(byte[] symmetricKeyBytes);

	/**
	 * 将字节数组形式的密钥转换成SymmetricKey格式；
	 *
	 * @param symmetricKeyBytes 包含算法标识、密钥掩码和对称密钥的字节数组
	 * @return SymmetricKey形式的对称密钥
	 */
	SymmetricKey resolveSymmetricKey(byte[] symmetricKeyBytes);

	/**
	 * 校验密文格式是否满足要求；
	 *
	 * @param ciphertextBytes 包含算法标识和密文的字节数组
	 * @return 是否满足指定算法的密文格式
	 */
	boolean supportCiphertext(byte[] ciphertextBytes);

	/**
	 * 将字节数组形式的密文转换成SymmetricCiphertext格式；
	 *
	 * @param ciphertextBytes 包含算法标识和密文的字节数组
	 * @return SymmetricCiphertext形式的签名摘要
	 */
	SymmetricCiphertext resolveCiphertext(byte[] ciphertextBytes);


}
