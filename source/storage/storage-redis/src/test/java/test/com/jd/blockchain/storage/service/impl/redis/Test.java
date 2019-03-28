package test.com.jd.blockchain.storage.service.impl.redis;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.impl.redis.RedisConnectionFactory;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

import redis.clients.jedis.Jedis;

public class Test {

	public static void main(String[] args) {
		
		Bytes key = Bytes.fromString("test111");
		try (Jedis jedis = new Jedis("192.168.151.33", 6379)) {
			jedis.connect();
			byte[] kbytes = key.toBytes();// BytesUtils.toBytes(key, "UTF-8");
			byte[] valueBytes = jedis.get(kbytes);
			String value = BytesUtils.toString(valueBytes, "UTF-8");
			System.out.println(String.format("%s=%s", key, value));
		}
		System.out.println("=================================");
		
		System.out.println("================= test expolicy storage =================");
		
		RedisConnectionFactory connFactory = new RedisConnectionFactory();
		try(DbConnection conn = connFactory.connect("redis://192.168.151.33:6379")){
			ExPolicyKVStorage exKVStorage = conn.getStorageService().getExPolicyKVStorage();
			byte[] valueBytes = exKVStorage.get(key);
			String value = BytesUtils.toString(valueBytes, "UTF-8");
			System.out.println(String.format("%s=%s", key, value));
			
			System.out.println(String.format("%s=%s", key, value));
			boolean success = exKVStorage.set(key, BytesUtils.toBytes("New Value by ExPolicyStorage interface..."), ExPolicy.NOT_EXISTING);
			System.out.println("update key when NX " + (success ? "success" : "fail"));
			success = exKVStorage.set(key, BytesUtils.toBytes("New Value by ExPolicyStorage interface..."), ExPolicy.EXISTING);
			System.out.println("update key when XX " + (success ? "success" : "fail"));
			
			valueBytes = exKVStorage.get(key);
			value = BytesUtils.toString(valueBytes, "UTF-8");
			System.out.println(String.format("Retrieve... %s=%s", key, value));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("=================================");
		
		try(DbConnection conn = connFactory.connect("redis://192.168.151.33:6379/0")){
			ExPolicyKVStorage exKVStorage = conn.getStorageService().getExPolicyKVStorage();
			byte[] valueBytes = exKVStorage.get(key);
			String value = BytesUtils.toString(valueBytes, "UTF-8");
			System.out.println(String.format("%s=%s", key, value));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("=================================");
		
		System.out.println("================= test versioning storage =================");
		Bytes addr =Bytes.fromString("User001");
		try(DbConnection conn = connFactory.connect("redis://192.168.151.33:6379/0")){
			VersioningKVStorage verStorage = conn.getStorageService().getVersioningKVStorage();
			long version = verStorage.getVersion(addr);
			System.out.println(String.format("Version of key[%s]=%s", addr, version));
			byte[] v1 = BytesUtils.toBytes("value-" + version);
			version = verStorage.set(addr, v1, version);
			System.out.println(String.format("Update[%s] to V1, version=%s", addr, version));
			byte[] v2 = BytesUtils.toBytes("value-" + version);
			version = verStorage.set(addr, v2, version);
			System.out.println(String.format("Update[%s] to V2, version=%s", addr, version));
			byte[] v3 = BytesUtils.toBytes("value-" + version);
			version = verStorage.set(addr, v3, version);
			System.out.println(String.format("Update[%s] to V3, version=%s", addr, version));
			
			version = verStorage.getVersion(addr);
			System.out.println(String.format("Now the latest version of key[%s]=%s", addr, version));
			for (int i = 0; i <=version; i++) {
				String value = BytesUtils.toString(verStorage.get(addr, i));
				System.out.println(String.format("The version[%s] value of key[%s] is :%s", i, addr, value));
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("=================================");
	}

}
