package test.perf.com.jd.blockchain.ledger;

import java.io.IOException;
import java.util.Random;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.MerkleDataSet;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.impl.redis.RedisConnectionFactory;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import com.jd.blockchain.utils.Bytes;

public class MerkleDatasetPerformanceTester {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName() };

	private static final String MKL_KEY_PREFIX = "";

	public static void main(String[] args) {
		testPerformanceInMemory();

		// testPerformanceWithRedis();
	}

	public static void testPerformanceInMemory() {
		testInsertPerformanceInMermory(1000, 1);
		testInsertPerformanceInMermory(10000, 1);
		testInsertPerformanceInMermory(20000, 1);
		testInsertPerformanceInMermory(40000, 1);
		// testInsertPerformance(80000, 1);
		// testInsertPerformance(100000, 1);
		// testInsertPerformance(1000000, 1);

		System.out.println("============================================================");

		testInsertPerformanceInMermory(500, 4);
		testInsertPerformanceInMermory(1000, 4);
		testInsertPerformanceInMermory(10000, 4);
		testInsertPerformanceInMermory(20000, 4);
		// testInsertPerformance(40000, 4);
		// testInsertPerformance(80000, 4);
		System.out.println("============================================================");
		testInsertPerformanceInMermory(100, 10);
		testInsertPerformanceInMermory(1000, 10);
		testInsertPerformanceInMermory(2000, 10);
		testInsertPerformanceInMermory(4000, 10);
		testInsertPerformanceInMermory(2000, 20);
		testInsertPerformanceInMermory(200, 20);
		testInsertPerformanceInMermory(4000, 20);
		testInsertPerformanceInMermory(400, 20);
		// testInsertPerformance(8000, 10);
		System.out.println("============================================================");
		testInsertPerformanceInMermory(100, 100);
		testInsertPerformanceInMermory(100, 1000);
		testInsertPerformanceInMermory(100, 10000);
		System.out.println("============================================================");
		testInsertPerformanceInMermory(20, 4);
		testInsertPerformanceInMermory(20, 8);
		testInsertPerformanceInMermory(20, 10);
		testInsertPerformanceInMermory(20, 20);
		testInsertPerformanceInMermory(20, 40);
		testInsertPerformanceInMermory(20, 100);
		testInsertPerformanceInMermory(20, 400);
		testInsertPerformanceInMermory(20, 1000);
	}

	public static void testPerformanceWithRedis() {
		String redisUri = "redis://127.0.0.1:6379/0";
		RedisConnectionFactory connFact = new RedisConnectionFactory();
		try (DbConnection conn = connFact.connect(redisUri)) {
			ExPolicyKVStorage exStorage = conn.getStorageService().getExPolicyKVStorage();
			VersioningKVStorage verStorage = conn.getStorageService().getVersioningKVStorage();

			testInsertPerformance(1000, 1, exStorage, verStorage);
			testInsertPerformance(10000, 1, exStorage, verStorage);
			testInsertPerformance(20000, 1, exStorage, verStorage);
			testInsertPerformance(40000, 1, exStorage, verStorage);
			// testInsertPerformance(80000, 1,exStorage, verStorage);
			// testInsertPerformance(100000, 1,exStorage, verStorage);
			// testInsertPerformance(1000000, 1,exStorage, verStorage);

			System.out.println("============================================================");

			testInsertPerformance(500, 4, exStorage, verStorage);
			testInsertPerformance(1000, 4, exStorage, verStorage);
			testInsertPerformance(10000, 4, exStorage, verStorage);
			testInsertPerformance(20000, 4, exStorage, verStorage);
			// testInsertPerformance(40000, 4,exStorage, verStorage);
			// testInsertPerformance(80000, 4,exStorage, verStorage);
			System.out.println("============================================================");
			testInsertPerformance(100, 10, exStorage, verStorage);
			testInsertPerformance(1000, 10, exStorage, verStorage);
			testInsertPerformance(2000, 10, exStorage, verStorage);
			testInsertPerformance(4000, 10, exStorage, verStorage);
			testInsertPerformance(2000, 20, exStorage, verStorage);
			testInsertPerformance(200, 20, exStorage, verStorage);
			testInsertPerformance(4000, 20, exStorage, verStorage);
			testInsertPerformance(400, 20, exStorage, verStorage);
			// testInsertPerformance(8000, 10,exStorage, verStorage);
			System.out.println("============================================================");
			testInsertPerformance(100, 100, exStorage, verStorage);
			testInsertPerformance(100, 1000, exStorage, verStorage);
			testInsertPerformance(100, 10000, exStorage, verStorage);
			System.out.println("============================================================");
			testInsertPerformance(20, 4, exStorage, verStorage);
			testInsertPerformance(20, 8, exStorage, verStorage);
			testInsertPerformance(20, 10, exStorage, verStorage);
			testInsertPerformance(20, 20, exStorage, verStorage);
			testInsertPerformance(20, 40, exStorage, verStorage);
			testInsertPerformance(20, 100, exStorage, verStorage);
			testInsertPerformance(20, 400, exStorage, verStorage);
			testInsertPerformance(20, 1000, exStorage, verStorage);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void testInsertPerformanceInMermory(int round, int batchCount) {
		// Map<String, Object> storageMap1 = new HashMap<>();
		// ExPolicyKVStorage exStorage = new ExistancePolicyKVStorageMap(storageMap1);
		// Map<String, Object> storageMap2 = new HashMap<>();
		// VersioningKVStorage verStorage = new VersioningKVStorageMap(storageMap2);
		MemoryKVStorage memoryKVStorage = new MemoryKVStorage();
		testInsertPerformance(round, batchCount, memoryKVStorage, memoryKVStorage);
	}

	public static void testInsertPerformance(int round, int batchCount, ExPolicyKVStorage exStorage,
			VersioningKVStorage verStorage) {
		Random rand = new Random();

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig cryptoConfig = new CryptoConfig();
		cryptoConfig.setSupportedProviders(supportedProviders);
		cryptoConfig.setHashAlgorithm(Crypto.getAlgorithm("SHA256"));
		cryptoConfig.setAutoVerifyHash(true);

		// generate base data sample;
		String key;
		byte[] data = new byte[64];
		rand.nextBytes(data);

		long startTs = System.currentTimeMillis();
		HashDigest rootHash;
		int randomId = rand.nextInt(1000);
		MerkleDataSet mds = new MerkleDataSet(cryptoConfig, MKL_KEY_PREFIX, exStorage, verStorage);
		for (int i = 0; i < round; i++) {
			for (int j = 0; j < batchCount; j++) {
				key = "data_" + startTs + "_" + randomId + "_" + (i * batchCount + j);
				long v = mds.getVersion(Bytes.fromString(key));
				mds.setValue(Bytes.fromString(key), data, v);
			}
			mds.commit();
			rootHash = mds.getRootHash();
			mds = new MerkleDataSet(rootHash, cryptoConfig, Bytes.fromString(MKL_KEY_PREFIX), exStorage, verStorage, false);
		}

		long elapsedTs = System.currentTimeMillis() - startTs;

		System.out.println(String.format(
				"Inserted %s keys[round=%s, batchCount=%s], takes %s ms! -- TPS=%.2f, KPS=%.2f;", round * batchCount,
				round, batchCount, elapsedTs, round * 1000.0D / elapsedTs, round * batchCount * 1000.0D / elapsedTs));

		// int exKeys = storageMap1.size();
		// int verKeys = storageMap2.size();
		// System.out.println(String.format(
		// "Inserted %s keys[round=%s, batchCount=%s], takes %s ms! -- TPS=%.2f,
		// KPS=%.2f, "
		// + "Total Storage Keys=%s [ex.count=%s, ver.count=%s];",
		// round * batchCount, round, batchCount, elapsedTs, round * 1000.0D /
		// elapsedTs,
		// round * batchCount * 1000.0D / elapsedTs, exKeys + verKeys, exKeys,
		// verKeys));
	}
}
