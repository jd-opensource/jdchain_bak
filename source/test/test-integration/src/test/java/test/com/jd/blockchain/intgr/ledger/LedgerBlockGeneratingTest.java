package test.com.jd.blockchain.intgr.ledger;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.core.DefaultOperationHandleRegisteration;
import com.jd.blockchain.ledger.core.LedgerDataQuery;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.TransactionBatchProcessor;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.LedgerInitConsensusService;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.PresetAnswerPrompter;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeTest;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeTest.NodeContext;
import test.com.jd.blockchain.intgr.perf.LedgerPerformanceTest;
import test.com.jd.blockchain.intgr.perf.Utils;

public class LedgerBlockGeneratingTest {

	@Test
	public void testBlocksGenerating() {
		// 初始化，并获取其中一个节点的账本，单独进行性能测试；
		NodeContext node = initLedgers(false)[0];

		LedgerManager ledgerManager = node.getLedgerManager();
		HashDigest ledgerHash = ledgerManager.getLedgerHashs()[0];

		DefaultOperationHandleRegisteration opHandler = new DefaultOperationHandleRegisteration();

		test(ledgerHash, node.getPartiKey(), ledgerManager, opHandler, 1000, 5);
	}

	private static void test(HashDigest ledgerHash, AsymmetricKeypair adminKey, LedgerManager ledgerManager,
			DefaultOperationHandleRegisteration opHandler, int batchSize, int batchCount) {
		LedgerRepository ledger = ledgerManager.getLedger(ledgerHash);
		long height = ledger.getLatestBlockHeight();
		assertEquals(0L, height);

		ConsoleUtils.info("\r\n\r\n================= 准备测试交易 [注册用户] =================");
		int totalCount = batchSize * batchCount;
		List<TransactionRequest> txList = prepareUserRegisterRequests(ledgerHash, totalCount, adminKey);

		for (int i = 0; i < batchCount; i++) {
			LedgerBlock latestBlock = ledger.getLatestBlock();
			assertEquals(height + i, latestBlock.getHeight());

			LedgerDataQuery previousDataSet = ledger.getLedgerData(latestBlock);
			ConsoleUtils.info("------ 开始执行交易, 即将生成区块[%s] ------", (latestBlock.getHeight() + 1));
			long startTs = System.currentTimeMillis();

			LedgerEditor newEditor = ledger.createNextBlock();
			TransactionBatchProcessor txProc = new TransactionBatchProcessor(
					LedgerPerformanceTest.DEFAULT_SECURITY_MANAGER, newEditor, ledger, opHandler);

			testTxExec(txList, i * batchSize, batchSize, txProc);

			long elapsedTs = System.currentTimeMillis() - startTs;

			ConsoleUtils.info("新区块已生成! 交易数=%s; 总耗时= %s ms; TPS=%.2f", batchSize, elapsedTs,
					(batchSize * 1000.00D / elapsedTs));

		}

	}

	private static void testTxExec(List<TransactionRequest> txList, int from, int count,
			TransactionBatchProcessor txProc) {
		for (int i = 0; i < count; i++) {
			txProc.schedule(txList.get(from + i));
		}
		TransactionBatchResultHandle handle = txProc.prepare();
		handle.commit();
	}

	private static List<TransactionRequest> prepareUserRegisterRequests(HashDigest ledgerHash, int count,
			AsymmetricKeypair adminKey) {
		List<TransactionRequest> txList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			TxBuilder txbuilder = new TxBuilder(ledgerHash);
			BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
			txbuilder.users().register(userKey.getIdentity());
			TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
			reqBuilder.signAsEndpoint(adminKey);
			txList.add(reqBuilder.buildRequest());
		}
		return txList;
	}

	public static ConsensusProvider getConsensusProvider() {
		return ConsensusProviders.getProvider("com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider");
	}

	public static NodeContext[] initLedgers(boolean optimized) {
		Map<NetworkAddress, LedgerInitConsensusService> serviceRegisterMap = new ConcurrentHashMap<>();

		Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();
		LedgerInitProperties initSetting = loadInitSetting();
		Properties props = loadConsensusSetting();
		ConsensusProvider csProvider = getConsensusProvider();
		ConsensusSettings csProps = csProvider.getSettingsFactory().getConsensusSettingsBuilder().createSettings(props,
				Utils.loadParticipantNodes());

		NodeContext node0 = new NodeContext(initSetting.getConsensusParticipant(0).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node1 = new NodeContext(initSetting.getConsensusParticipant(1).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node2 = new NodeContext(initSetting.getConsensusParticipant(2).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node3 = new NodeContext(initSetting.getConsensusParticipant(3).getInitializerAddress(),
				serviceRegisterMap);

		String[] memConns = new String[] { "memory://local/0", "memory://local/1", "memory://local/2",
				"memory://local/3" };

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeTest.PRIV_KEYS[0],
				LedgerInitializeTest.PASSWORD);
		DBConnectionConfig testDb0 = new DBConnectionConfig();
		testDb0.setConnectionUri(memConns[0]);
		AsyncCallback<HashDigest> callback0 = node0.startInit(0, privkey0, initSetting, testDb0, consolePrompter,
				!optimized);

		PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeTest.PRIV_KEYS[1],
				LedgerInitializeTest.PASSWORD);
		DBConnectionConfig testDb1 = new DBConnectionConfig();
		testDb1.setConnectionUri(memConns[1]);
		AsyncCallback<HashDigest> callback1 = node1.startInit(1, privkey1, initSetting, testDb1, consolePrompter,
				!optimized);

		PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeTest.PRIV_KEYS[2],
				LedgerInitializeTest.PASSWORD);
		DBConnectionConfig testDb2 = new DBConnectionConfig();
		testDb2.setConnectionUri(memConns[2]);
		AsyncCallback<HashDigest> callback2 = node2.startInit(2, privkey2, initSetting, testDb2, consolePrompter,
				!optimized);

		PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(LedgerInitializeTest.PRIV_KEYS[3],
				LedgerInitializeTest.PASSWORD);
		DBConnectionConfig testDb03 = new DBConnectionConfig();
		testDb03.setConnectionUri(memConns[3]);
		AsyncCallback<HashDigest> callback3 = node3.startInit(3, privkey3, initSetting, testDb03, consolePrompter,
				!optimized);

		HashDigest ledgerHash0 = callback0.waitReturn();
		HashDigest ledgerHash1 = callback1.waitReturn();
		HashDigest ledgerHash2 = callback2.waitReturn();
		HashDigest ledgerHash3 = callback3.waitReturn();

		node0.registLedger(ledgerHash0, memConns[0]);
		node1.registLedger(ledgerHash1, memConns[1]);
		node2.registLedger(ledgerHash2, memConns[2]);
		node3.registLedger(ledgerHash3, memConns[3]);

		return new NodeContext[] { node0, node1, node2, node3 };
	}

	public static LedgerInitProperties loadInitSetting() {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger_init_test_web2.init");
		try (InputStream in = ledgerInitSettingResource.getInputStream()) {
			LedgerInitProperties setting = LedgerInitProperties.resolve(in);
			return setting;
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static Properties loadConsensusSetting() {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("bftsmart.config");
		try (InputStream in = ledgerInitSettingResource.getInputStream()) {
			return FileUtils.readProperties(in);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
