package com.jd.blockchain.crypto.service.classic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.jd.blockchain.crypto.CryptoFunction;
import com.jd.blockchain.crypto.CryptoService;
import com.jd.blockchain.provider.NamedProvider;

@NamedProvider("CLASSIC")
public class ClassicCryptoService implements CryptoService {


	public static final AESEncryptionFunction AES = new AESEncryptionFunction();

	public static final ED25519SignatureFunction ED25519 = new ED25519SignatureFunction();

	public static final RIPEMD160HashFunction RIPEMD160 = new RIPEMD160HashFunction();

	public static final SHA256HashFunction SHA256 = new SHA256HashFunction();

	public static final JVMSecureRandomFunction JVM_SECURE_RANDOM = new JVMSecureRandomFunction();

	public static final ECDSASignatureFunction ECDSA = new ECDSASignatureFunction();

	public static final RSACryptoFunction RSA = new RSACryptoFunction();

	private static final Collection<CryptoFunction> FUNCTIONS;

	static {
		List<CryptoFunction> funcs = Arrays.asList(AES, ED25519, ECDSA, RSA, RIPEMD160, SHA256, JVM_SECURE_RANDOM);
		FUNCTIONS = Collections.unmodifiableList(funcs);
	}

	@Override
	public Collection<CryptoFunction> getFunctions() {
		return FUNCTIONS;
	}

}
