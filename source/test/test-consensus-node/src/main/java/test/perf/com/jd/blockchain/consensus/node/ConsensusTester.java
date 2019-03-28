//package test.perf.com.jd.blockchain.consensus.node;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.CyclicBarrier;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import com.jd.blockchain.consensus.*;
//import com.jd.blockchain.consensus.bftsmart.*;
//import com.jd.blockchain.consensus.bftsmart.client.BftsmartClientSettings;
//import com.jd.blockchain.consensus.bftsmart.service.BftsmartServerSettingConfig;
//import com.jd.blockchain.consensus.service.MessageHandle;
//import com.jd.blockchain.consensus.service.ServerSettings;
//import com.jd.blockchain.consensus.service.StateMachineReplicate;
//import com.jd.blockchain.crypto.asymmetric.PubKey;
//import com.jd.blockchain.ledger.BlockchainKeyGenerator;
//import com.jd.blockchain.peer.consensus.ConsensusMessageDispatcher;
//import com.jd.blockchain.peer.consensus.LedgerStateManager;
//import com.jd.blockchain.tools.keygen.KeyGenCommand;
//import my.utils.PropertiesUtils;
//import my.utils.Property;
//import my.utils.net.NetworkAddress;
//import org.springframework.core.io.ClassPathResource;
//
//import bftsmart.reconfiguration.util.HostsConfig;
//import my.utils.ConsoleUtils;
//import my.utils.concurrent.AsyncFuture;
//import my.utils.concurrent.ThreadInvoker;
//import my.utils.concurrent.ThreadInvoker.AsyncCallback;
//import my.utils.concurrent.ThreadUtils;
//import my.utils.io.BytesUtils;
//import my.utils.io.FileUtils;
//
//public class ConsensusTester {
//
//	public static final String PASSWORD = "abc";
//
//	public static final String[] PUB_KEYS = { "endPsK36imXrY66pru6ttZ8dZ3TynWekmdqoM1K7ZRRoRBBiYVzM",
//			"endPsK36jQE1uYpdVRSnwQXVYhgAMWTaMJiAqii7URiULoBDLUUN",
//			"endPsK36fc7FSecKAJCJdFhTejbPHMLaGcihJVQCv95czCq4tW5n",
//			"endPsK36m1grx8mkTMgh8XQHiiaNzajdC5hkuqP6pAuLmMbYkzd4" };
//
//	public static final String[] PRIV_KEYS = {
//			"177gjsuHdbf3PU68Sm1ZU2aMcyB7sLWj94xwBUoUKvTgHq7qGUfg6ynDB62hocYYXSRXD4X",
//			"177gjwQwTdXthkutDKVgKwiq6wWfLWYuxhji1U2N1C5MzqLRWCLZXo3i2g4vpfcEAQUPG8H",
//			"177gjvLHUjxvAWsqVcGgV8eHgVNBvJZYDfpP9FLjTouR1gEJNiamYu1qjTNDh18XWyLg8or",
//			"177gk2VtYeGbK5TS2xWhbSZA4BsT9Xj5Fb8hqCzxzgbojVVcqaDSFFrFPsLbZBx7rszyCNy" };
//
//	public static volatile boolean debug = false;
//
//	public static MessageHandle consensusMessageHandler = new ConsensusMessageDispatcher();
//	public static StateMachineReplicate consensusStateManager = new LedgerStateManager();
//
//	public static void main(String[] args) {
//		// DataContractRegistry.register(ActionResponse.class);
//		try {
//			HostsConfig hosts = new HostsConfig();
//			hosts.add(0, "127.0.0.1", 10000);
//			hosts.add(1, "127.0.0.1", 10010);
//			hosts.add(2, "127.0.0.1", 10020);
//			hosts.add(3, "127.0.0.1", 10030);
//
//			TestServceHandle handle0 = new TestServceHandle(0);
//			AsyncCallback<BftsmartConsensusServlet> call0 = startReplica(0, hosts, handle0);
//			TestServceHandle handle1 = new TestServceHandle(1);
//			AsyncCallback<BftsmartConsensusServlet> call1 = startReplica(1, hosts, handle1);
//			TestServceHandle handle2 = new TestServceHandle(2);
//			AsyncCallback<BftsmartConsensusServlet> call2 = startReplica(2, hosts, handle2);
//			TestServceHandle handle3 = new TestServceHandle(3);
//			AsyncCallback<BftsmartConsensusServlet> call3 = startReplica(3, hosts, handle3);
//
//			BftsmartConsensusServlet replica0 = call0.waitReturn();
//			BftsmartConsensusServlet replica1 = call1.waitReturn();
//			BftsmartConsensusServlet replica2 = call2.waitReturn();
//			BftsmartConsensusServlet replica3 = call3.waitReturn();
//
//			ConsoleUtils.info("All replicas have started!");
//
//			Topology tp = replica0.getTopology().copyOf();
//
//			{
//				// 单步测试；
//				// TestServce clientService = createClientService(0, hosts, tp);
//				// debug = true;
//				// ConsoleUtils.info("First message...");
//				// String resp = clientService.hello("AAAA");
//				//// String resp = clientService.hello("[SLEEP] AAAA");
//				// ConsoleUtils.info("response:[%s]", resp);
//
//				// ConsoleUtils.info("Second message...");
//				// resp = clientService.hello("[SLEEP] BBBB");
//				// ConsoleUtils.info("response:[%s]", resp);
//			}
//			{
//				// 异步调用；
//				// TestServce clientService = createClientService(0, hosts, tp);
//				// clientService = AsyncInvoker.asynchorize(TestServce.class, clientService);
//				// debug = true;
//				// long startTs = System.currentTimeMillis();
//				// ConsoleUtils.info("[%s] Async send first message...", startTs);
//				// AsyncResult<String> ayncResult =
//				// AsyncInvoker.call(clientService.hello("AAAA"));
//				// ayncResult.addListener(new my.utils.concurrent.AsyncCallback<String>() {
//				// @Override
//				// public void complete(String replyMessage, Throwable error) {
//				// if (error != null) {
//				// long endTs = System.currentTimeMillis();
//				// ConsoleUtils.error("[%s][spanTS=%s] Async response error!!! --%s", endTs,
//				// endTs - startTs,
//				// error.getMessage());
//				// error.printStackTrace();
//				// return;
//				// }
//				//
//				// ConsoleUtils.info("Async response:[%s]", replyMessage);
//				// }
//				// });
//
//			}
//
//			{
//				// 单客户端并发消息发送测试；
//				// TestServce clientService = createClientService(0, hosts, tp);
//				// testConcurrentMessageSending(clientService, 100);
//			}
//
//			{
//				// 多客户端并发消息发送测试；
//				int msgCount = 60000;
//
//				AtomicInteger receiveCount = new AtomicInteger(0);
//
//				// my.utils.concurrent.AsyncCallback<String> callback = new
//				// my.utils.concurrent.AsyncCallback<String>() {
//				// @Override
//				// public void complete(String replyMessage, Throwable error) {
//				// if (error != null) {
//				// long endTs = System.currentTimeMillis();
//				// ConsoleUtils.error("[%s][spanTS=%s] Async response error!!! --%s", endTs,
//				// endTs - startTs,
//				// error.getMessage());
//				// error.printStackTrace();
//				// return;
//				// }
//				// int c = receiveCount.incrementAndGet();
//				// if (c >= msgCount) {
//				// long endTs = System.currentTimeMillis();
//				// ConsoleUtils.info("\r\n============== All message has been received response!
//				// [耗时：%s millis][TPS=%.2f] =====\r\n", (endTs - startTs), msgCount * 1000.0D
//				// /(endTs - startTs));
//				// }
//				// }
//				// };
//
//				TestServce[] sessions = createSessions(10, hosts, tp);
//
//				long startTs = System.currentTimeMillis();
//				ConsoleUtils.info("[%s] Async send first message...", startTs);
//				AtomicInteger arrivedCounter = new AtomicInteger(0);
//				my.utils.concurrent.AsyncHandle<String> allArrivedCallback = new my.utils.concurrent.AsyncHandle<String>() {
//
//					@Override
//					public void complete(String returnValue, Throwable error) {
//						int c = arrivedCounter.incrementAndGet();
//						if (c >= 3) {
//							long endTs = System.currentTimeMillis();
//							ConsoleUtils.info(
//									"\r\n============== All message has been received by most nodes! [耗时：%s millis][TPS=%.2f] =====\r\n",
//									(endTs - startTs), msgCount * 1000.0D / (endTs - startTs));
//
//						}
//						if (c >= 4) {
//							ConsoleUtils.info("Verify consistence of all replicas after all message received!");
//							verifyConsistence(msgCount, handle0, handle1, handle2, handle3);
//						}
//					}
//				};
//
//				handle0.setThreshold(msgCount, allArrivedCallback);
//				handle1.setThreshold(msgCount, allArrivedCallback);
//				handle2.setThreshold(msgCount, allArrivedCallback);
//				handle3.setThreshold(msgCount, allArrivedCallback);
//
//				testConcurrentClient(sessions, msgCount, null);
//				ConsoleUtils.info("Complete client sending.");
//
//				boolean consistent;
//				int tryTimes = 0;
//				do {
//					ConsoleUtils.info("Verify consistence of all replicas...[%s]", tryTimes);
//					consistent = verifyConsistence(msgCount, handle0, handle1, handle2, handle3);
//					if (consistent) {
//						break;
//					}
//					tryTimes++;
//					ThreadUtils.sleepUninterrupted(1000);
//				} while (tryTimes < 5);
//			}
//			ConsoleUtils.info("----- Test finish! -----");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static boolean verifyConsistence(int expectedMessageCount, TestServceHandle... handles) {
//		try {
//			for (int i = 0; i < handles.length; i++) {
//				assertEquals(expectedMessageCount, handles[i].getMessageCount(), "replica[" + i + "] message count");
//			}
//
//			for (int i = 0; i < expectedMessageCount; i++) {
//				String msg0 = handles[0].getMessage(i);
//				assertNotNULL(msg0, "replica[" + i + "]");
//				for (int j = 1; j < handles.length; j++) {
//					String msg1 = handles[j].getMessage(i);
//					assertEquals(msg0, msg1, "message comparison between replica[0] and replica[" + j + "]");
//				}
//			}
//
//			ConsoleUtils.info("========== states of all replicas are consistence! =======");
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public static void assertNotNULL(Object actual, String note) {
//		if (actual == null) {
//			throw new IllegalStateException(String.format("%s (expected NULL, but actual[%s])", note, actual));
//		}
//	}
//
//	public static void assertEquals(Object expected, Object actual, String note) {
//		if (expected == null && actual == null) {
//			return;
//		}
//		if (expected == null) {
//			throw new IllegalStateException(String.format("%s (expected[%s], but actual[%s])", note, expected, actual));
//		}
//		if (!expected.equals(actual)) {
//			throw new IllegalStateException(String.format("%s (expected[%s], but actual[%s])", note, expected, actual));
//		}
//	}
//
//	private static TestServce[] createSessions(int sessionCount, HostsConfig hosts, Topology tp) {
//		TestServce[] clients = new TestServce[sessionCount];
//		for (int i = 0; i < clients.length; i++) {
//			PubKey clientPubKey= BlockchainKeyGenerator.getInstance().generate().getPubKey();
//			TestServce clientService = createClientService(i, clientPubKey, hosts, tp.copyOf());
//			clients[i] = AsyncInvoker.asynchorize(TestServce.class, clientService);
//		}
//		return clients;
//	}
//
//	public static void testConcurrentClient(TestServce[] clients, int count,
//			my.utils.concurrent.AsyncHandle<String> callback) {
//		// TestServce[] clients = createSessions(id, hosts, tp);
//
//		ConsoleUtils.info("All clients has conected to replicas...");
//
//		MessageSendingTask[] tasks = new MessageSendingTask[count];
//
//		int sessionCount = clients.length;
//		CyclicBarrier barrier = new CyclicBarrier(sessionCount);
//		AtomicInteger counter = new AtomicInteger(0);
//		for (int i = 0; i < sessionCount; i++) {
//			tasks[i] = new MessageSendingTask(count, counter, clients[i], barrier, null, callback);
//			Thread thrd = new Thread(tasks[i]);
//
//			thrd.start();
//		}
//
//	}
//
//	public static void testConcurrentMessageSending(TestServce clientService, int count) {
//
//		CyclicBarrier barrier = new CyclicBarrier(count);
//		for (int i = 0; i < count; i++) {
//
//			Thread thrd = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					try {
//						barrier.await();
//						clientService.hello("AAAA-" + count);
//					} catch (Exception e) {
//						ConsoleUtils.error("Error occurred on sending message! --%s", e.getMessage());
//						e.printStackTrace();
//					}
//				}
//			});
//
//			thrd.start();
//		}
//	}
//
//	public static TestServce createClientService(int id, PubKey clientPubKey, HostsConfig hosts, Topology tp) {
//		Properties systemConfig = loadConsensusSetting();
//
//		BftsmartTopology topology = (BftsmartTopology)tp.copyOf();
//		Property[] bftsmartSystemConfigs = PropertiesUtils.getOrderedValues(systemConfig);
//
//		BftsmartNodeSettings[] nodesSettings = new BftsmartNodeSettings[hosts.getNum()];
//
//		for (int i = 0; i < hosts.getNum(); i++) {
//			PubKey pubKey = KeyGenCommand.decodePubKey(PUB_KEYS[i]);
//			BftsmartNodeConfig nodeConfig = new BftsmartNodeConfig(pubKey, i,
//					new NetworkAddress(hosts.getHost(i), hosts.getPort(i), false));
//			nodesSettings[i] = nodeConfig;
//		}
//
//		ConsensusSettings consensusSettings = new BftsmartConsensusConfig(nodesSettings, null,
//				bftsmartSystemConfigs);
//
//		BftsmartClientSettings clientSettings = new BftsmartClientSettings(id, clientPubKey, consensusSettings, topology);
//		BftsmartConsensusProxyFactory serviceFactory = BftsmartConsensusProxyFactory.connect(clientSettings);
//
//		// BftsmartConsensusSetting consensusSetting = new BftsmartConsensusSetting(id,
//		// systemConfig, hosts);
//		// BftsmartConsensusServiceFactory serviceFactory =
//		// BftsmartConsensusServiceFactory.connect(0, consensusSetting,
//		// (BftsmartTopology) tp);
//
//		return serviceFactory.getService(TestServce.class);
//	}
//
//	public static AsyncCallback<BftsmartConsensusServlet> startReplica(int id, HostsConfig hosts,
//			TestServce serviceHandle) {
//		Properties systemConfig = loadConsensusSetting();
//
//		Property[] bftsmartSystemConfigs = PropertiesUtils.getOrderedValues(systemConfig);
//
//		BftsmartNodeSettings currNodeSettings = null;
//
//		for (int i = 0; i < hosts.getNum(); i++) {
//			PubKey pubKey = KeyGenCommand.decodePubKey(PUB_KEYS[i]);
//			BftsmartNodeConfig nodeConfig = new BftsmartNodeConfig(pubKey, i,
//					new NetworkAddress(hosts.getHost(i), hosts.getPort(i), false));
//
//			if (i == id) {
//				currNodeSettings = nodeConfig;
//			}
//		}
//
//		ServerSettings serverSettings = new BftsmartServerSettingConfig();
//		((BftsmartServerSettingConfig) serverSettings).setRealmName(null);
//		((BftsmartServerSettingConfig) serverSettings).setReplicaSettings(currNodeSettings);
//
//		// BftsmartConsensusServlet servlet = new BftsmartConsensusServlet(id,
//		// systemConfig, hosts);
//		BftsmartConsensusServlet servlet = new BftsmartConsensusServlet(serverSettings, consensusMessageHandler,
//				consensusStateManager);
////		servlet.addServiceHandler(TestServce.class, serviceHandle);
//
//		ThreadInvoker<BftsmartConsensusServlet> invoker = new ThreadInvoker<BftsmartConsensusServlet>() {
//			@Override
//			protected BftsmartConsensusServlet invoke() throws Exception {
//				try {
//					servlet.start();
//					ConsoleUtils.info("Replica[%s] start success.", id);
//					return servlet;
//				} catch (Exception e) {
//					ConsoleUtils.info("Replica[%s] start failed!", id);
//					e.printStackTrace();
//					throw e;
//				}
//			}
//		};
//
//		return invoker.start();
//	}
//
//	public static Properties loadConsensusSetting() {
//		ClassPathResource ledgerInitSettingResource = new ClassPathResource("system.config");
//		try (InputStream in = ledgerInitSettingResource.getInputStream()) {
//			return FileUtils.readProperties(in);
//		} catch (IOException e) {
//			throw new IllegalStateException(e.getMessage(), e);
//		}
//	}
//
//	public static class TextMessageConverter implements BinaryMessageConverter {
//
//		@Override
//		public byte[] encode(Object message) {
//			return BytesUtils.toBytes((String) message);
//		}
//
//		@Override
//		public Object decode(byte[] messageBytes) {
//			return BytesUtils.toString(messageBytes);
//		}
//
//	}
//
//	public static class DefaultGroupIndexer implements GroupIndexer {
//
//		private byte[] groupId = { (byte) 0 };
//
//		@Override
//		public byte[] getGroupId(Object[] messageObjects) {
//			return groupId;
//		}
//
//	}
//
//	// ===================================================
//
//	public static interface TestServce {
//		@OrderedAction(groupIndexer = DefaultGroupIndexer.class, responseConverter = TextMessageConverter.class)
//		String hello(@ActionMessage(converter = TextMessageConverter.class) String msg);
//	}
//
//	private static class TestServceHandle implements TestServce, StateHandle {
//
//		private int id;
//
//		private List<String> msgs = Collections.synchronizedList(new LinkedList<>());
//
//		private AtomicInteger counter = new AtomicInteger(0);
//		private int threshold;
//		private my.utils.concurrent.AsyncHandle<String> callback;
//
//		public void setThreshold(int threshold, my.utils.concurrent.AsyncHandle<String> callback) {
//			this.counter.set(0);
//			this.threshold = threshold;
//			this.callback = callback;
//		}
//
//		public int getMessageCount() {
//			return msgs.size();
//		}
//
//		public String getMessage(int index) {
//			return msgs.get(index);
//		}
//
//		public void clear() {
//			msgs.clear();
//		}
//
//		public TestServceHandle(int id) {
//			this.id = id;
//		}
//
//		@Override
//		public String hello(String msg) {
//			if (debug) {
//				ConsoleUtils.info("Handle message in replica[%s]. --MSG:%s", id, msg);
//			}
//			msgs.add(msg);
//			int c = counter.incrementAndGet();
//			if (c == threshold && callback != null) {
//				callback.complete("OK", null);
//			}
//			if (msg != null && msg.startsWith("[SLEEP]")) {
//				try {
//					Thread.sleep(600000);
//				} catch (InterruptedException e) {
//				}
//			}
//			return "OK";
//		}
//
//		@Override
//		public byte[] takeSnapshot() {
//			int msgCount = getMessageCount();
//			ConsoleUtils.info("Take snapshot...[replica.id=%s][message.count=%s]", id, msgCount);
//			return BytesUtils.toBytes(msgCount);
//		}
//
//		@Override
//		public void installSnapshot(byte[] snapshot) {
//			int msgCount = getMessageCount();
//			ConsoleUtils.info("Intall snapshot...[replica.id=%s][message.count=%s][snapshot.size=%s]", id, msgCount,
//					snapshot == null ? 0 : snapshot.length);
//		}
//
//	}
//
//	private static class MessageSendingTask implements Runnable {
//
//		public static final String MESSAGE = "_ABCDEF";
//
//		private CyclicBarrier barrier;
//
//		private CountDownLatch latch;
//
//		private TestServce client;
//
//		private int totalCount;
//		private AtomicInteger counter;
//
//		private my.utils.concurrent.AsyncHandle<String> callback;
//
//		// public MessageSendingTask(int taskId, TestServce client) {
//		// this(taskId, client, null, null, null);
//		// }
//
//		public MessageSendingTask(int totalCount, AtomicInteger counter, TestServce client, CyclicBarrier barrier,
//				CountDownLatch latch, my.utils.concurrent.AsyncHandle<String> callback) {
//			this.totalCount = totalCount;
//			this.counter = counter;
//			this.barrier = barrier;
//			this.latch = latch;
//			this.client = client;
//			this.callback = callback;
//		}
//
//		@Override
//		public void run() {
//			try {
//				if (barrier != null) {
//					barrier.await();
//				}
//				while (true) {
//					int taskId = counter.incrementAndGet();
//					if (taskId > totalCount) {
//						return;
//					}
//					AsyncFuture<String> result = AsyncInvoker.call(client.hello(taskId + MESSAGE));
//					if (callback != null) {
//						result.whenCompleteAsync(callback);
//					}
//				}
//
//			} catch (Exception e) {
//				ConsoleUtils.error("Error occurred on sending message! --%s", e.getMessage());
//				e.printStackTrace();
//			} finally {
//				if (latch != null) {
//					latch.countDown();
//				}
//			}
//		}
//
//	}
//
//}
