//package test.com.jd.blockchain.storage.service.impl.composite;
//import static org.junit.Assert.*;
//
//import com.jd.blockchain.storage.service.DbConnection;
//import com.jd.blockchain.storage.service.ExPolicyKVStorage;
//import com.jd.blockchain.storage.service.VersioningKVStorage;
//import com.jd.blockchain.storage.service.impl.composite.CompositeConnectionFactory;
//import com.jd.blockchain.utils.Bytes;
//import com.jd.blockchain.utils.io.BytesUtils;
//import com.jd.blockchain.utils.io.FileUtils;
//
//import org.junit.Test;
//
//import java.io.File;
//import java.util.regex.Pattern;
//
//public class CompositeConnectionFactoryTest {
//
//	public static final Pattern URI_PATTER_REDIS = Pattern
//			.compile("^\\w+\\://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\:\\d+(/\\d*(/.*)*)?$");
//
//	@Test
//	public void testRedisConnectionString() {
//		String connStr = "redis://192.168.86.130:6379/";
//		boolean match = URI_PATTER_REDIS.matcher(connStr).matches();
//		assertTrue(match);
//
//		connStr = "redis://192.168.86.131:6379/";
//		match = URI_PATTER_REDIS.matcher(connStr).matches();
//		assertTrue(match);
//
//		connStr = "redis://192.168.86.132:6379/";
//		match = URI_PATTER_REDIS.matcher(connStr).matches();
//		assertTrue(match);
//
//		connStr = "redis://192.168.86.133:6379/";
//		match = URI_PATTER_REDIS.matcher(connStr).matches();
//		assertTrue(match);
//	}
//
//	@Test
//	public void testRocksDbConnect() {
//		String dbUri = initEmptyDB("rocksdb_storage_test");
//		long expectedVersion;
//		try (CompositeConnectionFactory dbConnFactory = new CompositeConnectionFactory()) {
////		try (CompositeConnectionFactory dbConnFactory = CompositeConnectionFactory.getInstance()) {
//			DbConnection conn = dbConnFactory.connect(dbUri);
//			VersioningKVStorage verStorage = conn.getStorageService().getVersioningKVStorage();
//			ExPolicyKVStorage exStorage = conn.getStorageService().getExPolicyKVStorage();
//
//			expectedVersion = test(verStorage);
//
//			test(exStorage);
//		}
//	}
//	private String initEmptyDB(String name) {
//		String currDir = FileUtils.getCurrentDir();
//		String dbDir = new File(currDir, name + ".db").getAbsolutePath();
//		FileUtils.deleteFile(dbDir);
//		String dbURI = "rocksdb://" + dbDir;
//		return dbURI;
//	}
//	private long test(VersioningKVStorage verStorage) {
//		String key = "k1";
//		long v = verStorage.getVersion(Bytes.fromString(key));
//		assertEquals(-1, v);
//		byte[] data = verStorage.get(Bytes.fromString(key), -1);
//		assertNull(data);
//		data = verStorage.get(Bytes.fromString(key), 0);
//		assertNull(data);
//		data = verStorage.get(Bytes.fromString(key), 1);
//		assertNull(data);
//
//		data = BytesUtils.toBytes("data");
//		v = verStorage.set(Bytes.fromString(key), data, -1);
//		assertEquals(0, v);
//		v = verStorage.set(Bytes.fromString(key), data, -1);
//		assertEquals(-1, v);
//		v = verStorage.set(Bytes.fromString(key), data, 0);
//		assertEquals(1, v);
//		return v;
//	}
//
//	private void test(ExPolicyKVStorage exStorage) {
//		String key = "kex";
//		assertFalse(exStorage.exist(Bytes.fromString(key)));
//
//		byte[] data = exStorage.get(Bytes.fromString(key));
//		assertNull(data);
//
//		data = BytesUtils.toBytes("data");
//		assertFalse(exStorage.set(Bytes.fromString(key), data, ExPolicyKVStorage.ExPolicy.EXISTING));
//
//		assertTrue(exStorage.set(Bytes.fromString(key), data, ExPolicyKVStorage.ExPolicy.NOT_EXISTING));
//		assertTrue(exStorage.exist(Bytes.fromString(key)));
//
//		assertFalse(exStorage.set(Bytes.fromString(key), data, ExPolicyKVStorage.ExPolicy.NOT_EXISTING));
//
//		assertTrue(exStorage.set(Bytes.fromString(key), data, ExPolicyKVStorage.ExPolicy.EXISTING));
//		assertTrue(exStorage.set(Bytes.fromString(key), data, ExPolicyKVStorage.ExPolicy.EXISTING));
//		assertFalse(exStorage.set(Bytes.fromString(key), data, ExPolicyKVStorage.ExPolicy.NOT_EXISTING));
//
//		assertTrue(exStorage.exist(Bytes.fromString(key)));
//
//		byte[] reloadData = exStorage.get(Bytes.fromString(key));
//		assertTrue(BytesUtils.equals(data, reloadData));
//	}
//
//
//}
