package test.com.jd.blockchain.intgr.perf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import com.jd.blockchain.storage.service.impl.composite.CompositeConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.gateway.GatewayConfigProperties.KeyPairConfig;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitProposal;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.LedgerInitCommand;
import com.jd.blockchain.tools.initializer.LedgerInitProcess;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.LedgerInitializeWebController;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.GatewayTestRunner;
import test.com.jd.blockchain.intgr.IntegratedContext;
import test.com.jd.blockchain.intgr.IntegratedContext.Node;
import test.com.jd.blockchain.intgr.PeerTestRunner;
import test.com.jd.blockchain.intgr.PresetAnswerPrompter;

public class GlobalPerformanceTest {

	public static final int CONCURRENT_USER_COUNT = 1;
	public static final int USER_TX_COUNT = 1;

	public static void test(String[] args) {
		// init ledgers of all nodes ;
		IntegratedContext context = initLedgers();
		Node node0 = context.getNode(0);
		Node node1 = context.getNode(1);
		Node node2 = context.getNode(2);
		Node node3 = context.getNode(3);

		NetworkAddress peerSrvAddr0 = new NetworkAddress("127.0.0.1", 10200);
		PeerTestRunner peer0 = new PeerTestRunner(peerSrvAddr0, node0.getBindingConfig(), node0.getStorageDB(), null);

		NetworkAddress peerSrvAddr1 = new NetworkAddress("127.0.0.1", 10210);
		PeerTestRunner peer1 = new PeerTestRunner(peerSrvAddr1, node1.getBindingConfig(), node1.getStorageDB(), null);

		NetworkAddress peerSrvAddr2 = new NetworkAddress("127.0.0.1", 10220);
		PeerTestRunner peer2 = new PeerTestRunner(peerSrvAddr2, node2.getBindingConfig(), node2.getStorageDB(), null);

		NetworkAddress peerSrvAddr3 = new NetworkAddress("127.0.0.1", 10230);
		PeerTestRunner peer3 = new PeerTestRunner(peerSrvAddr3, node3.getBindingConfig(), node3.getStorageDB(), null);

		AsyncCallback<Object> peerStarting0 = peer0.start();
		AsyncCallback<Object> peerStarting1 = peer1.start();
		AsyncCallback<Object> peerStarting2 = peer2.start();
		AsyncCallback<Object> peerStarting3 = peer3.start();

		peerStarting0.waitReturn();
		peerStarting1.waitReturn();
		peerStarting2.waitReturn();
		peerStarting3.waitReturn();

		String encodedBase58Pwd = KeyGenUtils.encodePasswordAsBase58(Utils.PASSWORD);

		KeyPairConfig gwkey0 = new KeyPairConfig();
		gwkey0.setPubKeyValue(Utils.PUB_KEYS[0]);
		gwkey0.setPrivKeyValue(Utils.PRIV_KEYS[0]);
		gwkey0.setPrivKeyPassword(encodedBase58Pwd);
		GatewayTestRunner gateway0 = new GatewayTestRunner("127.0.0.1", 10300, gwkey0, peerSrvAddr0);

		KeyPairConfig gwkey1 = new KeyPairConfig();
		gwkey1.setPubKeyValue(Utils.PUB_KEYS[1]);
		gwkey1.setPrivKeyValue(Utils.PRIV_KEYS[1]);
		gwkey1.setPrivKeyPassword(encodedBase58Pwd);
		GatewayTestRunner gateway1 = new GatewayTestRunner("127.0.0.1", 10310, gwkey1, peerSrvAddr1);

		AsyncCallback<Object> gwStarting0 = gateway0.start();
		AsyncCallback<Object> gwStarting1 = gateway1.start();

		gwStarting0.waitReturn();
		gwStarting1.waitReturn();

		// 连接网关；
		GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway0.getServiceAddress());
		BlockchainService blockchainService = gwsrvFact.getBlockchainService();

		int batchSize = CONCURRENT_USER_COUNT * USER_TX_COUNT;
		BlockchainKeypair[] keys = generateKeys(batchSize);

		HashDigest ledgerHash = node0.getLedgerManager().getLedgerHashs()[0];
		LedgerQuery ledger = node0.getLedgerManager().getLedger(ledgerHash);

		PreparedTransaction[] ptxs = prepareTransactions_RegisterDataAcount(keys, node0.getPartiKeyPair(), ledgerHash,
				blockchainService);

		LedgerBlock latestBlock = ledger.getLatestBlock();

		ConsoleUtils.info("\r\n-----------------------------------------------");
		ConsoleUtils.info("------ 开始执行交易 [当前区块高度=%s] ------", latestBlock.getHeight());
		ConsoleUtils.info("-----------------------------------------------\r\n");
		long startTs = System.currentTimeMillis();

		// for (PreparedTransaction ptx : ptxs) {
		// ptx.commit();
		// }

		CyclicBarrier barrier = new CyclicBarrier(CONCURRENT_USER_COUNT);
		CountDownLatch latch = new CountDownLatch(CONCURRENT_USER_COUNT);
		for (int i = 0; i < CONCURRENT_USER_COUNT; i++) {
			TransactionCommitter committer = new TransactionCommitter(ptxs, i * USER_TX_COUNT, USER_TX_COUNT);
			committer.start(barrier, latch);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			ConsoleUtils.error("Error occurred on waiting accomplishing all tx committing. --%s", e.getMessage());
		}

		long elapsedTs = System.currentTimeMillis() - startTs;

		latestBlock = ledger.retrieveLatestBlock();
		ConsoleUtils.info("全部交易执行完成! -- 当前区块高度=%s; 交易数=%s; 总耗时= %s ms; TPS=%.2f", latestBlock.getHeight(), batchSize,
				elapsedTs, (batchSize * 1000.00D / elapsedTs));
	}

	private static BlockchainKeypair[] generateKeys(int count) {
		BlockchainKeypair[] keys = new BlockchainKeypair[count];
		for (int i = 0; i < count; i++) {
			keys[i] = BlockchainKeyGenerator.getInstance().generate();
		}
		return keys;
	}

	private static PreparedTransaction[] prepareTransactions_RegisterDataAcount(BlockchainKeypair[] userKeys,
			AsymmetricKeypair adminKey, HashDigest ledgerHash, BlockchainService blockchainService) {
		PreparedTransaction[] ptxs = new PreparedTransaction[userKeys.length];
		for (int i = 0; i < ptxs.length; i++) {
			// 定义交易；
			TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
			txTpl.users().register(userKeys[i].getIdentity());

			// 签名；
			PreparedTransaction ptx = txTpl.prepare();
			ptx.sign(adminKey);

			ptxs[i] = ptx;
		}

		return ptxs;
	}

	public static ConsensusProvider getConsensusProvider() {
		return ConsensusProviders.getProvider("com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider");
	}

	private static IntegratedContext initLedgers() {
		Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();
		LedgerInitProperties initSetting = loadInitSetting_integration();
		Properties props = Utils.loadConsensusSetting();
		ConsensusProvider csProvider = getConsensusProvider();
		ConsensusSettings csProps = csProvider.getSettingsFactory().getConsensusSettingsBuilder().createSettings(props,
				Utils.loadParticipantNodes());

		// 启动服务器；
		NetworkAddress initAddr0 = initSetting.getConsensusParticipant(0).getInitializerAddress();
		NodeInitContext nodeCtx0 = new NodeInitContext(0, initAddr0);

		NetworkAddress initAddr1 = initSetting.getConsensusParticipant(1).getInitializerAddress();
		NodeInitContext nodeCtx1 = new NodeInitContext(1, initAddr1);

		NetworkAddress initAddr2 = initSetting.getConsensusParticipant(2).getInitializerAddress();
		NodeInitContext nodeCtx2 = new NodeInitContext(2, initAddr2);

		NetworkAddress initAddr3 = initSetting.getConsensusParticipant(3).getInitializerAddress();
		NodeInitContext nodeCtx3 = new NodeInitContext(3, initAddr3);

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(Utils.PRIV_KEYS[0], Utils.PASSWORD);
		PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(Utils.PRIV_KEYS[1], Utils.PASSWORD);
		PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(Utils.PRIV_KEYS[2], Utils.PASSWORD);
		PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(Utils.PRIV_KEYS[3], Utils.PASSWORD);

		String encodedPassword = KeyGenUtils.encodePasswordAsBase58(Utils.PASSWORD);

		CountDownLatch quitLatch = new CountDownLatch(4);

		DBConnectionConfig testDb0 = new DBConnectionConfig();
		testDb0.setConnectionUri("memory://local/0");
		LedgerBindingConfig bindingConfig0 = new LedgerBindingConfig();
		AsyncCallback<HashDigest> callback0 = nodeCtx0.startInitCommand(privkey0, encodedPassword, initSetting, csProps,
				csProvider, testDb0, consolePrompter, bindingConfig0, quitLatch);

		DBConnectionConfig testDb1 = new DBConnectionConfig();
		testDb1.setConnectionUri("memory://local/1");
		LedgerBindingConfig bindingConfig1 = new LedgerBindingConfig();
		AsyncCallback<HashDigest> callback1 = nodeCtx1.startInitCommand(privkey1, encodedPassword, initSetting, csProps,
				csProvider, testDb1, consolePrompter, bindingConfig1, quitLatch);

		DBConnectionConfig testDb2 = new DBConnectionConfig();
		testDb2.setConnectionUri("memory://local/2");
		LedgerBindingConfig bindingConfig2 = new LedgerBindingConfig();
		AsyncCallback<HashDigest> callback2 = nodeCtx2.startInitCommand(privkey2, encodedPassword, initSetting, csProps,
				csProvider, testDb2, consolePrompter, bindingConfig2, quitLatch);

		DBConnectionConfig testDb3 = new DBConnectionConfig();
		testDb3.setConnectionUri("memory://local/3");
		LedgerBindingConfig bindingConfig3 = new LedgerBindingConfig();
		AsyncCallback<HashDigest> callback3 = nodeCtx3.startInitCommand(privkey3, encodedPassword, initSetting, csProps,
				csProvider, testDb3, consolePrompter, bindingConfig3, quitLatch);

		HashDigest ledgerHash0 = callback0.waitReturn();
		HashDigest ledgerHash1 = callback1.waitReturn();
		HashDigest ledgerHash2 = callback2.waitReturn();
		HashDigest ledgerHash3 = callback3.waitReturn();

		nodeCtx0.registLedger(ledgerHash0);
		nodeCtx1.registLedger(ledgerHash1);
		nodeCtx2.registLedger(ledgerHash2);
		nodeCtx3.registLedger(ledgerHash3);

		IntegratedContext context = new IntegratedContext();

		Node node0 = new Node(0);
		node0.setConsensusSettings(csProps);
		node0.setLedgerManager(nodeCtx0.getLedgerManager());
		node0.setStorageDB(nodeCtx0.getStorageDB());
		node0.setPartiKeyPair(new AsymmetricKeypair(initSetting.getConsensusParticipant(0).getPubKey(), privkey0));
		node0.setBindingConfig(bindingConfig0);
		context.addNode(node0);

		Node node1 = new Node(1);
		node1.setConsensusSettings(csProps);
		node1.setLedgerManager(nodeCtx1.getLedgerManager());
		node1.setStorageDB(nodeCtx1.getStorageDB());
		node1.setPartiKeyPair(new AsymmetricKeypair(initSetting.getConsensusParticipant(1).getPubKey(), privkey1));
		node1.setBindingConfig(bindingConfig1);
		context.addNode(node1);

		Node node2 = new Node(2);
		node2.setConsensusSettings(csProps);
		node2.setLedgerManager(nodeCtx2.getLedgerManager());
		node2.setStorageDB(nodeCtx2.getStorageDB());
		node2.setPartiKeyPair(new AsymmetricKeypair(initSetting.getConsensusParticipant(2).getPubKey(), privkey2));
		node2.setBindingConfig(bindingConfig2);
		context.addNode(node2);

		Node node3 = new Node(3);
		node3.setConsensusSettings(csProps);
		node3.setLedgerManager(nodeCtx3.getLedgerManager());
		node3.setStorageDB(nodeCtx3.getStorageDB());
		node3.setPartiKeyPair(new AsymmetricKeypair(initSetting.getConsensusParticipant(3).getPubKey(), privkey3));
		node3.setBindingConfig(bindingConfig3);
		context.addNode(node3);

		nodeCtx0.closeServer();
		nodeCtx1.closeServer();
		nodeCtx2.closeServer();
		nodeCtx3.closeServer();

		return context;
	}

	public static LedgerInitProperties loadInitSetting_integration() {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger_init_test_integration.init");
		try (InputStream in = ledgerInitSettingResource.getInputStream()) {
			LedgerInitProperties setting = LedgerInitProperties.resolve(in);
			return setting;
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private static class NodeInitContext {

		private NetworkAddress serverAddress;

		private DBConnectionConfig dbConnConfig;

		private volatile ConfigurableApplicationContext ctx;

		private volatile LedgerInitProcess initProcess;

		private volatile LedgerInitializeWebController controller;

		private volatile LedgerManager ledgerManager;

		private volatile CompositeConnectionFactory db;

		private List<DbConnection> conns = new ArrayList<>();

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

		public NodeInitContext(int id, NetworkAddress serverAddress) {
			this.id = id;
			this.serverAddress = serverAddress;
		}

		public LedgerQuery registLedger(HashDigest ledgerHash) {
			// LedgerManage ledgerManager = ctx.getBean(LedgerManage.class);
			//
			// DbConnectionFactory dbConnFactory = ctx.getBean(DbConnectionFactory.class);
			// DbConnection conn = dbConnFactory.connect(dbConnConfig.getUri(),
			// dbConnConfig.getPassword());

			DbConnection conn = db.connect(dbConnConfig.getUri(), dbConnConfig.getPassword());
			conns.add(conn);
			LedgerQuery ledgerRepo = ledgerManager.register(ledgerHash, conn.getStorageService());
			return ledgerRepo;
		}

		public SignatureDigest createPermissionRequestSignature(int requesterId, PrivKey privKey) {
			return controller.signPermissionRequest(requesterId, privKey);
		}

		public AsyncCallback<HashDigest> startInit(PrivKey privKey, LedgerInitProperties setting,
				ConsensusSettings csProps, ConsensusProvider csProvider, DBConnectionConfig dbConnConfig,
				Prompter prompter, CountDownLatch quitLatch) {

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					doStartServer();

					// NodeWebContext.this.initProcess = ctx.getBean(LedgerInitProcess.class);
					NodeInitContext.this.dbConnConfig = dbConnConfig;
					HashDigest ledgerHash = NodeInitContext.this.initProcess.initialize(id, privKey, setting,
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
				CountDownLatch quitLatch) {
			this.db = new CompositeConnectionFactory();
			this.dbConnConfig = dbConnConfig;

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					LedgerInitCommand initCmd = new LedgerInitCommand();
					HashDigest ledgerHash = initCmd.startInit(id, privKey, base58Pwd, ledgerSetting, dbConnConfig,
							prompter, conf, db);
					NodeInitContext.this.ledgerManager = initCmd.getLedgerManager();
					quitLatch.countDown();
					return ledgerHash;
				}
			};

			return invoker.start();
		}

		public LedgerInitProposal preparePermision(PrivKey privKey, LedgerInitProperties initProps) {
			return controller.prepareLocalPermission(id, privKey, initProps);
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

			ctx = SpringApplication.run(LedgerInitTestConfiguration.class, innerArgs);

			ctx.setId("Node-" + id);
			controller = ctx.getBean(LedgerInitializeWebController.class);
			ledgerManager = ctx.getBean(LedgerManager.class);
			db = ctx.getBean(CompositeConnectionFactory.class);
			initProcess = ctx.getBean(LedgerInitProcess.class);
		}

		public void closeServer() {
			try {
				if (this.ctx != null) {
					this.ctx.close();
					this.ctx = null;
				}
			} catch (Exception e1) {
				// Ignore;
			}
			for (DbConnection conn : conns) {
				try {
					conn.close();
				} catch (Exception e) {
					// Ignore;
				}
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
}
