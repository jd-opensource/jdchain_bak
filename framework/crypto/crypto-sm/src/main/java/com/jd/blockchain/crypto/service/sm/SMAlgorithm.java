package com.jd.blockchain.crypto.service.sm;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoAlgorithmDefinition;

public final class SMAlgorithm {

	public static final CryptoAlgorithm SM2 = CryptoAlgorithmDefinition.defineSignature("SM2", true, (byte) 2);

	public static final CryptoAlgorithm SM3 = CryptoAlgorithmDefinition.defineHash("SM3", (byte) 3);

	public static final CryptoAlgorithm SM4 = CryptoAlgorithmDefinition.defineSymmetricEncryption("SM4", (byte) 4);

	private SMAlgorithm() {
	}

}
