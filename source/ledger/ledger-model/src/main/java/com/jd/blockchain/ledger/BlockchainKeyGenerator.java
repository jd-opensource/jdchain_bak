package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoKeyPair;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.SignatureFunction;

/**
 * 区块链密钥生成器；
 * 
 * @author huanghaiquan
 *
 */
public class BlockchainKeyGenerator {
	
	public static final String DEFAULT_ALGORITHM = "ED25519";

	private BlockchainKeyGenerator() {
	}

	public static BlockchainKeyGenerator getInstance() {
		return new BlockchainKeyGenerator();
	}

	public BlockchainKeyPair generate() {
		return generate(DEFAULT_ALGORITHM);
	}

	public BlockchainKeyPair generate(String algorithmName) {
		CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm(algorithmName);
		return generate(algorithm);
	}
	
	public BlockchainKeyPair generate(CryptoAlgorithm signatureAlgorithm) {
		SignatureFunction signFunc = CryptoServiceProviders.getSignatureFunction(signatureAlgorithm);
		CryptoKeyPair cryptoKeyPair = signFunc.generateKeyPair();
		return new BlockchainKeyPair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
	}

}
