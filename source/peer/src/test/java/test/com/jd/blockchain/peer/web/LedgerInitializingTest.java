//package test.com.jd.blockchain.peer.web;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyInt;
//import static org.mockito.Mockito.mocker;
//import static org.mockito.Mockito.spy;
//import static org.mockito.Mockito.when;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.FixMethodOrder;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.jd.blockchain.ledger.BlockchainIdentity;
//import com.jd.blockchain.ledger.BlockchainKeyPair;
//import com.jd.blockchain.ledger.DigitalSignature;
//import com.jd.blockchain.ledger.data.SignatureUtils;
//import com.jd.blockchain.ledger.service.LedgerService;
//import com.jd.blockchain.ledger.service.impl.LedgerServiceImpl;
//import com.jd.blockchain.peer.PeerSettings;
//import com.jd.blockchain.peer.service.BlockchainKeyInfo;
//import com.jd.blockchain.peer.service.MessageBroadcaster;
//import com.jd.blockchain.peer.service.PeerKeyStorageService;
//import com.jd.blockchain.peer.web.LedgerInitializationContext;
//import com.jd.blockchain.peer.web.LedgerInitializingController;
//import com.jd.blockchain.peer.web.LedgerInitializingHttpService;
//import com.jd.blockchain.storage.service.impl.redis.RedisStorageService;
//
//import my.utils.io.ByteArray;
//import redis.clients.jedis.Jedis;
//
//@Ignore
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = { ControllerTestConfiguration.class})
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class LedgerInitializingTest {
//
//	@Autowired
//	private PeerKeyStorageService keyStorageService;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	@Before
//	public void setup() {
//	}
//
//	@After
//	public void after() {
//	}
//
//	@Test
//	public void testStartNewLedger() {
//		// 准备上下文；
//		ServerContext cordinator = new ServerContext("cordinator", 0, 9000, keyStorageService);
//
//		ServerContext participant1 = new ServerContext("participant1", 1, 9001, keyStorageService);
//		participant1.setLedgerInitHttpServiceProxy(cordinator.getController());
//
//		ServerContext participant2 = new ServerContext("participant2", 2, 9002, keyStorageService);
//		participant2.setLedgerInitHttpServiceProxy(cordinator.getController());
//
//		ServerContext participant3 = new ServerContext("participant3", 3, 9003, keyStorageService);
//		participant3.setLedgerInitHttpServiceProxy(cordinator.getController());
//
//		// 协调节点开始新建账本；
//		LedgerInitializationContext ctx = cordinator.controller.startNewLedger(cordinator.keyInfo.getName(),
//				cordinator.keyInfo.getIdentity().getAddress());
//		String defId = ctx.getLedgerDefinition().getDefinitionId();
//
//		// 参与者加入账本；
//		participant1.controller.startJoiningLedger(defId, "localhost", 9000, participant1.keyInfo.getName(),
//				participant1.keyInfo.getIdentity().getAddress());
//		participant2.controller.startJoiningLedger(defId, "localhost", 9000, participant2.keyInfo.getName(),
//				participant2.keyInfo.getIdentity().getAddress());
//		participant3.controller.startJoiningLedger(defId, "localhost", 9000, participant3.keyInfo.getName(),
//				participant3.keyInfo.getIdentity().getAddress());
//
//		// TODO:断言协调节点已经收到了参与者的请求；
//		
//
//		// 协调节点签署账本；
//		cordinator.controller.prepareAndSign();
//
//		// 等待各个参与节点完成签署和生成块；
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// Swallow InterruptedException;
//		}
//
//		LedgerInitializationContext cordCtx = cordinator.controller.getLedgerInitContext();
//		LedgerInitializationContext part1Ctx = participant1.controller.getLedgerInitContext();
//		LedgerInitializationContext part2Ctx = participant2.controller.getLedgerInitContext();
//		LedgerInitializationContext part3Ctx = participant3.controller.getLedgerInitContext();
//
//		// 断言各个参与节点都达到了块一致的状态；
//		//ssertTrue(cordCtx.isConsistent());
//
//		// 等待各个参与节点完成签署和生成块；
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// Swallow InterruptedException;
//		}
//		//assertTrue(part1Ctx.isConsistent());
//		//assertTrue(part2Ctx.isConsistent());
//		//assertTrue(part3Ctx.isConsistent());
//
//		// 断言各个参与节点的块都是真正地一致；
//		//assertEquals(cordCtx.getLedger().getLedgerHash(), part1Ctx.getLedger().getLedgerHash());
//		//assertEquals(cordCtx.getLedger().getLedgerHash(), part2Ctx.getLedger().getLedgerHash());
//		//assertEquals(cordCtx.getLedger().getLedgerHash(), part3Ctx.getLedger().getLedgerHash());
//
//		// 协调节点提交；
//		cordinator.controller.commitLedger();
//
//		// 等待各个参与节点完成提交；
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// Swallow InterruptedException;
//		}
//
//		// 断言各个参与节点的状态都被重置了；
//		cordCtx = cordinator.controller.getLedgerInitContext();
//		part1Ctx = participant1.controller.getLedgerInitContext();
//		part2Ctx = participant2.controller.getLedgerInitContext();
//		part3Ctx = participant3.controller.getLedgerInitContext();
//		
//		//assertFalse(cordCtx.isJoined());
//		//assertFalse(part1Ctx.isJoined());
//		//assertFalse(part2Ctx.isJoined());
//		//assertFalse(part3Ctx.isJoined());
//		
//		//assertNull(cordCtx.getLedgerDefinition());
//		//assertNull(part1Ctx.getLedgerDefinition());
//		//assertNull(part2Ctx.getLedgerDefinition());
//		//assertNull(part3Ctx.getLedgerDefinition());
//	}
//
//	public static class ServerContext {
//
//		private PeerKeyStorageService keystoreService;
//
//		private LedgerService ledgerService;
//
//		private PeerSettings peerSettings;
//
//		private MessageBroadcaster msgBroadcaster; // 用于向客户端进行消息通知；
//
//		private LedgerInitializingController controller;
//
//		//private final BlockchainKeyPair key;
//
//		private final BlockchainIdentity identity;
//
//		private final BlockchainKeyInfo keyInfo;
//
//		private LedgerInitializingHttpService ledgerInitHttpServiceProxy;
//
//		public ServerContext(String name, int idx, int port, PeerKeyStorageService keystoreService) {
//			Jedis jedis = new Jedis("192.168.151.33", 6379);
//			jedis.select(idx);
//			jedis.connect();
//
//			this.ledgerService = new LedgerServiceImpl(new RedisStorageService(jedis));
//			this.peerSettings =  peerSetting(port);
//			this.keystoreService = keystoreService;
//
//			this.keyInfo = this.keystoreService.generateNewKey(name);
//			this.identity = this.keyInfo.getIdentity();
//
//			//this.key = BlockchainKeyGenerator.getInstance().generate(KeyType.ED25519);
//			//this.keyInfo = new BlockchainKeyInfo();
//			//this.keyInfo.setName(name);
//			//this.keyInfo.setIdentity(key.getIdentity());
//
//			initTestContext();
//		}
//
//
//		private PeerSettings peerSetting(int port) {
//			PeerSettings setting = new PeerSettings();
//			PeerSettings.ConsensusSetting consensusSetting = new PeerSettings.ConsensusSetting();
//			consensusSetting.setIp("127.0.0.1");
//			consensusSetting.setPort(port);
//			setting.setConsensus(consensusSetting);
//
//			return setting;
//		}
//
//
//		private void initTestContext() {
//			//keystoreService = spy(PeerKeyStorageServiceImpl.class);
//			//when(keystoreService.getBlockchainKey(key.getAddress())).thenReturn(keyInfo);
//			//when(keystoreService.sign(any(), key.getAddress())).then(answerSignature(key));
//
//			msgBroadcaster = mocker(MessageBroadcaster.class);
//
//			LedgerInitializingController ctrl = new LedgerInitializingController(peerSettings, keystoreService,
//					ledgerService, msgBroadcaster);
//			this.controller = spy(ctrl);
//
//			when(controller.getLedgerInitHttpService(any(), anyInt())).then(new Answer<LedgerInitializingHttpService>() {
//				@Override
//				public LedgerInitializingHttpService answer(InvocationOnMock invocationOnMock) throws Throwable {
//					return ledgerInitHttpServiceProxy;
//				}
//			});
//		}
//
//		private Answer<DigitalSignature> answerSignature(BlockchainKeyPair keyPair) {
//			return new Answer<DigitalSignature>() {
//				@Override
//				public DigitalSignature answer(InvocationOnMock invocation) throws Throwable {
//					Object[] args = invocation.getArguments();
//					ByteArray data = (ByteArray) args[0];
//					return SignatureUtils.sign(data, keyPair);
//				}
//			};
//		}
//
//		public LedgerInitializingController getController() {
//			return controller;
//		}
//
//		public LedgerInitializingHttpService getLedgerInitHttpServiceProxy() {
//			return ledgerInitHttpServiceProxy;
//		}
//
//		public void setLedgerInitHttpServiceProxy(LedgerInitializingHttpService ledgerInitHttpServiceProxy) {
//			this.ledgerInitHttpServiceProxy = ledgerInitHttpServiceProxy;
//		}
//
//	}
//}
