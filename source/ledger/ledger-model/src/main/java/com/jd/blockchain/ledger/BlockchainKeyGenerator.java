package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
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

	public BlockchainKeypair generate() {
		return generate(DEFAULT_ALGORITHM);
	}

	public BlockchainKeypair generate(String algorithmName) {
		CryptoAlgorithm algorithm = Crypto.getAlgorithm(algorithmName);
		return generate(algorithm);
	}
	
	public BlockchainKeypair generate(CryptoAlgorithm signatureAlgorithm) {
		SignatureFunction signFunc = Crypto.getSignatureFunction(signatureAlgorithm);
		AsymmetricKeypair cryptoKeyPair = signFunc.generateKeypair();
		return new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
	}

}
