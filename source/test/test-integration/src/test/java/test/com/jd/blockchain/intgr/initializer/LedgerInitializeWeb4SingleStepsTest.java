package test.com.jd.blockchain.intgr.initializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitProposal;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.impl.composite.CompositeConnectionFactory;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.LedgerInitCommand;
import com.jd.blockchain.tools.initializer.LedgerInitProcess;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.HttpInitConsensServiceFactory;
import com.jd.blockchain.tools.initializer.web.LedgerInitConfiguration;
import com.jd.blockchain.tools.initializer.web.LedgerInitConsensusService;
import com.jd.blockchain.tools.initializer.web.LedgerInitializeWebController;
import com.jd.blockchain.transaction.SignatureUtils;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.LedgerInitConsensusConfig;
import test.com.jd.blockchain.intgr.PresetAnswerPrompter;
import test.com.jd.blockchain.intgr.perf.Utils;

public class LedgerInitializeWeb4SingleStepsTest {

	public static final String PASSWORD = LedgerInitializeTest.PASSWORD;

	public static final String[] PUB_KEYS = LedgerInitializeTest.PUB_KEYS;

	public static final String[] PRIV_KEYS = LedgerInitializeTest.PRIV_KEYS;

	/**
	 * 测试一个节点向多个节点请求新建许可的过程；
	 */

	public void testWithSingleSteps() {

	}

	public void testWithSingleSteps(LedgerInitConsensusConfig.ConsensusConfig consensusConfig, String[] dbConns) {
		Prompter prompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();

		HttpInitConsensServiceFactory httpCsSrvFactory = new HttpInitConsensServiceFactory();

		// 加载初始化配置；
		LedgerInitProperties initSetting = loadInitSetting_1();
		// 加载共识配置；
		Properties props = loadConsensusSetting(consensusConfig.getConfigPath());
		ConsensusProvider csProvider = LedgerInitConsensusConfig.getConsensusProvider(consensusConfig.getProvider());
		ConsensusSettings csProps = csProvider.getSettingsFactory().getConsensusSettingsBuilder().createSettings(props,
				Utils.loadParticipantNodes());

		// 启动服务器；
		NetworkAddress initAddr0 = initSetting.getConsensusParticipant(0).getInitializerAddress();
		NodeWebContext node0 = new NodeWebContext(0, initAddr0);
		node0.startServer();

		NetworkAddress initAddr1 = initSetting.getConsensusParticipant(1).getInitializerAddress();
		NodeWebContext node1 = new NodeWebContext(1, initAddr1);
		node1.startServer();

		NetworkAddress initAddr2 = initSetting.getConsensusParticipant(2).getInitializerAddress();
		NodeWebContext node2 = new NodeWebContext(2, initAddr2);
		node2.startServer();

		NetworkAddress initAddr3 = initSetting.getConsensusParticipant(3).getInitializerAddress();
		NodeWebContext node3 = new NodeWebContext(3, initAddr3);
		node3.startServer();

		node0.setPrompter(prompter);
		node1.setPrompter(prompter);
		node2.setPrompter(prompter);
		node3.setPrompter(prompter);

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD);
		PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[1], PASSWORD);
		PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[2], PASSWORD);
		PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[3], PASSWORD);

		PubKey pubKey0 = KeyGenUtils.decodePubKey(PUB_KEYS[0]);
		PubKey pubKey1 = KeyGenUtils.decodePubKey(PUB_KEYS[1]);
		PubKey pubKey2 = KeyGenUtils.decodePubKey(PUB_KEYS[2]);
		PubKey pubKey3 = KeyGenUtils.decodePubKey(PUB_KEYS[3]);

		// 测试生成“账本初始化许可”；
		LedgerInitConfiguration initConfig = LedgerInitConfiguration.create(initSetting);
		initConfig.setConsensusSettings(csProvider, csProps);
		LedgerInitProposal permission0 = testPreparePermisssion(node0, privkey0, initConfig);
		LedgerInitProposal permission1 = testPreparePermisssion(node1, privkey1, initConfig);
		LedgerInitProposal permission2 = testPreparePermisssion(node2, privkey2, initConfig);
		LedgerInitProposal permission3 = testPreparePermisssion(node3, privkey3, initConfig);

		TransactionContent initTxContent0 = node0.getInitTxContent();
		TransactionContent initTxContent1 = node1.getInitTxContent();
		TransactionContent initTxContent2 = node2.getInitTxContent();
		TransactionContent initTxContent3 = node3.getInitTxContent();

		assertTrue(SignatureUtils.verifySignature(initTxContent0, permission0.getTransactionSignature(), pubKey0));
		assertTrue(SignatureUtils.verifySignature(initTxContent1, permission1.getTransactionSignature(), pubKey1));
		assertTrue(SignatureUtils.verifySignature(initTxContent2, permission2.getTransactionSignature(), pubKey2));
		assertTrue(SignatureUtils.verifySignature(initTxContent3, permission3.getTransactionSignature(), pubKey3));

		assertNotNull(initTxContent0.getHash());
		if (!initTxContent0.getHash().equals(initTxContent1.getHash())) {
			assertNull(initTxContent0.getLedgerHash());
			assertNull(initTxContent1.getLedgerHash());
			Operation[] oplist0 = initTxContent0.getOperations();
			Operation[] oplist1 = initTxContent1.getOperations();
			assertEquals(oplist0.length, oplist1.length);

			LedgerInitOperation initOp0 = (LedgerInitOperation) oplist0[0];
			LedgerInitOperation initOp1 = (LedgerInitOperation) oplist1[0];

			byte[] initOpBytes0 = BinaryProtocol.encode(initOp0, LedgerInitOperation.class);
			byte[] initOpBytes1 = BinaryProtocol.encode(initOp1, LedgerInitOperation.class);
			assertTrue(BytesUtils.equals(initOpBytes0, initOpBytes1));

			UserRegisterOperation regOp00 = (UserRegisterOperation) oplist0[1];
			UserRegisterOperation regOp10 = (UserRegisterOperation) oplist1[1];
			byte[] regOpBytes00 = BinaryProtocol.encode(regOp00, UserRegisterOperation.class);
			byte[] regOpBytes10 = BinaryProtocol.encode(regOp10, UserRegisterOperation.class);
			assertTrue(BytesUtils.equals(regOpBytes00, regOpBytes10));

			UserRegisterOperation regOp01 = (UserRegisterOperation) oplist0[2];
			UserRegisterOperation regOp11 = (UserRegisterOperation) oplist1[2];
			byte[] regOpBytes01 = BinaryProtocol.encode(regOp01, UserRegisterOperation.class);
			byte[] regOpBytes11 = BinaryProtocol.encode(regOp11, UserRegisterOperation.class);
			assertTrue(BytesUtils.equals(regOpBytes01, regOpBytes11));

			UserRegisterOperation regOp02 = (UserRegisterOperation) oplist0[3];
			UserRegisterOperation regOp12 = (UserRegisterOperation) oplist1[3];
			byte[] regOpBytes02 = BinaryProtocol.encode(regOp02, UserRegisterOperation.class);
			byte[] regOpBytes12 = BinaryProtocol.encode(regOp12, UserRegisterOperation.class);
			assertTrue(BytesUtils.equals(regOpBytes02, regOpBytes12));

			UserRegisterOperation regOp03 = (UserRegisterOperation) oplist0[4];
			UserRegisterOperation regOp13 = (UserRegisterOperation) oplist1[4];
			byte[] regOpBytes03 = BinaryProtocol.encode(regOp03, UserRegisterOperation.class);
			byte[] regOpBytes13 = BinaryProtocol.encode(regOp13, UserRegisterOperation.class);
			assertTrue(BytesUtils.equals(regOpBytes03, regOpBytes13));

		}
		assertEquals(initTxContent0.getHash(), initTxContent1.getHash());
		assertEquals(initTxContent0.getHash(), initTxContent2.getHash());
		assertEquals(initTxContent0.getHash(), initTxContent3.getHash());

		assertNull(initTxContent0.getLedgerHash());
		assertNull(initTxContent1.getLedgerHash());
		assertNull(initTxContent2.getLedgerHash());
		assertNull(initTxContent3.getLedgerHash());

		// 测试请求“账本初始化许可”；
		// test request permission, and verify the response;
		LedgerInitConsensusService initCsService0 = httpCsSrvFactory.connect(initAddr0);
		LedgerInitConsensusService initCsService1 = httpCsSrvFactory.connect(initAddr1);
		LedgerInitConsensusService initCsService2 = httpCsSrvFactory.connect(initAddr2);
		LedgerInitConsensusService initCsService3 = httpCsSrvFactory.connect(initAddr3);

		testRequestPermission(node0, privkey0, node1, initCsService1);
		testRequestPermission(node0, privkey0, node2, initCsService2);
		testRequestPermission(node0, privkey0, node3, initCsService3);
		testRequestPermission(node1, privkey1, node0, initCsService0);
		testRequestPermission(node1, privkey1, node2, initCsService2);
		testRequestPermission(node1, privkey1, node3, initCsService3);
		testRequestPermission(node2, privkey2, node0, initCsService0);
		testRequestPermission(node2, privkey2, node1, initCsService1);
		testRequestPermission(node2, privkey2, node3, initCsService3);
		testRequestPermission(node3, privkey3, node0, initCsService0);
		testRequestPermission(node3, privkey3, node1, initCsService1);
		testRequestPermission(node3, privkey3, node2, initCsService2);

		// 测试在节点之间共识彼此的“账本初始化许可”
		boolean allPermitted0 = node0.consensusPermission(privkey0);
		boolean allPermitted1 = node1.consensusPermission(privkey1);
		boolean allPermitted2 = node2.consensusPermission(privkey2);
		boolean allPermitted3 = node3.consensusPermission(privkey3);

		assertTrue(allPermitted0);
		assertTrue(allPermitted1);
		assertTrue(allPermitted2);
		assertTrue(allPermitted3);

		// 测试生成账本，并创建“账本初始化决议”；
		DBConnectionConfig testDb0 = new DBConnectionConfig(dbConns[0]);
		DBConnectionConfig testDb1 = new DBConnectionConfig(dbConns[1]);
		DBConnectionConfig testDb2 = new DBConnectionConfig(dbConns[2]);
		DBConnectionConfig testDb3 = new DBConnectionConfig(dbConns[3]);

		LedgerInitDecision dec0 = node0.prepareLedger(testDb0, privkey0);
		LedgerInitDecision dec1 = node1.prepareLedger(testDb1, privkey1);
		LedgerInitDecision dec2 = node2.prepareLedger(testDb2, privkey2);
		LedgerInitDecision dec3 = node3.prepareLedger(testDb3, privkey3);

		assertEquals(dec0.getLedgerHash(), dec1.getLedgerHash());
		assertEquals(dec0.getLedgerHash(), dec2.getLedgerHash());
		assertEquals(dec0.getLedgerHash(), dec3.getLedgerHash());

		testRequestDecision(node0, node1, initCsService1);
		testRequestDecision(node0, node2, initCsService2);
		testRequestDecision(node0, node3, initCsService3);
		testRequestDecision(node1, node0, initCsService0);
		testRequestDecision(node1, node2, initCsService2);
		testRequestDecision(node1, node3, initCsService3);
		testRequestDecision(node2, node0, initCsService0);
		testRequestDecision(node2, node1, initCsService1);
		testRequestDecision(node2, node3, initCsService3);
		testRequestDecision(node3, node0, initCsService0);
		testRequestDecision(node3, node1, initCsService1);
		testRequestDecision(node3, node2, initCsService2);
	}

	private LedgerInitProposal testPreparePermisssion(NodeWebContext node, PrivKey privKey,
			LedgerInitConfiguration setting) {
		LedgerInitProposal permission = node.preparePermision(privKey, setting);

		assertEquals(node.getId(), permission.getParticipantId());
		assertNotNull(permission.getTransactionSignature());

		return permission;
	}

	private void testRequestPermission(NodeWebContext fromNode, PrivKey fromPrivkey, NodeWebContext targetNode,
			LedgerInitConsensusService targetNodeService) {
		SignatureDigest reqSignature = fromNode.createPermissionRequestSignature(fromNode.getId(), fromPrivkey);
		LedgerInitProposal targetPermission = targetNodeService.requestPermission(fromNode.getId(), reqSignature);
		assertEquals(targetNode.getId(), targetPermission.getParticipantId());
		assertEquals(targetNode.getLocalPermission().getTransactionSignature(),
				targetPermission.getTransactionSignature());
	}

	private void testRequestDecision(NodeWebContext fromNode, NodeWebContext targetNode,
			LedgerInitConsensusService targetNodeService) {
		LedgerInitDecision targetDecision = targetNodeService.synchronizeDecision(fromNode.getLocalDecision());
		assertEquals(targetNode.getId(), targetDecision.getParticipantId());
		assertEquals(targetNode.getLocalDecision().getLedgerHash(), targetDecision.getLedgerHash());
		assertEquals(targetNode.getLocalDecision().getSignature(), targetDecision.getSignature());
	}

	public static LedgerInitProperties loadInitSetting_1() {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger_init_test_web1.init");
		try (InputStream in = ledgerInitSettingResource.getInputStream()) {
			LedgerInitProperties setting = LedgerInitProperties.resolve(in);
			return setting;
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static Properties loadConsensusSetting(String configPath) {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource(configPath);
		try (InputStream in = ledgerInitSettingResource.getInputStream()) {
			return FileUtils.readProperties(in);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static class NodeWebContext {

		private NetworkAddress serverAddress;

		private DBConnectionConfig dbConnConfig;

		private volatile ConfigurableApplicationContext ctx;

		private volatile LedgerInitProcess initProcess;

		private volatile LedgerInitializeWebController controller;

		private volatile LedgerManager ledgerManager;

		private volatile CompositeConnectionFactory db;

		private int id;

		public int getId() {
			return controller.getId();
		}

		public TransactionContent getInitTxContent() {
			return controller.getInitTxContent();
		}

		public LedgerInitProposal getLocalPermission() {
			return controller.getLocalPermission();
		}

		public LedgerInitDecision getLocalDecision() {
			return controller.getLocalDecision();
		}

		public NodeWebContext(int id, NetworkAddress serverAddress) {
			this.id = id;
			this.serverAddress = serverAddress;
		}

		public LedgerQuery registLedger(HashDigest ledgerHash) {
			// LedgerManage ledgerManager = ctx.getBean(LedgerManage.class);
			//
			// DbConnectionFactory dbConnFactory = ctx.getBean(DbConnectionFactory.class);
			// DbConnection conn = dbConnFactory.connect(dbConnConfig.getUri(),
			// dbConnConfig.getPassword());

			// DbConnection conn = db.connect(dbConnConfig.getUri(),
			// dbConnConfig.getPassword());
			DbConnection conn = db.connect(dbConnConfig.getUri());
			LedgerQuery ledgerRepo = ledgerManager.register(ledgerHash, conn.getStorageService());
			return ledgerRepo;
		}

		public SignatureDigest createPermissionRequestSignature(int requesterId, PrivKey privKey) {
			return controller.signPermissionRequest(requesterId, privKey);
		}

		public AsyncCallback<HashDigest> startInit(PrivKey privKey, LedgerInitProperties setting,
				DBConnectionConfig dbConnConfig, Prompter prompter, CountDownLatch quitLatch) {

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					doStartServer();

					// NodeWebContext.this.initProcess = ctx.getBean(LedgerInitProcess.class);
					NodeWebContext.this.dbConnConfig = dbConnConfig;
					HashDigest ledgerHash = NodeWebContext.this.initProcess.initialize(id, privKey, setting,
							dbConnConfig, prompter);

					quitLatch.countDown();
					return ledgerHash;
				}
			};

			return invoker.start();
		}

		public AsyncCallback<HashDigest> startInitCommand(PrivKey privKey, String base58Pwd,
				LedgerInitProperties ledgerSetting, DBConnectionConfig dbConnConfig, Prompter prompter,
				LedgerBindingConfig conf, CountDownLatch quitLatch) {
			this.db = new CompositeConnectionFactory();
			this.dbConnConfig = dbConnConfig;

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					LedgerInitCommand initCmd = new LedgerInitCommand();
					HashDigest ledgerHash = initCmd.startInit(id, privKey, base58Pwd, ledgerSetting, dbConnConfig,
							prompter, conf, db);
					NodeWebContext.this.ledgerManager = initCmd.getLedgerManager();
					quitLatch.countDown();
					return ledgerHash;
				}
			};

			return invoker.start();
		}

		public LedgerInitProposal preparePermision(PrivKey privKey, LedgerInitConfiguration setting) {
			return controller.prepareLocalPermission(id, privKey, setting);
		}

		public boolean consensusPermission(PrivKey privKey) {
			return controller.consensusPermisions(privKey);
		}

		public LedgerInitDecision prepareLedger(DBConnectionConfig dbConnConfig, PrivKey privKey) {
			controller.connectDb(dbConnConfig);
			return controller.makeLocalDecision(privKey);
		}

		public void startServer() {
			ThreadInvoker<Object> invoker = new ThreadInvoker<Object>() {

				@Override
				protected Object invoke() throws Exception {
					doStartServer();
					return null;
				}
			};

			invoker.startAndWait();
		}

		public void setPrompter(Prompter prompter) {
			controller.setPrompter(prompter);
		}

		public void doStartServer() {
			String argServerAddress = String.format("--server.address=%s", serverAddress.getHost());
			String argServerPort = String.format("--server.port=%s", serverAddress.getPort());
			String nodebug = "--debug=false";
			String[] innerArgs = { argServerAddress, argServerPort, nodebug };

			ctx = SpringApplication.run(LedgerInitWebTestConfiguration.class, innerArgs);

			ctx.setId("Node-" + id);
			controller = ctx.getBean(LedgerInitializeWebController.class);
			ledgerManager = ctx.getBean(LedgerManager.class);
			db = ctx.getBean(CompositeConnectionFactory.class);
			initProcess = ctx.getBean(LedgerInitProcess.class);
		}

		public void closeServer() {
			if (this.ctx != null) {
				this.ctx.close();
				this.ctx = null;
			}
		}

		public LedgerManager getLedgerManager() {
			return ledgerManager;
			// return ctx.getBean(LedgerManager.class);
		}

		public CompositeConnectionFactory getStorageDB() {
			return db;
			// return ctx.getBean(MemoryBasedDb.class);
		}
	}

	private static class ConsensusConfig {
		String provider;

		String configPath;

		public String getProvider() {
			return provider;
		}

		public String getConfigPath() {
			return configPath;
		}
	}

}
