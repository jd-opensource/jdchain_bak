package com.jd.blockchain.crypto.service.classic;

import static com.jd.blockchain.crypto.BaseCryptoKey.KEY_TYPE_BYTES;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_CODE_SIZE;
import static com.jd.blockchain.crypto.CryptoKeyType.SYMMETRIC;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.CryptoKey;
import com.jd.blockchain.crypto.SymmetricCiphertext;
import com.jd.blockchain.crypto.SymmetricEncryptionFunction;
import com.jd.blockchain.crypto.SymmetricKey;
import com.jd.blockchain.utils.security.AESUtils;

public class AESEncryptionFunction implements SymmetricEncryptionFunction {

	public static final CryptoAlgorithm AES = ClassicAlgorithm.AES;

	private static final int KEY_SIZE = 128 / 8;
	private static final int BLOCK_SIZE = 128 / 8;

	// AES-ECB
	private static final int PLAINTEXT_BUFFER_LENGTH = 256;
	private static final int CIPHERTEXT_BUFFER_LENGTH = 256 + 16 + 2;

	private static final int SYMMETRICKEY_LENGTH = ALGORYTHM_CODE_SIZE + KEY_TYPE_BYTES + KEY_SIZE;

	AESEncryptionFunction() {
	}

	@Override
	public Ciphertext encrypt(SymmetricKey key, byte[] data) {

		byte[] rawKeyBytes = key.getRawKeyBytes();

		// 验证原始密钥长度为128比特，即16字节
		if (rawKeyBytes.length != KEY_SIZE) {
			throw new CryptoException("This key has wrong format!");
		}

		// 验证密钥数据的算法标识对应AES算法
		if (key.getAlgorithm() != AES.code()) {
			throw new CryptoException("The is not AES symmetric key!");
		}

		// 调用底层AES128算法并计算密文数据
		return new SymmetricCiphertext(AES, AESUtils.encrypt(data, rawKeyBytes));
	}

	@Override
	public void encrypt(SymmetricKey key, InputStream in, OutputStream out) {
		// 读输入流得到明文，加密，密文数据写入输出流
		try {


			byte[] buffBytes = new byte[PLAINTEXT_BUFFER_LENGTH];

			// The final byte of plaintextWithPadding represents the length of padding in the first 256 bytes,
			// and the padded value in hexadecimal
			byte[] plaintextWithPadding = new byte[buffBytes.length + 1];

			byte padding;

			int len;
			int i;

			while((len=in.read(buffBytes)) > 0) {
				padding = (byte) (PLAINTEXT_BUFFER_LENGTH - len);
				i = len;
				while (i < plaintextWithPadding.length) {
					plaintextWithPadding[i] = padding;
					i++;
				}
				out.write(encrypt(key,plaintextWithPadding).toBytes());
			}
//			// TODO: 错误地使用 available 方法；
//			int size = in.available();
//			if (size < 1){
//				throw new CryptoException("The input is null!");
//			}
//
//			byte[] aesData = new byte[size];
//
//			if (in.read(aesData) != -1) {
//				out.write(encrypt(key, aesData).);
//			}
//
//			in.close();
//			out.close();

		} catch (IOException e) {
			throw new CryptoException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] decrypt(SymmetricKey key, Ciphertext ciphertext) {
		byte[] rawKeyBytes = key.getRawKeyBytes();
		byte[] rawCiphertextBytes = ciphertext.getRawCiphertext();

		// 验证原始密钥长度为128比特，即16字节
		if (rawKeyBytes.length != KEY_SIZE) {
			throw new CryptoException("This key has wrong format!");
		}

		// 验证密钥数据的算法标识对应AES算法
		if (key.getAlgorithm() != AES.code()) {
			throw new CryptoException("The is not AES symmetric key!");
		}

		// 验证原始密文长度为分组长度的整数倍
		if (rawCiphertextBytes.length % BLOCK_SIZE != 0) {
			throw new CryptoException("This ciphertext has wrong format!");
		}

		// 验证密文数据算法标识对应AES算法
		if (ciphertext.getAlgorithm() != AES.code()) {
			throw new CryptoException("This is not AES ciphertext!");
		}

		// 调用底层AES128算法解密，得到明文
		return AESUtils.decrypt(rawCiphertextBytes, rawKeyBytes);
	}

	@Override
	public void decrypt(SymmetricKey key, InputStream in, OutputStream out) {
		// 读输入流得到密文数据，解密，明文写入输出流
		try {
			byte[] buffBytes = new byte[CIPHERTEXT_BUFFER_LENGTH];
			byte[] plaintextWithPadding;

			byte padding;
			byte[] plaintext;

			int len,i;
			while ((len = in.read(buffBytes)) > 0) {
				if (len != CIPHERTEXT_BUFFER_LENGTH) {
					throw new CryptoException("inputStream's length is wrong!");
				}
				if (!supportCiphertext(buffBytes)) {
					throw new CryptoException("InputStream is not valid AES ciphertext!");
				}

				plaintextWithPadding = decrypt(key,resolveCiphertext(buffBytes));

				if (plaintextWithPadding.length != (PLAINTEXT_BUFFER_LENGTH + 1)) {
					throw new CryptoException("The decrypted plaintext is invalid");
				}

				padding = plaintextWithPadding[PLAINTEXT_BUFFER_LENGTH];
				i = PLAINTEXT_BUFFER_LENGTH;

				while ((PLAINTEXT_BUFFER_LENGTH - padding) < i) {

					if (plaintextWithPadding[i] != padding) {
						throw new CryptoException("The inputSteam padding is invalid!");
					}
					i--;
				}
				plaintext = new byte[PLAINTEXT_BUFFER_LENGTH - padding];
				System.arraycopy(plaintextWithPadding,0,plaintext,0,plaintext.length);
				out.write(plaintext);
			}

//			// TODO: 错误地使用 available 方法；
//			byte[] aesData = new byte[in.available()];
//			in.read(aesData);
//			in.close();
//
//			if (!supportCiphertext(aesData)) {
//				throw new CryptoException("InputStream is not valid AES ciphertext!");
//			}
//
//			out.write(decrypt(key, resolveCiphertext(aesData)));
//			out.close();
		} catch (IOException e) {
			throw new CryptoException(e.getMessage(), e);
		}
	}

	@Override
	public boolean supportSymmetricKey(byte[] symmetricKeyBytes) {
		// 验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，字节数组的算法标识对应AES算法且密钥密钥类型是对称密钥
		return symmetricKeyBytes.length == SYMMETRICKEY_LENGTH && CryptoAlgorithm.match(AES, symmetricKeyBytes)
				&& symmetricKeyBytes[ALGORYTHM_CODE_SIZE] == SYMMETRIC.CODE;
	}

	@Override
	public SymmetricKey resolveSymmetricKey(byte[] symmetricKeyBytes) {
		if (supportSymmetricKey(symmetricKeyBytes)) {
			return new SymmetricKey(symmetricKeyBytes);
		} else {
			throw new CryptoException("symmetricKeyBytes is invalid!");
		}
	}

	@Override
	public boolean supportCiphertext(byte[] ciphertextBytes) {
		// 验证(输入字节数组长度-算法标识长度)是分组长度的整数倍，字节数组的算法标识对应AES算法
		return (ciphertextBytes.length - ALGORYTHM_CODE_SIZE) % BLOCK_SIZE == 0
				&& CryptoAlgorithm.match(AES, ciphertextBytes);
	}

	@Override
	public SymmetricCiphertext resolveCiphertext(byte[] ciphertextBytes) {
		if (supportCiphertext(ciphertextBytes)) {
			return new SymmetricCiphertext(ciphertextBytes);
		} else {
			throw new CryptoException("ciphertextBytes is invalid!");
		}
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return AES;
	}

	@Override
	public SymmetricKey generateSymmetricKey() {
		// 根据对应的标识和原始密钥生成相应的密钥数据
		return new SymmetricKey(AES, AESUtils.generateKey128_Bytes());
	}
}
