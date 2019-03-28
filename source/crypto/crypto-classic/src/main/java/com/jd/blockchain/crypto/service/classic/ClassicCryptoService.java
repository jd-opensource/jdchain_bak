package com.jd.blockchain.crypto.service.classic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoAlgorithmDefinition;
import com.jd.blockchain.crypto.CryptoFunction;
import com.jd.blockchain.crypto.CryptoService;
import com.jd.blockchain.provider.NamedProvider;

@NamedProvider("CLASSIC")
public class ClassicCryptoService implements CryptoService {

	public static final CryptoAlgorithm ED25519_ALGORITHM = CryptoAlgorithmDefinition.defineSignature("ED25519",
			false, (byte) 21);
	public static final CryptoAlgorithm ECDSA_ALGORITHM = CryptoAlgorithmDefinition.defineSignature("ECDSA",
			false, (byte) 22);

	public static final CryptoAlgorithm RSA_ALGORITHM = CryptoAlgorithmDefinition.defineSignature("RSA",
			true, (byte) 23);

	public static final CryptoAlgorithm SHA256_ALGORITHM = CryptoAlgorithmDefinition.defineHash("SHA256",
			(byte) 24);

	public static final CryptoAlgorithm RIPEMD160_ALGORITHM = CryptoAlgorithmDefinition.defineHash("RIPEMD160",
			(byte) 25);

	public static final CryptoAlgorithm AES_ALGORITHM = CryptoAlgorithmDefinition.defineSymmetricEncryption("AES",
			(byte) 26);

	public static final CryptoAlgorithm JVM_SECURE_RANDOM_ALGORITHM = CryptoAlgorithmDefinition
			.defineRandom("JVM-SECURE-RANDOM", (byte) 27);

	public static final AESEncryptionFunction AES = new AESEncryptionFunction();

	public static final ED25519SignatureFunction ED25519 = new ED25519SignatureFunction();

	public static final RIPEMD160HashFunction RIPEMD160 = new RIPEMD160HashFunction();

	public static final SHA256HashFunction SHA256 = new SHA256HashFunction();

	public static final JVMSecureRandomFunction JVM_SECURE_RANDOM = new JVMSecureRandomFunction();

	// public static final ECDSASignatureFunction ECDSA = new
	// ECDSASignatureFunction();

	private static final Collection<CryptoFunction> FUNCTIONS;

	static {
		List<CryptoFunction> funcs = Arrays.asList(AES, ED25519, RIPEMD160, SHA256, JVM_SECURE_RANDOM);
		FUNCTIONS = Collections.unmodifiableList(funcs);
	}

	@Override
	public Collection<CryptoFunction> getFunctions() {
		return FUNCTIONS;
	}

}
