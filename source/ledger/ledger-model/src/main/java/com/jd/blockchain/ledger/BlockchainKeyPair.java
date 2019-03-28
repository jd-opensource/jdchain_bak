package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.utils.Bytes;

/**
 * 区块链密钥对；
 * 
 * @author huanghaiquan
 *
 */
public class BlockchainKeyPair extends CryptoKeyPair {

	private BlockchainIdentity id;

//	public BlockchainKeyPair(CryptoAlgorithm algorithm, ByteArray pubKeyBytes, ByteArray privKeyBytes) {
//		this.id = new BlockchainIdentity(algorithm, pubKeyBytes);
//		privKey = new PrivKey(algorithm, privKeyBytes.bytes());
//	}

	public BlockchainKeyPair(String address, PubKey pubKey, PrivKey privKey) {
		super(pubKey, privKey);
		if (pubKey.getAlgorithm() != privKey.getAlgorithm()) {
			throw new IllegalArgumentException("The PublicKey's algorithm is different from the PrivateKey's!");
		}
		this.id = new BlockchainIdentityData(Bytes.fromBase58(address), pubKey);
	}

	public BlockchainKeyPair(PubKey pubKey, PrivKey privKey) {
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
