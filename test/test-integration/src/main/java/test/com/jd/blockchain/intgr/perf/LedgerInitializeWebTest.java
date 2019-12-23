package test.com.jd.blockchain.intgr.perf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.impl.composite.CompositeConnectionFactory;
//import com.jd.blockchain.storage.service.utils.MemoryBasedDb;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.LedgerInitCommand;
import com.jd.blockchain.tools.initializer.LedgerInitProcess;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.HttpInitConsensServiceFactory;
import com.jd.blockchain.tools.initializer.web.LedgerInitConfiguration;
import com.jd.blockchain.tools.initializer.web.LedgerInitConsensusService;
import com.jd.blockchain.tools.initializer.web.LedgerInitializeWebController;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.LedgerInitConsensusConfig;
import test.com.jd.blockchain.intgr.PresetAnswerPrompter;

public class LedgerInitializeWebTest {

	public static final String PASSWORD = LedgerInitializeTest.PASSWORD;

	public static final String[] PUB_KEYS = LedgerInitializeTest.PUB_KEYS;

	public static final String[] PRIV_KEYS = LedgerInitializeTest.PRIV_KEYS;

	/**
	 * 测试一个节点向多个节点请求新建许可的过程；
	 */
	public void testWithSingleSteps() {
		Prompter prompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();

		HttpInitConsensServiceFactory httpCsSrvFactory = new HttpInitConsensServiceFactory();

		// 加载初始化配置；
		LedgerInitProperties initSetting = loadInitSetting_1();
		// 加载共识配置；
		Properties props = loadConsensusSetting(LedgerInitConsensusConfig.bftsmartConfig.getConfigPath());
		// ConsensusProperties csProps = new ConsensusProperties(props);
		ConsensusProvider csProvider = getConsensusProvider();
		ConsensusSettings csProps = csProvider.getSettingsFactory()
				.getConsensusSettingsBuilder()
				.createSettings(props, Utils.loadParticipantNodes());

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

		if (!initTxContent0.getHash().equals(initTxContent1.getHash())) {
			Operation[] oplist0 = initTxContent0.getOperations();
			Operation[] oplist1 = initTxContent1.getOperations();

			LedgerInitOperation initOp0 = (LedgerInitOperation) oplist0[0];
			LedgerInitOperation initOp1 = (LedgerInitOperation) oplist1[0];

			byte[] initOpBytes0 = BinaryProtocol.encode(initOp0, LedgerInitOperation.class);
			byte[] initOpBytes1 = BinaryProtocol.encode(initOp1, LedgerInitOperation.class);

			UserRegisterOperation regOp00 = (UserRegisterOperation) oplist0[1];
			UserRegisterOperation regOp10 = (UserRegisterOperation) oplist1[1];
			byte[] regOpBytes00 = BinaryProtocol.encode(regOp00, UserRegisterOperation.class);
			byte[] regOpBytes10 = BinaryProtocol.encode(regOp10, UserRegisterOperation.class);

			UserRegisterOperation regOp01 = (UserRegisterOperation) oplist0[2];
			UserRegisterOperation regOp11 = (UserRegisterOperation) oplist1[2];
			byte[] regOpBytes01 = BinaryProtocol.encode(regOp01, UserRegisterOperation.class);
			byte[] regOpBytes11 = BinaryProtocol.encode(regOp11, UserRegisterOperation.class);

			UserRegisterOperation regOp02 = (UserRegisterOperation) oplist0[3];
			UserRegisterOperation regOp12 = (UserRegisterOperation) oplist1[3];
			byte[] regOpBytes02 = BinaryProtocol.encode(regOp02, UserRegisterOperation.class);
			byte[] regOpBytes12 = BinaryProtocol.encode(regOp12, UserRegisterOperation.class);

			UserRegisterOperation regOp03 = (UserRegisterOperation) oplist0[4];
			UserRegisterOperation regOp13 = (UserRegisterOperation) oplist1[4];
			byte[] regOpBytes03 = BinaryProtocol.encode(regOp03, UserRegisterOperation.class);
			byte[] regOpBytes13 = BinaryProtocol.encode(regOp13, UserRegisterOperation.class);

		}

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

		// 测试生成账本，并创建“账本初始化决议”；
		DBConnectionConfig testDb0 = new DBConnectionConfig("memory://local/0");
		DBConnectionConfig testDb1 = new DBConnectionConfig("memory://local/1");
		DBConnectionConfig testDb2 = new DBConnectionConfig("memory://local/2");
		DBConnectionConfig testDb3 = new DBConnectionConfig("memory://local/3");

		LedgerInitDecision dec0 = node0.prepareLedger(testDb0, privkey0);
		LedgerInitDecision dec1 = node1.prepareLedger(testDb1, privkey1);
		LedgerInitDecision dec2 = node2.prepareLedger(testDb2, privkey2);
		LedgerInitDecision dec3 = node3.prepareLedger(testDb3, privkey3);

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

		return permission;
	}

	private void testRequestPermission(NodeWebContext fromNode, PrivKey fromPrivkey, NodeWebContext targetNode,
			LedgerInitConsensusService targetNodeService) {
		SignatureDigest reqSignature = fromNode.createPermissionRequestSignature(fromNode.getId(), fromPrivkey);
		LedgerInitProposal targetPermission = targetNodeService.requestPermission(fromNode.getId(), reqSignature);
	}

	private void testRequestDecision(NodeWebContext fromNode, NodeWebContext targetNode,
			LedgerInitConsensusService targetNodeService) {
		LedgerInitDecision targetDecision = targetNodeService.synchronizeDecision(fromNode.getLocalDecision());
	}

	public SignatureDigest signPermissionRequest(int requesterId, PrivKey privKey, LedgerInitProperties initSetting) {
		byte[] reqAuthBytes = BytesUtils.concat(BytesUtils.toBytes(requesterId), initSetting.getLedgerSeed());
		SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");
		SignatureDigest reqAuthSign = signFunc.sign(privKey, reqAuthBytes);
		return reqAuthSign;
	}

	private static ConsensusProvider getConsensusProvider() {
		return ConsensusProviders.getProvider("com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider");
	}

	public void testInitWith4Nodes() {
		System.out.println("----------- is daemon=" + Thread.currentThread().isDaemon());

		Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();
		LedgerInitProperties initSetting = loadInitSetting_2();
		Properties props = loadConsensusSetting(LedgerInitConsensusConfig.bftsmartConfig.getConfigPath());
		// ConsensusProperties csProps = new ConsensusProperties(props);
		ConsensusProvider csProvider = getConsensusProvider();
		ConsensusSettings csProps = csProvider.getSettingsFactory()
				.getConsensusSettingsBuilder()
				.createSettings(props, Utils.loadParticipantNodes());

		// 启动服务器；
		NetworkAddress initAddr0 = initSetting.getConsensusParticipant(0).getInitializerAddress();
		NodeWebContext node0 = new NodeWebContext(0, initAddr0);

		NetworkAddress initAddr1 = initSetting.getConsensusParticipant(1).getInitializerAddress();
		NodeWebContext node1 = new NodeWebContext(1, initAddr1);

		NetworkAddress initAddr2 = initSetting.getConsensusParticipant(2).getInitializerAddress();
		NodeWebContext node2 = new NodeWebContext(2, initAddr2);

		NetworkAddress initAddr3 = initSetting.getConsensusParticipant(3).getInitializerAddress();
		NodeWebContext node3 = new NodeWebContext(3, initAddr3);

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD);
		PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[1], PASSWORD);
		PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[2], PASSWORD);
		PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[3], PASSWORD);

		CountDownLatch quitLatch = new CountDownLatch(4);

		DBConnectionConfig testDb0 = new DBConnectionConfig();
		testDb0.setConnectionUri("memory://local/0");
		AsyncCallback<HashDigest> callback0 = node0.startInit(privkey0, initSetting, testDb0, consolePrompter,
				quitLatch);

		DBConnectionConfig testDb1 = new DBConnectionConfig();
		testDb1.setConnectionUri("memory://local/1");
		AsyncCallback<HashDigest> callback1 = node1.startInit(privkey1, initSetting, testDb1, consolePrompter,
				quitLatch);

		DBConnectionConfig testDb2 = new DBConnectionConfig();
		testDb2.setConnectionUri("memory://local/2");
		AsyncCallback<HashDigest> callback2 = node2.startInit(privkey2, initSetting, testDb2, consolePrompter,
				quitLatch);

		DBConnectionConfig testDb03 = new DBConnectionConfig();
		testDb03.setConnectionUri("memory://local/3");
		AsyncCallback<HashDigest> callback3 = node3.startInit(privkey3, initSetting, testDb03, consolePrompter,
				quitLatch);

		HashDigest ledgerHash0 = callback0.waitReturn();
		HashDigest ledgerHash1 = callback1.waitReturn();
		HashDigest ledgerHash2 = callback2.waitReturn();
		HashDigest ledgerHash3 = callback3.waitReturn();

		LedgerQuery ledger0 = node0.registLedger(ledgerHash0);
		LedgerQuery ledger1 = node1.registLedger(ledgerHash1);
		LedgerQuery ledger2 = node2.registLedger(ledgerHash2);
		LedgerQuery ledger3 = node3.registLedger(ledgerHash3);

		LedgerBlock genesisBlock = ledger0.getLatestBlock();

		UserAccountQuery userset0 = ledger0.getUserAccountSet(genesisBlock);

		PubKey pubKey0 = KeyGenUtils.decodePubKey(PUB_KEYS[0]);
		Bytes address0 = AddressEncoding.generateAddress(pubKey0);
		UserAccount user0_0 = userset0.getAccount(address0);

		PubKey pubKey1 = KeyGenUtils.decodePubKey(PUB_KEYS[1]);
		Bytes address1 = AddressEncoding.generateAddress(pubKey1);
		UserAccount user1_0 = userset0.getAccount(address1);

		PubKey pubKey2 = KeyGenUtils.decodePubKey(PUB_KEYS[2]);
		Bytes address2 = AddressEncoding.generateAddress(pubKey2);
		UserAccount user2_0 = userset0.getAccount(address2);

		PubKey pubKey3 = KeyGenUtils.decodePubKey(PUB_KEYS[3]);
		Bytes address3 = AddressEncoding.generateAddress(pubKey3);
		UserAccount user3_0 = userset0.getAccount(address3);
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

	public static LedgerInitProperties loadInitSetting_2() {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger_init_test_web2.init");
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

		// private MQConnectionConfig mqConnConfig;

		private volatile ConfigurableApplicationContext ctx;

		private volatile LedgerInitProcess initProcess;

		private volatile LedgerInitializeWebController controller;

		private volatile LedgerManager ledgerManager;

		private volatile DbConnectionFactory db;

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

		public LedgerRepository ledgerRepository(HashDigest ledgerHash) {
			return ledgerManager.getLedger(ledgerHash);
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
														  LedgerInitProperties ledgerSetting, ConsensusSettings csProps, ConsensusProvider csProvider,
														  DBConnectionConfig dbConnConfig, Prompter prompter, LedgerBindingConfig conf,
														  CountDownLatch quitLatch, DbConnectionFactory db) {
			this.dbConnConfig = dbConnConfig;
			// this.mqConnConfig = mqConnConfig;
			this.db = db;

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					LedgerInitCommand initCmd = new LedgerInitCommand();
					HashDigest ledgerHash = initCmd.startInit(id, privKey, base58Pwd, ledgerSetting, dbConnConfig,
							prompter, conf, db);

					LedgerManager lm = initCmd.getLedgerManager();

					NodeWebContext.this.ledgerManager = lm;

					quitLatch.countDown();
					return ledgerHash;
				}
			};

			return invoker.start();
		}


		public LedgerInitProposal preparePermision(PrivKey privKey, LedgerInitConfiguration initConfig) {
			return controller.prepareLocalPermission(id, privKey, initConfig);
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

		public DbConnectionFactory getStorageDB() {
			return db;
		}
	}

}
