package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.Bytes;

/**
 * 区块链密钥对；
 * 
 * @author huanghaiquan
 *
 */
public class BlockchainKeypair extends AsymmetricKeypair {

	private BlockchainIdentity id;

//	public BlockchainKeyPair(CryptoAlgorithm algorithm, ByteArray pubKeyBytes, ByteArray privKeyBytes) {
//		this.id = new BlockchainIdentity(algorithm, pubKeyBytes);
//		privKey = new PrivKey(algorithm, privKeyBytes.bytes());
//	}

	public BlockchainKeypair(String address, PubKey pubKey, PrivKey privKey) {
		super(pubKey, privKey);
		if (pubKey.getAlgorithm() != privKey.getAlgorithm()) {
			throw new IllegalArgumentException("The PublicKey's algorithm is different from the PrivateKey's!");
		}
		this.id = new BlockchainIdentityData(Bytes.fromBase58(address), pubKey);
	}

	public BlockchainKeypair(PubKey pubKey, PrivKey privKey) {
		super(pubKey, privKey);
		if (pubKey.getAlgorithm() != privKey.getAlgorithm()) {
			throw new IllegalArgumentException("The PublicKey's algorithm is different from the PrivateKey's!");
		}
		this.id = new BlockchainIdentityData(pubKey);
	}

	public Bytes getAddress() {
		return id.getAddress();
	}

	public BlockchainIdentity getIdentity() {
		return id;
	}
}
