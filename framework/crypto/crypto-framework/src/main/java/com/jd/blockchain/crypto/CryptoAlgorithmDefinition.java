package com.jd.blockchain.crypto;

public final class CryptoAlgorithmDefinition implements CryptoAlgorithm {

	private short code;

	private String name;

	CryptoAlgorithmDefinition(String name, short code) {
		this.code = code;
		this.name = name;
	}

	@Override
	public short code() {
		return this.code;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return CryptoAlgorithm.getString(this);
	}

	/**
	 * 声明一项哈希算法；
	 * 
	 * @param name 算法名称；
	 * @param uid  算法ID；需要在同类算法中保持唯一性；
	 * @return
	 */
	public static CryptoAlgorithm defineHash(String name, byte uid) {
		short code = (short) (HASH_ALGORITHM | (uid & 0x00FF));
		return new CryptoAlgorithmDefinition(name, code);
	}

	/**
	 * 声明一项非对称密码算法；
	 * 
	 * @param name 算法名称；
	 * @param uid  算法ID；需要在同类算法中保持唯一性；
	 * @return
	 */
	public static CryptoAlgorithm defineSignature(String name, boolean encryptable, byte uid) {
		short code;
		if (encryptable) {
			code = (short) (SIGNATURE_ALGORITHM | ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | (uid & 0x00FF));
		} else {
			code = (short) (SIGNATURE_ALGORITHM | ASYMMETRIC_KEY | (uid & 0x00FF));
		}
		return new CryptoAlgorithmDefinition(name, code);
	}

	/**
	 * 声明一项非对称加密算法；
	 *
	 * @param name 算法名称；
	 * @param uid  算法ID；需要在同类算法中保持唯一性；
	 * @return
	 */
	public static CryptoAlgorithm defineAsymmetricEncryption(String name, byte uid) {
		short code = (short) (ENCRYPTION_ALGORITHM | ASYMMETRIC_KEY | (uid & 0x00FF));
		return new CryptoAlgorithmDefinition(name, code);
	}

	/**
	 * 声明一项对称密码算法；
	 * 
	 * @param name 算法名称；
	 * @param uid  算法ID；需要在同类算法中保持唯一性；
	 * @return
	 */
	public static CryptoAlgorithm defineSymmetricEncryption(String name, byte uid) {
		short code = (short) (ENCRYPTION_ALGORITHM | SYMMETRIC_KEY | (uid & 0x00FF));
		return new CryptoAlgorithmDefinition(name, code);
	}

	/**
	 * 声明一项随机数算法；
	 * 
	 * @param name 算法名称；
	 * @param uid  算法ID；需要在同类算法中保持唯一性；
	 * @return
	 */
	public static CryptoAlgorithm defineRandom(String name, byte uid) {
		short code = (short) (RANDOM_ALGORITHM | (uid & 0x00FF));
		return new CryptoAlgorithmDefinition(name, code);
	}

	/**
	 * 声明一项扩展的密码算法；
	 * 
	 * @param name 算法名称；
	 * @param uid  算法ID；需要在同类算法中保持唯一性；
	 * @return
	 */
	public static CryptoAlgorithm definExt(String name, byte uid) {
		short code = (short) (EXT_ALGORITHM | (uid & 0x00FF));
		return new CryptoAlgorithmDefinition(name, code);
	}

}
