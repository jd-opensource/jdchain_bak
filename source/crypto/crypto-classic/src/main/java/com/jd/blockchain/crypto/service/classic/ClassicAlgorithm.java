package com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoAlgorithmDefinition;

public final class ClassicAlgorithm {

	public static final CryptoAlgorithm ED25519 = CryptoAlgorithmDefinition.defineSignature("ED25519", false,
			(byte) 21);

	public static final CryptoAlgorithm ECDSA = CryptoAlgorithmDefinition.defineSignature("ECDSA", false,
			(byte) 22);

	public static final CryptoAlgorithm RSA = CryptoAlgorithmDefinition.defineSignature("RSA", true,
			(byte) 23);

	public static final CryptoAlgorithm SHA256 = CryptoAlgorithmDefinition.defineHash("SHA256",
			(byte) 24);

	public static final CryptoAlgorithm RIPEMD160 = CryptoAlgorithmDefinition.defineHash("RIPEMD160",
			(byte) 25);

	public static final CryptoAlgorithm AES = CryptoAlgorithmDefinition.defineSymmetricEncryption("AES",
			(byte) 26);

	public static final CryptoAlgorithm JVM_SECURE_RANDOM = CryptoAlgorithmDefinition.defineRandom("JVM-SECURE-RANDOM",
			(byte) 27);

	private ClassicAlgorithm() {

	}

}
