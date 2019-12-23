package test.com.jd.blockchain.intgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.gateway.GatewayConfigProperties.KeyPairConfig;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.ledger.core.DataAccountQuery;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.IntegratedContext.Node;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeWeb4SingleStepsTest;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeWeb4SingleStepsTest.NodeWebContext;
import test.com.jd.blockchain.intgr.perf.Utils;

public class IntegrationTestDataAccount {

	LedgerInitConsensusConfig.ConsensusConfig config = LedgerInitConsensusConfig.mqConfig;

	public IntegratedContext context = initLedgers(config, LedgerInitConsensusConfig.memConnectionStrings);
	public GatewayTestRunner gateway0;
	public GatewayTestRunner gateway1;

	private class JsonTest {
		String name;

		public JsonTest(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Test
	public void startPeer() {

		// init ledgers of all nodes ;
		Node node0 = context.getNode(0);
		Node node1 = context.getNode(1);
		Node node2 = context.getNode(2);
		Node node3 = context.getNode(3);

		NetworkAddress peerSrvAddr0 = new NetworkAddress("127.0.0.1", 14200);
		PeerTestRunner peer0 = new PeerTestRunner(peerSrvAddr0, node0.getBindingConfig(), node0.getStorageDB(), null);

		NetworkAddress peerSrvAddr1 = new NetworkAddress("127.0.0.1", 14210);
		PeerTestRunner peer1 = new PeerTestRunner(peerSrvAddr1, node1.getBindingConfig(), node1.getStorageDB(), null);

		NetworkAddress peerSrvAddr2 = new NetworkAddress("127.0.0.1", 14220);
		PeerTestRunner peer2 = new PeerTestRunner(peerSrvAddr2, node2.getBindingConfig(), node2.getStorageDB(), null);

		NetworkAddress peerSrvAddr3 = new NetworkAddress("127.0.0.1", 14230);
		PeerTestRunner peer3 = new PeerTestRunner(peerSrvAddr3, node3.getBindingConfig(), node3.getStorageDB(), null);

		AsyncCallback<Object> peerStarting0 = peer0.start();
		AsyncCallback<Object> peerStarting1 = peer1.start();
		AsyncCallback<Object> peerStarting2 = peer2.start();
		AsyncCallback<Object> peerStarting3 = peer3.start();

		peerStarting0.waitReturn();
		peerStarting1.waitReturn();
		peerStarting2.waitReturn();
		peerStarting3.waitReturn();

		String encodedBase58Pwd = KeyGenUtils.encodePasswordAsBase58(LedgerInitializeWeb4SingleStepsTest.PASSWORD);

		KeyPairConfig gwkey0 = new KeyPairConfig();
		gwkey0.setPubKeyValue(LedgerInitializeWeb4SingleStepsTest.PUB_KEYS[0]);
		gwkey0.setPrivKeyValue(LedgerInitializeWeb4SingleStepsTest.PRIV_KEYS[0]);
		gwkey0.setPrivKeyPassword(encodedBase58Pwd);
		gateway0 = new GatewayTestRunner("127.0.0.1", 13300, gwkey0, peerSrvAddr0);

		KeyPairConfig gwkey1 = new KeyPairConfig();
		gwkey1.setPubKeyValue(LedgerInitializeWeb4SingleStepsTest.PUB_KEYS[1]);
		gwkey1.setPrivKeyValue(LedgerInitializeWeb4SingleStepsTest.PRIV_KEYS[1]);
		gwkey1.setPrivKeyPassword(encodedBase58Pwd);
		gateway1 = new GatewayTestRunner("127.0.0.1", 13310, gwkey1, peerSrvAddr1);

		AsyncCallback<Object> gwStarting0 = gateway0.start();
		AsyncCallback<Object> gwStarting1 = gateway1.start();

		gwStarting0.waitReturn();
		gwStarting1.waitReturn();

		// 执行测试用例之前，校验每个节点的一致性；
		testConsistencyAmongNodes(context);

		// temp test add
		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeWeb4SingleStepsTest.PRIV_KEYS[0],
				LedgerInitializeWeb4SingleStepsTest.PASSWORD);
		PubKey pubKey0 = KeyGenUtils.decodePubKey(LedgerInitializeWeb4SingleStepsTest.PUB_KEYS[0]);
		AsymmetricKeypair adminKey = new AsymmetricKeypair(pubKey0, privkey0);

		// regist data account
		Bytes dataAddr = registDataAccount(gateway0, adminKey, context);

		// add kv ops to data account
		testAddKvOpToDataAccount(gateway0, adminKey, context, dataAddr);

		// 执行测试用例之后，校验每个节点的一致性；
		testConsistencyAmongNodes(context);
	}

	private Bytes registDataAccount(GatewayTestRunner gateway, AsymmetricKeypair adminKey, IntegratedContext context) {
		// 连接网关；
		GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway.getServiceAddress());
		BlockchainService blockchainService = gwsrvFact.getBlockchainService();
		HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHashs[0]);

		// BlockchainKeyPair user = BlockchainKeyGenerator.getInstance().generate();
		BlockchainKeypair data = BlockchainKeyGenerator.getInstance().generate();

		// regist user account
		// txTpl.users().register(user.getIdentity());

		// //regist data account
		txTpl.dataAccounts().register(data.getIdentity());

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		try {
			Thread.sleep(6000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data.getAddress();

	}

	// 通过调用SDK->GATEWAY,测试一个区块包含多个交易时的写入情况，并验证写入结果；
	private void testAddKvOpToDataAccount(GatewayTestRunner gateway, AsymmetricKeypair adminKey,
			IntegratedContext context, Bytes dataAddr) {

		GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway.getServiceAddress());
		BlockchainService blockchainService = gwsrvFact.getBlockchainService();
		HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();

		LedgerManager ledgerManager = context.getNode(0).getLedgerManager();

		DbConnection memoryBasedDb = context.getNode(0).getStorageDB()
				.connect(LedgerInitConsensusConfig.memConnectionStrings[0]);

		LedgerQuery ledgerRepository = ledgerManager.register(ledgerHashs[0], memoryBasedDb.getStorageService());

		DataAccountQuery dataAccountSet = ledgerRepository.getDataAccountSet(ledgerRepository.retrieveLatestBlock());

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHashs[0]);

		long currentTime = System.currentTimeMillis();
		byte[] time = BytesUtils.toBytes(currentTime);

		JsonTest jsonTest = new JsonTest("Jack");
		//
		// //add kv ops for data account: Bytes, string, long, json string
		DataAccountKVSetOperation dataKvsetOP = txTpl.dataAccount(dataAddr).setText("A", "Value_A_0", -1)
				.setText("B", "Value_B_0", -1).setInt64("C", currentTime, -1).setText("D", JSON.toJSONString(jsonTest), -1)
				.getOperation();

		// 签名；
		PreparedTransaction ptx = txTpl.prepare();
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		try {
			Thread.sleep(6000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHashs[0], dataAddr.toBase58(), "A", "B",
				"C", "D");
		for (int i = 0; i < kvDataEntries.length; i++) {
			Object result = kvDataEntries[i].getValue();
			System.out.println("result = " + result);
		}

		return;
	}

	public void testConsistencyAmongNodes(IntegratedContext context) {
		int[] ids = context.getNodeIds();
		Node[] nodes = new Node[ids.length];
		LedgerQuery[] ledgers = new LedgerQuery[ids.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = context.getNode(ids[i]);
			HashDigest ledgerHash = nodes[i].getLedgerManager().getLedgerHashs()[0];
			ledgers[i] = nodes[i].getLedgerManager().getLedger(ledgerHash);
		}
		LedgerQuery ledger0 = ledgers[0];
		LedgerBlock latestBlock0 = ledger0.retrieveLatestBlock();
		for (int i = 1; i < ledgers.length; i++) {
			LedgerQuery otherLedger = ledgers[i];
			LedgerBlock otherLatestBlock = otherLedger.retrieveLatestBlock();
			assertEquals(ledger0.getHash(), otherLedger.getHash());
			assertEquals(ledger0.getLatestBlockHeight(), otherLedger.getLatestBlockHeight());
			assertEquals(ledger0.getLatestBlockHash(), otherLedger.getLatestBlockHash());

			assertEquals(latestBlock0.getHeight(), otherLatestBlock.getHeight());
			assertEquals(latestBlock0.getHash(), otherLatestBlock.getHash());
			assertEquals(latestBlock0.getAdminAccountHash(), otherLatestBlock.getAdminAccountHash());
			assertEquals(latestBlock0.getTransactionSetHash(), otherLatestBlock.getTransactionSetHash());
			assertEquals(latestBlock0.getUserAccountSetHash(), otherLatestBlock.getUserAccountSetHash());
			assertEquals(latestBlock0.getDataAccountSetHash(), otherLatestBlock.getDataAccountSetHash());
			assertEquals(latestBlock0.getContractAccountSetHash(), otherLatestBlock.getContractAccountSetHash());
			assertEquals(latestBlock0.getPreviousHash(), otherLatestBlock.getPreviousHash());
		}
	}

	private IntegratedContext initLedgers(LedgerInitConsensusConfig.ConsensusConfig config, String[] dbConns) {
		Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();
		LedgerInitProperties initSetting = loadInitSetting_integration();
		Properties props = LedgerInitializeWeb4SingleStepsTest.loadConsensusSetting(config.getConfigPath());
		ConsensusProvider csProvider = LedgerInitConsensusConfig.getConsensusProvider(config.getProvider());
		ConsensusSettings csProps = csProvider.getSettingsFactory()
				.getConsensusSettingsBuilder()
				.createSettings(props, Utils.loadParticipantNodes());

		// 启动服务器；
		NetworkAddress initAddr0 = initSetting.getConsensusParticipant(0).getInitializerAddress();
		NodeWebContext nodeCtx0 = new NodeWebContext(0, initAddr0);

		NetworkAddress initAddr1 = initSetting.getConsensusParticipant(1).getInitializerAddress();
		NodeWebContext nodeCtx1 = new NodeWebContext(1, initAddr1);

		NetworkAddress initAddr2 = initSetting.getConsensusParticipant(2).getInitializerAddress();
		NodeWebContext nodeCtx2 = new NodeWebContext(2, initAddr2);

		NetworkAddress initAddr3 = initSetting.getConsensusParticipant(3).getInitializerAddress();
		NodeWebContext nodeCtx3 = new NodeWebContext(3, initAddr3);

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeWeb4SingleStepsTest.PRIV_KEYS[0],
				LedgerInitializeWeb4SingleStepsTest.PASSWORD);
		PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeWeb4SingleStepsTest.PRIV_KEYS[1],
				LedgerInitializeWeb4SingleStepsTest.PASSWORD);
		PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeWeb4SingleStepsTest.PRIV_KEYS[2],
				LedgerInitializeWeb4SingleStepsTest.PASSWORD);
		PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeWeb4SingleStepsTest.PRIV_KEYS[3],
				LedgerInitializeWeb4SingleStepsTest.PASSWORD);

		String encodedPassword = KeyGenUtils.encodePasswordAsBase58(LedgerInitializeWeb4SingleStepsTest.PASSWORD);

		CountDownLatch quitLatch = new CountDownLatch(4);

		DBConnectionConfig testDb0 = new DBConnectionConfig();
		testDb0.setConnectionUri(dbConns[0]);
		LedgerBindingConfig bindingConfig0 = new LedgerBindingConfig();
		AsyncCallback<HashDigest> callback0 = nodeCtx0.startInitCommand(privkey0, encodedPassword, initSetting, testDb0,
				consolePrompter, bindingConfig0, quitLatch);

		DBConnectionConfig testDb1 = new DBConnectionConfig();
		testDb1.setConnectionUri(dbConns[1]);
		LedgerBindingConfig bindingConfig1 = new LedgerBindingConfig();
		AsyncCallback<HashDigest> callback1 = nodeCtx1.startInitCommand(privkey1, encodedPassword, initSetting, testDb1,
				consolePrompter, bindingConfig1, quitLatch);

		DBConnectionConfig testDb2 = new DBConnectionConfig();
		testDb2.setConnectionUri(dbConns[2]);
		LedgerBindingConfig bindingConfig2 = new LedgerBindingConfig();
		AsyncCallback<HashDigest> callback2 = nodeCtx2.startInitCommand(privkey2, encodedPassword, initSetting, testDb2,
				consolePrompter, bindingConfig2, quitLatch);

		DBConnectionConfig testDb3 = new DBConnectionConfig();
		testDb3.setConnectionUri(dbConns[3]);
		LedgerBindingConfig bindingConfig3 = new LedgerBindingConfig();
		AsyncCallback<HashDigest> callback3 = nodeCtx3.startInitCommand(privkey3, encodedPassword, initSetting, testDb3,
				consolePrompter, bindingConfig3, quitLatch);

		HashDigest ledgerHash0 = callback0.waitReturn();
		HashDigest ledgerHash1 = callback1.waitReturn();
		HashDigest ledgerHash2 = callback2.waitReturn();
		HashDigest ledgerHash3 = callback3.waitReturn();

		assertNotNull(ledgerHash0);
		assertEquals(ledgerHash0, ledgerHash1);
		assertEquals(ledgerHash0, ledgerHash2);
		assertEquals(ledgerHash0, ledgerHash3);

		LedgerQuery ledger0 = nodeCtx0.registLedger(ledgerHash0);
		LedgerQuery ledger1 = nodeCtx1.registLedger(ledgerHash1);
		LedgerQuery ledger2 = nodeCtx2.registLedger(ledgerHash2);
		LedgerQuery ledger3 = nodeCtx3.registLedger(ledgerHash3);

		assertNotNull(ledger0);
		assertNotNull(ledger1);
		assertNotNull(ledger2);
		assertNotNull(ledger3);

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
}
