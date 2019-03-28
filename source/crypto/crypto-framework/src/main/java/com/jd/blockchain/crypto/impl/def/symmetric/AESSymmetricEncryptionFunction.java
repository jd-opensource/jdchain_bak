package com.jd.blockchain.crypto.impl.def.symmetric;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoKey;
import com.jd.blockchain.crypto.symmetric.SymmetricCiphertext;
import com.jd.blockchain.crypto.symmetric.SymmetricEncryptionFunction;
import com.jd.blockchain.crypto.symmetric.SymmetricKey;
import com.jd.blockchain.utils.security.AESUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.jd.blockchain.crypto.CryptoAlgorithm.AES;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_BYTES;
import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC_KEY;
import static com.jd.blockchain.crypto.base.BaseCryptoKey.KEY_TYPE_BYTES;

public class AESSymmetricEncryptionFunction implements SymmetricEncryptionFunction {

	private static final int KEY_SIZE = 16;
	private static final int BLOCK_SIZE = 16;

	private static final int SYMMETRICKEY_LENGTH = ALGORYTHM_BYTES + KEY_TYPE_BYTES + KEY_SIZE;

	public AESSymmetricEncryptionFunction() {
	}

	@Override
	public Ciphertext encrypt(SymmetricKey key, byte[] data) {

		byte[] rawKeyBytes = key.getRawKeyBytes();

		// 验证原始密钥长度为128比特，即16字节
		if (rawKeyBytes.length != KEY_SIZE)
			throw new IllegalArgumentException("This key has wrong format!");

		// 验证密钥数据的算法标识对应AES算法
		if (key.getAlgorithm() != AES)
			throw new IllegalArgumentException("The is not AES symmetric key!");

		// 调用底层AES128算法并计算密文数据
		return new SymmetricCiphertext(AES, AESUtils.encrypt(data, rawKeyBytes));
	}

	@Override
	public void encrypt(SymmetricKey key, InputStream in, OutputStream out) {

		// 读输入流得到明文，加密，密文数据写入输出流
		try {
			byte[] aesData = new byte[in.available()];
			in.read(aesData);
			in.close();

			out.write(encrypt(key, aesData).toBytes());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] decrypt(SymmetricKey key, Ciphertext ciphertext) {

		byte[] rawKeyBytes = key.getRawKeyBytes();
		byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();

		// 验证原始密钥长度为128比特，即16字节
		if (rawKeyBytes.length != KEY_SIZE)
			throw new IllegalArgumentException("This key has wrong format!");

		// 验证密钥数据的算法标识对应AES算法
		if (key.getAlgorithm().CODE != AES.CODE)
			throw new IllegalArgumentException("The is not AES symmetric key!");

		// 验证原始密文长度为分组长度的整数倍
		if (rawCiphertextBytes.length % BLOCK_SIZE != 0)
			throw new IllegalArgumentException("This ciphertext has wrong format!");

		// 验证密文数据算法标识对应AES算法
		if (ciphertext.getAlgorithm() != AES)
			throw new IllegalArgumentException("This is not AES ciphertext!");

		// 调用底层AES128算法解密，得到明文
		return AESUtils.decrypt(rawCiphertextBytes, rawKeyBytes);
	}

	@Override
	public void decrypt(SymmetricKey key, InputStream in, OutputStream out) {

		// 读输入流得到密文数据，解密，明文写入输出流
		try {
			byte[] aesData = new byte[in.available()];
			in.read(aesData);
			in.close();

			if (!supportCiphertext(aesData))
				throw new IllegalArgumentException("InputStream is not valid AES ciphertext!");

			out.write(decrypt(key, resolveCiphertext(aesData)));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
    public boolean supportSymmetricKey(byte[] symmetricKeyBytes) {
        //验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，字节数组的算法标识对应AES算法且密钥密钥类型是对称密钥
        return symmetricKeyBytes.length == SYMMETRICKEY_LENGTH && symmetricKeyBytes[0] == AES.CODE && symmetricKeyBytes[1] == SYMMETRIC_KEY.CODE;
    }

	@Override
	public SymmetricKey resolveSymmetricKey(byte[] symmetricKeyBytes) {
		// 由框架调用 support 方法检查有效性，在此不做重复检查；
		return new SymmetricKey(symmetricKeyBytes);
	}

	@Override
	public boolean supportCiphertext(byte[] ciphertextBytes) {
		// 验证(输入字节数组长度-算法标识长度)是分组长度的整数倍，字节数组的算法标识对应AES算法
		return (ciphertextBytes.length - ALGORYTHM_BYTES) % BLOCK_SIZE == 0 && ciphertextBytes[0] == AES.CODE;
	}

	@Override
	public SymmetricCiphertext resolveCiphertext(byte[] ciphertextBytes) {
        // 由框架调用 support 方法检查有效性，在此不做重复检查；
		return new SymmetricCiphertext(ciphertextBytes);
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return AES;
	}

	@Override
	public CryptoKey generateSymmetricKey() {
		// 根据对应的标识和原始密钥生成相应的密钥数据
		return new SymmetricKey(AES, AESUtils.generateKey128_Bytes());
	}
}
