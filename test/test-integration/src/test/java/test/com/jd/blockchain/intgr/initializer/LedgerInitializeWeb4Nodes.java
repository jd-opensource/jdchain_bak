package test.com.jd.blockchain.intgr.initializer;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.impl.composite.CompositeConnectionFactory;
import com.jd.blockchain.tools.initializer.*;
import com.jd.blockchain.tools.initializer.web.LedgerInitializeWebController;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import test.com.jd.blockchain.intgr.IntegrationBase;
import test.com.jd.blockchain.intgr.LedgerInitConsensusConfig;
import test.com.jd.blockchain.intgr.PresetAnswerPrompter;
import test.com.jd.blockchain.intgr.perf.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class LedgerInitializeWeb4Nodes {

	public static final String PASSWORD = IntegrationBase.PASSWORD;

	public static final String[] PUB_KEYS = IntegrationBase.PUB_KEYS;

	public static final String[] PRIV_KEYS = IntegrationBase.PRIV_KEYS;

	static {
		try {
			// 首先获取当前Resource路径
			ClassPathResource ledgerInitSettingResource = new ClassPathResource("");
			String path = ledgerInitSettingResource.getURL().getPath();
			System.out.println("-----" + path + "-----");
			// 将参数注册进去
			System.setProperty("peer.log", path);
			System.setProperty("init.log", path);
			System.setProperty("gateway.log", path);
			System.setProperty("jdchain.log", path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMQInitByMemWith4Nodes() {
		testInitWith4Nodes(LedgerInitConsensusConfig.mqConfig, LedgerInitConsensusConfig.memConnectionStrings);
	}

	@Test
	public void testMQInitByRedisWith4Nodes() {
		testInitWith4Nodes(LedgerInitConsensusConfig.mqConfig, LedgerInitConsensusConfig.redisConnectionStrings);
	}

	@Test
	public void testBftsmartLedgerInitByMemWith4Nodes() {
		testInitWith4Nodes(LedgerInitConsensusConfig.bftsmartConfig, LedgerInitConsensusConfig.memConnectionStrings);
	}

	@Test
	public void testBftsmartLedgerInitByRedisWith4Nodes() {
		testInitWith4Nodes(LedgerInitConsensusConfig.bftsmartConfig, LedgerInitConsensusConfig.redisConnectionStrings);
	}

	public HashDigest testInitWith4Nodes(LedgerInitConsensusConfig.ConsensusConfig config, String[] dbConns) {
		System.out.println("----------- is daemon=" + Thread.currentThread().isDaemon());

		Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();
		LedgerInitProperties initSetting = loadInitSetting_2();
		Properties props = loadConsensusSetting(config.getConfigPath());
		ConsensusProvider csProvider = LedgerInitConsensusConfig.getConsensusProvider(config.getProvider());
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
		testDb0.setConnectionUri(dbConns[0]);
		AsyncCallback<HashDigest> callback0 = node0.startInit(privkey0, initSetting, testDb0, consolePrompter,
				quitLatch);

		DBConnectionConfig testDb1 = new DBConnectionConfig();
		testDb1.setConnectionUri(dbConns[1]);
		AsyncCallback<HashDigest> callback1 = node1.startInit(privkey1, initSetting, testDb1, consolePrompter,
				quitLatch);

		DBConnectionConfig testDb2 = new DBConnectionConfig();
		testDb2.setConnectionUri(dbConns[2]);
		AsyncCallback<HashDigest> callback2 = node2.startInit(privkey2, initSetting, testDb2, consolePrompter,
				quitLatch);

		DBConnectionConfig testDb03 = new DBConnectionConfig();
		testDb03.setConnectionUri(dbConns[3]);
		AsyncCallback<HashDigest> callback3 = node3.startInit(privkey3, initSetting, testDb03, consolePrompter,
				quitLatch);

		HashDigest ledgerHash0 = callback0.waitReturn();
		HashDigest ledgerHash1 = callback1.waitReturn();
		HashDigest ledgerHash2 = callback2.waitReturn();
		HashDigest ledgerHash3 = callback3.waitReturn();

		assertNotNull(ledgerHash0);
		assertEquals(ledgerHash0, ledgerHash1);
		assertEquals(ledgerHash0, ledgerHash2);
		assertEquals(ledgerHash0, ledgerHash3);

		LedgerQuery ledger0 = node0.registLedger(ledgerHash0);
		LedgerQuery ledger1 = node1.registLedger(ledgerHash1);
		LedgerQuery ledger2 = node2.registLedger(ledgerHash2);
		LedgerQuery ledger3 = node3.registLedger(ledgerHash3);

		assertNotNull(ledger0);
		assertNotNull(ledger1);
		assertNotNull(ledger2);
		assertNotNull(ledger3);

		LedgerBlock genesisBlock = ledger0.getLatestBlock();
		assertEquals(0, genesisBlock.getHeight());
		assertEquals(ledgerHash0, genesisBlock.getHash());

		UserAccountQuery userset0 = ledger0.getUserAccountSet(genesisBlock);

		PubKey pubKey0 = KeyGenUtils.decodePubKey(PUB_KEYS[0]);
		Bytes address0 = AddressEncoding.generateAddress(pubKey0);
		System.out.printf("localNodeAddress0 = %s \r\n", address0.toBase58());
		UserAccount user0_0 = userset0.getAccount(address0);
		assertNotNull(user0_0);

		PubKey pubKey1 = KeyGenUtils.decodePubKey(PUB_KEYS[1]);
		Bytes address1 = AddressEncoding.generateAddress(pubKey1);
		UserAccount user1_0 = userset0.getAccount(address1);
		assertNotNull(user1_0);
		System.out.printf("localNodeAddress1 = %s \r\n", address1.toBase58());

		PubKey pubKey2 = KeyGenUtils.decodePubKey(PUB_KEYS[2]);
		Bytes address2 = AddressEncoding.generateAddress(pubKey2);
		UserAccount user2_0 = userset0.getAccount(address2);
		assertNotNull(user2_0);
		System.out.printf("localNodeAddress2 = %s \r\n", address2.toBase58());

		PubKey pubKey3 = KeyGenUtils.decodePubKey(PUB_KEYS[3]);
		Bytes address3 = AddressEncoding.generateAddress(pubKey3);
		UserAccount user3_0 = userset0.getAccount(address3);
		assertNotNull(user3_0);
		System.out.printf("localNodeAddress3 = %s \r\n", address3.toBase58());

		return ledgerHash0;
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
			DbConnection conn = db.connect(dbConnConfig.getUri());
			LedgerQuery ledgerRepo = ledgerManager.register(ledgerHash, conn.getStorageService());
			return ledgerRepo;
		}

		public AsyncCallback<HashDigest> startInit(PrivKey privKey, LedgerInitProperties setting,
				DBConnectionConfig dbConnConfig, Prompter prompter, CountDownLatch quitLatch) {

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					doStartServer();

					NodeWebContext.this.dbConnConfig = dbConnConfig;
					HashDigest ledgerHash = NodeWebContext.this.initProcess.initialize(id, privKey, setting,
							dbConnConfig, prompter);

					System.out.printf("ledgerHash = %s \r\n", ledgerHash.toBase58());

					quitLatch.countDown();
					return ledgerHash;
				}
			};

			return invoker.start();
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
	}
}
