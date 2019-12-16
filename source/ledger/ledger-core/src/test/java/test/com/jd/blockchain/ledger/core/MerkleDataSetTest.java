package test.com.jd.blockchain.ledger.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.MerkleDataSet;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;
import com.jd.blockchain.utils.Dataset;
import com.jd.blockchain.utils.DatasetHelper;
import com.jd.blockchain.utils.io.BytesUtils;

public class MerkleDataSetTest {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };
	
	/**
	 * 测试存储的增长；
	 */
	@Test
	public void testKeyIndex() {
		
		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}
		
		String keyPrefix = "";
		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);
		cryptoConfig.setAutoVerifyHash(true);
		
		MemoryKVStorage storage = new MemoryKVStorage();
		
		MerkleDataSet mds = new MerkleDataSet(cryptoConfig, keyPrefix, storage, storage);
		mds.setValue(Bytes.fromString("A"), "A".getBytes(), -1);
		mds.setValue(Bytes.fromString("B"), "B".getBytes(), -1);
		mds.setValue(Bytes.fromString("C"), "C".getBytes(), -1);
		
		mds.commit();
		
		//校验 Key 的正确性；
		assertEquals("A", mds.getKeyAtIndex(0));
		assertEquals("B", mds.getKeyAtIndex(1));
		assertEquals("C", mds.getKeyAtIndex(2));
	}

	/**
	 * 测试存储的增长；
	 */
	@Test
	public void testStorageIncreasement() {

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		String keyPrefix = "";
		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);
		cryptoConfig.setAutoVerifyHash(true);

		MemoryKVStorage storage = new MemoryKVStorage();

		MerkleDataSet mds = new MerkleDataSet(cryptoConfig, keyPrefix, storage, storage);
		Dataset<String, byte[]> ds = DatasetHelper.map(mds);
		ds.setValue("A", "A".getBytes(), -1);
		ds.setValue("B", "B".getBytes(), -1);
		ds.setValue("C", "C".getBytes(), -1);

		mds.commit();

		byte[] va = ds.getValue("A");
		assertNotNull(va);
		assertEquals("A", new String(va));
		
		byte[] vc = ds.getValue("C");
		DataEntry<String, byte[]> ventry = ds.getDataEntry("C");
		assertNotNull(vc);
		assertNotNull(ventry);
		assertEquals("C", new String(vc));
		assertEquals("C", ventry.getKey());

		HashDigest root1 = mds.getRootHash();
	

		// 1个KV项的存储KEY的数量= 1 + 1(保存SN) + Merkle节点数量;
		// 所以：3 项;
		// so the expected item count in storage is 10;
		int expStorageCount = 10;
		assertEquals(expStorageCount, storage.getStorageCount());

		ds.setValue("B", "B".getBytes(), 0);
		ds.setValue("C", "C".getBytes(), 0);
		mds.commit();
		HashDigest root2 = mds.getRootHash();
		assertNotEquals(root1, root2);

		// Version changed only;仅仅增加 merkle 节点，此时 Merkle 树只有 1 层路径节点，因此只更新2个数据节点和 1
		// 个路径节点；(注：版本值是在同一个 key 下按序列保存的)；
		expStorageCount = expStorageCount + 3;
		assertEquals(expStorageCount, storage.getStorageCount());

		ds.setValue("D", "DValue".getBytes(), -1);
		mds.commit();
		HashDigest root3 = mds.getRootHash();
		assertNotEquals(root2, root3);
		assertNotEquals(root1, root3);

		// New key added, include 1 versioning kv, 1 sn key, 2 merkle nodes;
		// String[] keys = StringUtils.toStringArray(storage.keySet());
		Bytes[] keys = storage.keySet().toArray(new Bytes[0]);
		expStorageCount = expStorageCount + 1 + 1 + 2;
		assertEquals(expStorageCount, storage.getStorageCount());

		// Check rollback function: Add some keys, and then rollback;
		long v = ds.setValue("E", "E-values".getBytes(), -1);
		assertEquals(v, 0);
		String expEValue = new String(ds.getValue("E"));
		assertEquals(expEValue, "E-values");

		v = ds.setValue("F", "F-values".getBytes(), -1);
		assertEquals(v, 0);
		String expFValue = new String(ds.getValue("F"));
		assertEquals(expFValue, "F-values");

		v = ds.setValue("E", "E-values-1".getBytes(), 0);
		assertEquals(v, 1);
		expEValue = new String(ds.getValue("E"));
		assertEquals(expEValue, "E-values-1");

		mds.cancel();

		byte[] bv = ds.getValue("E");
		assertNull(bv);
		bv = ds.getValue("F");
		assertNull(bv);

		v = ds.getVersion("E");
		assertEquals(-1, v);
		v = ds.getVersion("F");
		assertEquals(-1, v);

		// Expect that states has been recover;
		keys = storage.keySet().toArray(new Bytes[0]);
		assertEquals(expStorageCount, storage.getStorageCount());

		// ------
		System.out.println("\r\n------------- storage keys --------------");
		Set<Bytes> storageKeys = storage.getStorageKeySet();
		int i = 0;
		for (Bytes k : storageKeys) {
			System.out.println(String.format("keys[%s]=%s", i, k));
			i++;
		}
	}

	@Test
	public void testDataReload() {
		String keyPrefix = "";
		Random rand = new Random();

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);
		cryptoConfig.setAutoVerifyHash(true);

		MemoryKVStorage storage = new MemoryKVStorage();

		MerkleDataSet mds = new MerkleDataSet(cryptoConfig, keyPrefix, storage, storage);
		Dataset<String, byte[]> ds = DatasetHelper.map(mds);

		// 初始的时候没有任何数据，总是返回 null；
		DataEntry verKVEntry = ds.getDataEntry("NULL_KEY");
		byte[] vbytes = ds.getValue("NULL_KEY");
		assertNull(verKVEntry);
		assertNull(vbytes);

		Map<String, Long> dataVersions = new HashMap<>();
		Map<String, byte[]> dataValues = new HashMap<>();

		Map<HashDigest, Map<String, KeySnapshot>> history = new LinkedHashMap<>();

		HashDigest rootHash;

		// generate base data sample;
		int count = 1024;// + rand.nextInt(1024);
		String key;
		byte[] data = new byte[64];
		long v;
		MerkleProof proof;
		for (int i = 0; i < count; i++) {
			key = "data" + i;
			rand.nextBytes(data);
			v = ds.setValue(key, data, -1);
			dataVersions.put(key, v);
			dataValues.put(key + "_" + v, data);
			assertEquals(v, 0);
		}
		mds.commit();

		// Take snapshot;
		{
			rootHash = mds.getRootHash();
			Map<String, KeySnapshot> snapshot = new HashMap<>();
			for (int i = 0; i < count; i++) {
				key = "data" + i;

				proof = mds.getProof(key);
				assertNotNull(proof);
				assertEquals(rootHash, proof.getRootHash());

				KeySnapshot ks = new KeySnapshot();
				ks.proof = proof;
				ks.maxVersion = ds.getVersion(key);

				snapshot.put(key, ks);
			}
			history.put(rootHash, snapshot);
		}

		// verify;
		{
			MerkleDataSet mdsReload = new MerkleDataSet(rootHash, cryptoConfig, keyPrefix, storage, storage, true);
			// verify every keys;
			Map<String, KeySnapshot> snapshot = history.get(rootHash);
			MerkleProof expProof;
			for (int i = 0; i < count; i++) {
				key = "data" + i;
				proof = mdsReload.getProof(key);
				assertNotNull(proof);
				assertEquals(rootHash, proof.getRootHash());
				expProof = snapshot.get(key).proof;
				assertEquals(expProof.toString(), proof.toString());
			}
		}

		// generate multi-version data sample;
		long expVer;
		long maxVer = 0;
		long minIdx = count;
		long maxIdx = 0;
		for (int t = 0; t < 100; t++) {
			int bound = rand.nextInt(500) + 1;
			for (int i = rand.nextInt(count); i < count; i = i + rand.nextInt(bound) + 1) {
				key = "data" + i;
				rand.nextBytes(data);
				expVer = dataVersions.get(key);
				v = ds.setValue(key, data, expVer);

				assertEquals(v, expVer + 1);

				dataVersions.put(key, v);

				dataValues.put(key + "_" + v, data);

				maxVer = v > maxVer ? v : maxVer;
				minIdx = i < minIdx ? i : minIdx;
				maxIdx = i > maxIdx ? i : maxIdx;
			}
			mds.commit();

			assertNotEquals(rootHash, mds.getRootHash());

			// Take snapshot;
			{
				rootHash = mds.getRootHash();
				Map<String, KeySnapshot> snapshot = new HashMap<>();
				for (int i = 0; i < count; i++) {
					key = "data" + i;

					proof = mds.getProof(key);
					assertNotNull(proof);
					assertEquals(rootHash, proof.getRootHash());

					KeySnapshot ks = new KeySnapshot();
					ks.proof = proof;
					ks.maxVersion = ds.getVersion(key);
					snapshot.put(key, ks);
				}
				history.put(rootHash, snapshot);
			}
		}

		System.out.println(
				String.format("total count=%s; from %s to %s,  max version=%s;", count, minIdx, maxIdx, maxVer));

		{
			for (HashDigest hisRootHash : history.keySet()) {
				Map<String, KeySnapshot> snapshot = history.get(hisRootHash);

				MerkleDataSet mdsReload = new MerkleDataSet(hisRootHash, cryptoConfig, keyPrefix, storage, storage,
						true);
				Dataset<String, byte[]> dsReload = DatasetHelper.map(mdsReload);
				assertEquals(hisRootHash, mdsReload.getRootHash());

				// verify every keys;
				for (int i = 0; i < count; i++) {
					key = "data" + i;
					// 最新版本一致；
					long expLatestVersion = snapshot.get(key).maxVersion;
					long actualLatestVersion = dsReload.getVersion(key);
					assertEquals(expLatestVersion, actualLatestVersion);

					// 数据证明一致；
					proof = mdsReload.getProof(key);
					assertNotNull(proof);

					MerkleProof expProof = snapshot.get(key).proof;
					assertEquals(expProof, proof);

					maxVer = dataVersions.get(key);
					assertTrue(actualLatestVersion > -1);
					assertTrue(actualLatestVersion <= maxVer);
					for (long j = 0; j < actualLatestVersion; j++) {
						String keyver = key + "_" + j;
						byte[] expValue = dataValues.get(keyver);
						byte[] actualValue = dsReload.getValue(key, j);
						assertTrue(BytesUtils.equals(expValue, actualValue));
					}
				}
			}
		}
	}

	@Test
	public void testInsertSameData() {
		String keyPrefix = "";
		Random rand = new Random();

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setHashAlgorithm(ClassicAlgorithm.SHA256);
		cryptoConfig.setAutoVerifyHash(true);

		MemoryKVStorage storage = new MemoryKVStorage();

		MerkleDataSet mds = new MerkleDataSet(cryptoConfig, keyPrefix, storage, storage);
		Dataset<String, byte[]> ds = DatasetHelper.map(mds);

		// 初始的时候没有任何数据，总是返回 null；
		DataEntry verKVEntry = ds.getDataEntry("NULL_KEY");
		byte[] vbytes = ds.getValue("NULL_KEY");
		assertNull(verKVEntry);
		assertNull(vbytes);

		Map<String, Long> dataVersions = new HashMap<>();
		// Map<String, byte[]> dataValues = new HashMap<>();

		Map<HashDigest, Map<String, KeySnapshot>> history = new LinkedHashMap<>();

		HashDigest rootHash;

		// generate base data sample;
		int count = 1024;// + rand.nextInt(1024);
		String key;
		byte[] data = new byte[64];
		rand.nextBytes(data);
		long v;
		MerkleProof proof;
		for (int i = 0; i < count; i++) {
			key = "data" + i;
			v = ds.setValue(key, data, -1);
			dataVersions.put(key, v);
			// dataValues.put(key + "_" + v, data);
			assertEquals(v, 0);
		}
		mds.commit();

		// Take snapshot;
		{
			rootHash = mds.getRootHash();
			Map<String, KeySnapshot> snapshot = new HashMap<>();
			for (int i = 0; i < count; i++) {
				key = "data" + i;

				proof = mds.getProof(key);
				assertNotNull(proof);
				assertEquals(rootHash, proof.getRootHash());

				KeySnapshot ks = new KeySnapshot();
				ks.proof = proof;
				ks.maxVersion = ds.getVersion(key);

				snapshot.put(key, ks);
			}
			history.put(rootHash, snapshot);
		}

		// verify;
		{
			MerkleDataSet mdsReload = new MerkleDataSet(rootHash, cryptoConfig, keyPrefix, storage, storage, true);
			Dataset<String, byte[]> dsReload = DatasetHelper.map(mdsReload);
			// verify every keys;
			Map<String, KeySnapshot> snapshot = history.get(rootHash);
			MerkleProof expProof;
			for (int i = 0; i < count; i++) {
				key = "data" + i;
				proof = mdsReload.getProof(key);
				assertNotNull(proof);
				assertEquals(rootHash, proof.getRootHash());
				expProof = snapshot.get(key).proof;
				assertEquals(expProof.toString(), proof.toString());

				byte[] value = dsReload.getValue(key);
				assertTrue(BytesUtils.equals(data, value));
			}
		}
	}

	private static class KeySnapshot {
		private MerkleProof proof;
		private long maxVersion;

	}
}
