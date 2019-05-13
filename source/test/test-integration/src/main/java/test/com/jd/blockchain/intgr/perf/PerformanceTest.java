package test.com.jd.blockchain.intgr.perf;

import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.impl.redis.RedisConnectionFactory;
import com.jd.blockchain.utils.ArgumentSet;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.security.ShaUtils;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class PerformanceTest {

	public static void main(String[] args) {
		try {
			boolean testLedger = !ArgumentSet.hasOption(args, "-test=storage");
			if (testLedger) {
//				LedgerPerformanceTest.test(new String[]{"-silent", "-usertest", "-o"});
				LedgerPerformanceTest.test(new String[]{"-silent", "-o", "-rocksdb"});
				return;
			}

			// GlobalPerformanceTest.test(args);

//			testRedisWriting(args);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			ForkJoinPool.commonPool().shutdown();
		}
	}

	private static void testRedisWriting(String[] args) {
		ConsoleUtils.info("-------------------- start redis test --------------------");
		RedisConnectionFactory redisConnFactory = new RedisConnectionFactory();
		DbConnection conn = redisConnFactory.connect("redis://127.0.0.1:6079");
		KVStorageService storage = conn.getStorageService();
		VersioningKVStorage vs = storage.getVersioningKVStorage();

		byte[] data = BytesUtils.toBytes("TestDATA");

		int count = 100000;
		if (args.length > 0) {
			count = Integer.parseInt(args[0]);
		}
		if (args.length > 0) {
			for (String arg : args) {
				if (arg.startsWith("-threshold=")) {
					int threshold = Integer.parseInt(arg.substring("-threshold=".length()));
					if (threshold > 0) {
						RedisWriteTestTask.THRESHOLD = threshold;
					}
				}
			}
		}

		Random rand = new Random();
		byte[] nameBytes = new byte[16];
		rand.nextBytes(nameBytes);
		String name = Base58Utils.encode(ShaUtils.hash_256(nameBytes));
		rand.nextBytes(nameBytes);
		name = name + "/" + Base58Utils.encode(ShaUtils.hash_256(nameBytes));
		rand.nextBytes(nameBytes);
		name = name + "/" + Base58Utils.encode(ShaUtils.hash_256(nameBytes));

		long startTS = System.currentTimeMillis();

		RedisWriteTestTask task = new RedisWriteTestTask(name, 0, count, data, vs);
		ForkJoinPool.commonPool().invoke(task);

		long elapsedTS = System.currentTimeMillis() - startTS;

		double globalTPS = count * 1000.00D / elapsedTS;

		ConsoleUtils.info("\r\n**********************************************");
		ConsoleUtils.info("写入KEY总数：%s; 总体TPS：%.2f;", count, globalTPS);
		ConsoleUtils.info("**********************************************\r\n");

		try {
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class RedisWriteTestTask extends RecursiveAction {

		private static final long serialVersionUID = -8415596564326385834L;

		public static int THRESHOLD = 800;

		private int offset;

		private int count;

		private byte[] data;

		private String name;

		private VersioningKVStorage vs;

		public RedisWriteTestTask(String name, int offset, int count, byte[] data, VersioningKVStorage vs) {
			this.name = name;
			this.offset = offset;
			this.count = count;
			this.data = data;
			this.vs = vs;
		}

		@Override
		protected void compute() {
			if (count > THRESHOLD) {
				int count1 = count / 2;
				RedisWriteTestTask task1 = new RedisWriteTestTask(name, offset, count1, data, vs);
				RedisWriteTestTask task2 = new RedisWriteTestTask(name, offset + count1, count - count1, data, vs);
				ForkJoinTask.invokeAll(task1, task2);
			} else {
				for (int i = 0; i < count; i++) {
					String key = String.format("[%s][%s]-TEST-KEY-%s", name, offset, i);
					vs.set(Bytes.fromString(key), data, -1);
				}
			}
		}

	}

}
