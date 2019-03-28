package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;

/**
 * 区块链密钥生成器；
 * 
 * @author huanghaiquan
 *
 */
public class BlockchainKeyGenerator {

	private BlockchainKeyGenerator() {
	}

	public static BlockchainKeyGenerator getInstance() {
		return new BlockchainKeyGenerator();
	}

	public BlockchainKeyPair generate() {
		return generate(CryptoAlgorithm.ED25519);
	}

	public BlockchainKeyPair generate(CryptoAlgorithm signatureAlgorithm) {
		CryptoKeyPair cryptoKeyPair = CryptoUtils.sign(signatureAlgorithm).generateKeyPair();
		return new BlockchainKeyPair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
	}

}
