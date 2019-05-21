package com.jd.blockchain.crypto;

import java.io.InputStream;
import java.io.OutputStream;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.io.BytesUtils;

@DataContract(code = DataCodes.CRYPTO_ALGORITHM)
public interface CryptoAlgorithm {

	/**
	 * 随机数算法标识；
	 */
	static final int RANDOM_ALGORITHM = 0x1000;

	/**
	 * 哈希数算法标识；
	 */
	static final int HASH_ALGORITHM = 0x2000;

	/**
	 * 签名算法标识；
	 */
	static final int SIGNATURE_ALGORITHM = 0x4000;

	/**
	 * 加密算法标识；
	 */
	static final int ENCRYPTION_ALGORITHM = 0x8000;

	/**
	 * 扩展密码算法标识； <br>
	 * 表示除了
	 * {@link #RANDOM_ALGORITHM}、{@link #HASH_ALGORITHM}、{@link #SIGNATURE_ALGORITHM}、{@link #ENCRYPTION_ALGORITHM}
	 * 之外的其它非标准分类的密码算法，诸如加法同态算法、多方求和算法等；
	 */
	static final int EXT_ALGORITHM = 0x0000;

	/**
	 * 非对称密钥标识；
	 */
	static final int ASYMMETRIC_KEY = 0x0100;

	/**
	 * 对称密钥标识；
	 */
	static final int SYMMETRIC_KEY = 0x0200;

	/**
	 * 算法编码的字节长度；等同于 {@link #getCodeBytes(CryptoAlgorithm)} 返回的字节数组的长度；
	 */
	static final int CODE_SIZE = 2;

	/**
	 * 密码算法的唯一编码；
	 * <p>
	 * 长度16位，高4位标识算法类型（包括： {@link #RANDOM_ALGORITHM}, {@link #HASH_ALGORITHM},
	 * {@link #SIGNATURE_ALGORITHM}, {@link #ENCRYPTION_ALGORITHM},
	 * {@link #EXT_ALGORITHM}) 5 种）; 接下来4位标识密钥类型（包括：{@link #SYMMETRIC_KEY},
	 * {@link #ASYMMETRIC_KEY}）； 最后8位是算法唯一ID；
	 */
	@DataField(order = 0, primitiveType = PrimitiveType.INT16)
	short code();

	/**
	 * 算法名称；
	 * <p>
	 * 
	 * 实现者应该遵循“英文字符大写”的命名规范，并确保唯一性；<br>
	 * 例如，sha256 和 SHA256 将被视为相同的名称；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.TEXT)
	String name();
	

	public static String getString(CryptoAlgorithm algorithm) {
		return String.format("%s[%s]", algorithm.name(), (algorithm.code() & 0xFFFF));
	}

	/**
	 * 
	 * @return
	 */
	static byte[] getCodeBytes(CryptoAlgorithm algorithm) {
		return BytesUtils.toBytes(algorithm.code());
	}

	static short resolveCode(byte[] codeBytes) {
		return BytesUtils.toShort(codeBytes, 0);
	}

	static short resolveCode(byte[] codeBytes, int offset) {
		return BytesUtils.toShort(codeBytes, offset);
	}

	static short resolveCode(InputStream in) {
		return BytesUtils.readShort(in);
	}

	static int writeCode(short code, OutputStream out) {
		return BytesUtils.writeShort(code, out);
	}

	static boolean match(CryptoAlgorithm algorithm, byte[] algorithmBytes) {
		return algorithm.code() == BytesUtils.toShort(algorithmBytes, 0);
	}

	static boolean match(CryptoAlgorithm algorithm, byte[] algorithmBytes, int offset) {
		return algorithm.code() == BytesUtils.toShort(algorithmBytes, offset);
	}

	/**
	 * 是否属于随机数算法；
	 * 
	 * @return
	 */
	static boolean isRandomAlgorithm(CryptoAlgorithm algorithm) {
		return RANDOM_ALGORITHM == (algorithm.code() & RANDOM_ALGORITHM);
	}

	/**
	 * 是否属于摘要算法；
	 * 
	 * @return
	 */
	static boolean isHashAlgorithm(CryptoAlgorithm algorithm) {
		return HASH_ALGORITHM == (algorithm.code() & HASH_ALGORITHM);
	}

	/**
	 * 是否属于摘要算法；
	 * 
	 * @return
	 */
	static boolean isHashAlgorithm(short algorithm) {
		return HASH_ALGORITHM == (algorithm & HASH_ALGORITHM);
	}

	/**
	 * 是否属于签名算法；
	 * 
	 * @return
	 */
	static boolean isSignatureAlgorithm(short algorithm) {
		return SIGNATURE_ALGORITHM == (algorithm & SIGNATURE_ALGORITHM);
	}

	/**
	 * 是否属于签名算法；
	 * 
	 * @return
	 */
	static boolean isSignatureAlgorithm(CryptoAlgorithm algorithm) {
		return SIGNATURE_ALGORITHM == (algorithm.code() & SIGNATURE_ALGORITHM);
	}

	/**
	 * 是否属于加密算法；
	 * 
	 * @return
	 */
	static boolean isEncryptionAlgorithm(short algorithm) {
		return ENCRYPTION_ALGORITHM == (algorithm & ENCRYPTION_ALGORITHM);
	}

	/**
	 * 是否属于加密算法；
	 * 
	 * @return
	 */
	static boolean isEncryptionAlgorithm(CryptoAlgorithm algorithm) {
		return ENCRYPTION_ALGORITHM == (algorithm.code() & ENCRYPTION_ALGORITHM);
	}

	/**
	 * 是否属于扩展密码算法；
	 * 
	 * @return
	 */
	static boolean isExtAlgorithm(CryptoAlgorithm algorithm) {
		return EXT_ALGORITHM == (algorithm.code() & 0xF000);
	}

	/**
	 * 算法是否包含非对称密钥；
	 * 
	 * @return
	 */
	static boolean hasAsymmetricKey(short algorithm) {
		return ASYMMETRIC_KEY == (algorithm & ASYMMETRIC_KEY);
	}

	/**
	 * 算法是否包含非对称密钥；
	 * 
	 * @return
	 */
	static boolean hasAsymmetricKey(CryptoAlgorithm algorithm) {
		return ASYMMETRIC_KEY == (algorithm.code() & ASYMMETRIC_KEY);
	}

	/**
	 * 算法是否包含对称密钥；
	 * 
	 * @return
	 */
	static boolean hasSymmetricKey(short algorithm) {
		return SYMMETRIC_KEY == (algorithm & SYMMETRIC_KEY);
	}

	/**
	 * 算法是否包含对称密钥；
	 * 
	 * @return
	 */
	static boolean hasSymmetricKey(CryptoAlgorithm algorithm) {
		return SYMMETRIC_KEY == (algorithm.code() & SYMMETRIC_KEY);
	}

	/**
	 * 是否属于对称加密算法；
	 * 
	 * @param algorithm
	 * @return
	 */
	static boolean isSymmetricEncryptionAlgorithm(CryptoAlgorithm algorithm) {
		return isEncryptionAlgorithm(algorithm) && hasSymmetricKey(algorithm);
	}

	/**
	 * 是否属于非对称加密算法；
	 * 
	 * @param algorithm
	 * @return
	 */
	static boolean isAsymmetricEncryptionAlgorithm(short algorithm) {
		return isEncryptionAlgorithm(algorithm) && hasAsymmetricKey(algorithm);
	}

	/**
	 * 是否属于非对称加密算法；
	 * 
	 * @param algorithm
	 * @return
	 */
	static boolean isAsymmetricEncryptionAlgorithm(CryptoAlgorithm algorithm) {
		return isEncryptionAlgorithm(algorithm) && hasAsymmetricKey(algorithm);
	}

	static boolean equals(CryptoAlgorithm algorithm1, CryptoAlgorithm algorithm2) {
		return algorithm1.code() == algorithm2.code();
	}
}
