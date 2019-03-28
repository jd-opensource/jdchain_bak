package com.jd.blockchain.crypto.service.sm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoAlgorithmDefinition;
import com.jd.blockchain.crypto.CryptoFunction;
import com.jd.blockchain.crypto.CryptoService;
import com.jd.blockchain.provider.NamedProvider;

/**
 * 国密软实现；
 * 
 * @author huanghaiquan
 *
 */
@NamedProvider("SM-SOFTWARE")
public class SMCryptoService implements CryptoService {

	public static final CryptoAlgorithm SM2_ALGORITHM = CryptoAlgorithmDefinition.defineSignature("SM2",
			true, (byte) 2);

	public static final CryptoAlgorithm SM3_ALGORITHM = CryptoAlgorithmDefinition.defineHash("SM3", (byte) 3);

	public static final CryptoAlgorithm SM4_ALGORITHM = CryptoAlgorithmDefinition.defineSymmetricEncryption("SM4",
			(byte) 4);

	public static final SM2CryptoFunction SM2 = new SM2CryptoFunction();
	public static final SM3HashFunction SM3 = new SM3HashFunction();
	public static final SM4EncryptionFunction SM4 = new SM4EncryptionFunction();

	private static final Collection<CryptoFunction> FUNCTIONS;

	static {
		List<CryptoFunction> funcs = Arrays.asList(SM2, SM3, SM4);
		FUNCTIONS = Collections.unmodifiableList(funcs);
	}

	@Override
	public Collection<CryptoFunction> getFunctions() {
		return FUNCTIONS;
	}

}
