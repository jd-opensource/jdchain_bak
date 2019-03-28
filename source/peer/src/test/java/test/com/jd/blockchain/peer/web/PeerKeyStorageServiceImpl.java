//package test.com.jd.blockchain.peer.web;
//
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import javax.annotation.PostConstruct;
//
//import com.jd.blockchain.crypto.CryptoAlgorithm;
//import com.jd.blockchain.crypto.asymmetric.PrivKey;
//import com.jd.blockchain.crypto.asymmetric.PubKey;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.jd.blockchain.ledger.BlockchainIdentity;
//import com.jd.blockchain.ledger.BlockchainKeyGenerator;
//import com.jd.blockchain.ledger.BlockchainKeyPair;
//import com.jd.blockchain.ledger.DigitalSignature;
//import com.jd.blockchain.ledger.data.CryptoKeyEncoding;
//import com.jd.blockchain.ledger.data.SignatureUtils;
//import com.jd.blockchain.peer.PeerSettings;
//import com.jd.blockchain.peer.service.BlockchainKeyInfo;
//import com.jd.blockchain.peer.service.PeerKeyStorageService;
//
//import my.utils.io.ByteArray;
//
//@Service
//public class PeerKeyStorageServiceImpl implements PeerKeyStorageService {
//
//	@Autowired
//	private PeerSettings settings;
//
//	public static BlockchainKeyPair keyOfP1 = BlockchainKeyGenerator.getInstance().generate();
//	public static BlockchainKeyPair keyOfP2 = BlockchainKeyGenerator.getInstance().generate();
//
//	private Map<String, BlockchainKeyStore> keyStores = new ConcurrentHashMap<>();
//
//	private static String[] encodeToStringLines(BlockchainKeyStore keyStore) {
//		return new String[] { keyStore.getKeyInfo().getName(), keyStore.getKeyPair().getAddress(),
//				keyStore.getKeyPair().getPubKey().toString(), keyStore.getKeyPair().getPrivKey().toString() };
//	}
//
//	private static BlockchainKeyStore decodeFromStringLines(String[] lines) {
//		String name = lines[0];
//		String address = lines[1];
//		PubKey pubKey = (PubKey) CryptoKeyEncoding.fromBase58(lines[2]);
//		PrivKey privKey = (PrivKey) CryptoKeyEncoding.fromBase58(lines[3]);
//		return new BlockchainKeyStore(name, address, pubKey, privKey);
//	}
//
//	@PostConstruct
//	private void init() {
//		keyStores.put(keyOfP1.getAddress(), new BlockchainKeyStore("a", keyOfP1));
//		keyStores.put(keyOfP1.getAddress(), new BlockchainKeyStore("b", keyOfP1));
//	}
//
//	@Override
//	public BlockchainKeyInfo generateNewKey(String name) {
//		BlockchainKeyPair keypair = BlockchainKeyGenerator.getInstance().generate(CryptoAlgorithm.ED25519);
//		BlockchainKeyStore keystore = new BlockchainKeyStore(name, keypair);
//
//		keyStores.put(keypair.getAddress(), keystore);
//		return keystore.getKeyInfo();
//	}
//
//	@Override
//	public DigitalSignature sign(ByteArray data, String address) {
//		BlockchainKeyStore keystore = keyStores.get(address);
//		if (keystore == null) {
//			throw new IllegalArgumentException("Key not exist!");
//		}
//		return SignatureUtils.sign(data, keystore.getKeyPair());
//	}
//	
//	@Override
//	public DigitalSignature sign(byte[] data, String address) {
//		BlockchainKeyStore keystore = keyStores.get(address);
//		if (keystore == null) {
//			throw new IllegalArgumentException("Key not exist!");
//		}
//		return SignatureUtils.sign(data, keystore.getKeyPair());
//	}
//
//	@Override
//	public BlockchainKeyInfo getBlockchainKey(String address) {
//		BlockchainKeyStore keystore = keyStores.get(address);
//		if (keystore == null) {
//			throw new IllegalArgumentException("Key not exist!");
//		}
//		return keystore.getKeyInfo();
//	}
//
//	@Override
//	public BlockchainKeyInfo[] getBlockchainKeys() {
//		BlockchainKeyInfo[] keys = new BlockchainKeyInfo[keyStores.size()];
//		int i = 0;
//		for (BlockchainKeyStore keystore : keyStores.values()) {
//			keys[i] = keystore.getKeyInfo();
//			i++;
//		}
//		Arrays.sort(keys, new Comparator<BlockchainKeyInfo>() {
//			@Override
//			public int compare(BlockchainKeyInfo o1, BlockchainKeyInfo o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
//		return keys;
//	}
//
//	private static class BlockchainKeyStore {
//		private BlockchainKeyInfo keyInfo;
//
//		private BlockchainKeyPair keyPair;
//
//		public BlockchainKeyStore(String name, String address, PubKey pubKey, PrivKey privKey) {
//			keyInfo = new BlockchainKeyInfo();
//			keyInfo.setName(name);
//			keyInfo.setIdentity(new BlockchainIdentity(address, pubKey));
//			keyPair = new BlockchainKeyPair(address, pubKey, privKey);
//		}
//
//		public BlockchainKeyStore(String name, BlockchainKeyPair keypair) {
//			keyInfo = new BlockchainKeyInfo();
//			keyInfo.setName(name);
//			keyInfo.setIdentity(keypair.getIdentity());
//			this.keyPair = keypair;
//		}
//
//		public BlockchainKeyInfo getKeyInfo() {
//			return keyInfo;
//		}
//
//		public BlockchainKeyPair getKeyPair() {
//			return keyPair;
//		}
//
//	}
//
//}
