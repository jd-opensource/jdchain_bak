package test.com.jd.blockchain.intgr.perf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.DoubleStream;

import com.jd.blockchain.ledger.*;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerSecurityException;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.core.DefaultOperationHandleRegisteration;
import com.jd.blockchain.ledger.core.LedgerDataQuery;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerSecurityManager;
import com.jd.blockchain.ledger.core.MultiIDsPolicy;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionBatchProcessor;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.storage.service.impl.redis.JedisConnection;
import com.jd.blockchain.storage.service.impl.redis.RedisConnectionFactory;
import com.jd.blockchain.storage.service.impl.redis.RedisStorageService;
import com.jd.blockchain.storage.service.impl.rocksdb.RocksDBConnectionFactory;
import com.jd.blockchain.storage.service.utils.MemoryDBConnFactory;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.LedgerInitConsensusService;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.ArgumentSet;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.PresetAnswerPrompter;
import test.com.jd.blockchain.intgr.perf.Utils.NodeContext;

//import com.jd.blockchain.storage.service.utils.MemoryBasedDb;

/**
 * 账本性能测试； <br>
 * 
 * 可用的参数：<br>
 * -o: 优化模式执行；此时merkle树在加载时不做检查；
 * <p>
 * 
 * -redis: 基于 redis 数据库进行测试；需要预先启动redis数据库，4个节点分别连接到本机 redis://127.0.0.1 的端口为
 * 6079、6179、6279、6379 的 4 个数据库实例；
 * <p>
 * 
 * -rocksdb: 基于 rocksDB 进行测试；不需要预先配置数据库，4个节点连接到当前路径下的4个RocksDB数据库目录：
 * rocksdb0.db、rocksdb1.db、rocksdb2.db、rocksdb3.db；
 * <p>
 * 
 * -silent: 采用静默模式启动测试用例；否则会在准备就绪后等待输入任意键才正式开始测试，并在完成测试后等待输入任意键才退出；
 * 
 * 
 * @author huanghaiquan
 *
 */
public class LedgerPerformanceTest {
	static {
		DataContractRegistry.register(LedgerInitOperation.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
		DataContractRegistry.register(DataAccountKVSetOperation.class);
		DataContractRegistry.register(ParticipantRegisterOperation.class);
		DataContractRegistry.register(ParticipantStateUpdateOperation.class);
	}

	public static final LedgerSecurityManager DEFAULT_SECURITY_MANAGER = new FreedomLedgerSecurityManager();

	public static void test(String[] args) {
		NodeContext[] nodes = null;
		try {
			boolean usertest = ArgumentSet.hasOption(args, "-usertest");
			boolean optimized = ArgumentSet.hasOption(args, "-o");
			boolean useRedis = ArgumentSet.hasOption(args, "-redis");
			boolean useRocksDB = ArgumentSet.hasOption(args, "-rocksdb");
			boolean silent = ArgumentSet.hasOption(args, "-silent");
			boolean contract = ArgumentSet.hasOption(args, "-contract");
			boolean mqConsensus = ArgumentSet.hasOption(args, "-mq");
			DBType dbType = DBType.DEFAULT;
			if (useRedis) {
				dbType = DBType.REDIS;
			}
			if (useRocksDB) {
				dbType = DBType.ROCKSDB;
			}

			CryptoAlgorithm hashAlg = ArgumentSet.hasOption(args, "-160") ? Crypto.getAlgorithm("RIPEMD160")
					: Crypto.getAlgorithm("SHA256");
			System.out.println(
					String.format("----- LedgerPerformanceTest [HashAlgorithm=%s][DBType=%s] ----", hashAlg, dbType));
			// 初始化，并获取其中一个节点的账本，单独进行性能测试；
			String provider = "com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider";
			String config = "bftsmart.config";
			if (mqConsensus) {
				provider = "com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider";
				config = "mq.config";
			}
			nodes = initLedgers(optimized, hashAlg, dbType, provider, config);

			NodeContext testNode = nodes[0];
			LedgerManager ledgerManager = testNode.getLedgerManager();
			HashDigest ledgerHash = ledgerManager.getLedgerHashs()[0];

			DefaultOperationHandleRegisteration opHandler = new DefaultOperationHandleRegisteration();

			System.out.println("Ledger is ready!");

			int batchSize = 1000;
			int batchCount = 30;

			if (args.length > 0) {
				if (!args[0].startsWith("-")) {
					batchSize = Integer.parseInt(args[0]);
				}
			}
			if (args.length > 1) {
				if (!args[1].startsWith("-")) {
					batchCount = Integer.parseInt(args[1]);
				}
			}
			if (contract) {
				testContract(ledgerHash, testNode.getPartiKey(), ledgerManager, opHandler, batchSize, batchCount,
						silent);
			}

			if (usertest) {
				testUserRegistering(ledgerHash, testNode.getPartiKey(), ledgerManager, opHandler, batchSize, batchCount,
						silent);
			} else {
				testKVWrite(ledgerHash, testNode.getPartiKey(), ledgerManager, opHandler, batchSize, batchCount,
						silent);
			}
		} catch (Exception e) {
			System.out.println("----------- error [" + e.getMessage() + "]-----------");
			e.printStackTrace();
		} finally {
			if (nodes != null) {
				for (NodeContext node : nodes) {
					node.getStorageDb().close();
				}
			}
		}
	}

	/**
	 * 执行针对“注册用户”的性能测试；
	 * 
	 * @param ledgerHash
	 * @param adminKey
	 * @param ledgerManager
	 * @param opHandler
	 * @param batchSize
	 * @param batchCount
	 * @param silent
	 */
	private static void testUserRegistering(HashDigest ledgerHash, AsymmetricKeypair adminKey,
			LedgerManager ledgerManager, DefaultOperationHandleRegisteration opHandler, int batchSize, int batchCount,
			boolean silent) {
		LedgerRepository ledger = ledgerManager.getLedger(ledgerHash);
		ConsoleUtils.info("\r\n\r\n================= 准备测试交易 [注册用户] =================");

		int totalCount = batchSize * batchCount;
		List<TransactionRequest> txList = prepareUserRegisterRequests(ledgerHash, totalCount, adminKey);

		// 预热；
		ConsoleUtils.info("preheat......");
		int preheatTxBatch = 10;
		int preheatTxBatchSize = 10;
		int preheatTotalTx = preheatTxBatch * preheatTxBatchSize;
		List<TransactionRequest> preheatTxList = prepareUserRegisterRequests(ledgerHash, preheatTotalTx, adminKey);
		execPerformanceTest(preheatTxBatch, preheatTxBatchSize, preheatTxList, ledger, ledgerManager, opHandler, false);
		preheatTxList.clear();
		preheatTxList = null;

		if (!silent) {
			ConsoleUtils.confirm("\r\nTest is ready! Any key to continue...");
		}

		execPerformanceTest(batchCount, batchSize, txList, ledger, ledgerManager, opHandler, true);

		if (!silent) {
			ConsoleUtils.confirm("\r\nTest completed! Any key to quit...");
		}

	}

	/**
	 * 执行针对“写入数据”的性能测试；
	 * 
	 * @param ledgerHash
	 * @param adminKey
	 * @param ledgerManager
	 * @param opHandler
	 * @param batchSize
	 * @param batchCount
	 * @param silent
	 */
	private static void testKVWrite(HashDigest ledgerHash, AsymmetricKeypair adminKey, LedgerManager ledgerManager,
			DefaultOperationHandleRegisteration opHandler, int batchSize, int batchCount, boolean silent) {
		LedgerRepository ledger = ledgerManager.getLedger(ledgerHash);
		ConsoleUtils.info("\r\n\r\n================= 准备测试交易 [写入数据] =================");

		// 创建数据账户；
		BlockchainIdentity[] dataAccounts = new BlockchainIdentity[10];
		List<TransactionRequest> dataAccountRegTxList = prepareDataAccountRegisterRequests(ledgerHash, dataAccounts,
				adminKey, false);
		execPerformanceTest(1, dataAccounts.length, dataAccountRegTxList, ledger, ledgerManager, opHandler, false);

		// 预热；
		ConsoleUtils.info("preheat......");
		int preheatTxBatch = 10;
		int preheatTxBatchSize = 10;
		int preheatTotalTx = preheatTxBatch * preheatTxBatchSize;
		List<TransactionRequest> preheatTxList = prepareDataWriteRequests(ledgerHash, dataAccounts, preheatTotalTx,
				adminKey, false);
		execPerformanceTest(preheatTxBatch, preheatTxBatchSize, preheatTxList, ledger, ledgerManager, opHandler, false);
		preheatTxList.clear();
		preheatTxList = null;

		// 准备正式数据；
		int totalCount = batchSize * batchCount;
		List<TransactionRequest> txList = prepareDataWriteRequests(ledgerHash, dataAccounts, totalCount, adminKey,
				false);

		Prompter consolePrompter = new PresetAnswerPrompter("N");

		if (!silent) {
//			ConsoleUtils.confirm("\r\nTest is ready! Any key to continue...");
			consolePrompter.confirm("testKVWrite", "Test is ready! Any key to continue...");
		}

		execPerformanceTest(batchCount, batchSize, txList, ledger, ledgerManager, opHandler, true);

		if (!silent) {
//			ConsoleUtils.confirm("\r\nTest completed! Any key to quit...");
			consolePrompter.confirm("testKVWrite", "Test completed! Any key to quit...");
		}

	}

	/**
	 * 执行针对“执行合约”的性能测试；
	 *
	 * @param ledgerHash
	 * @param adminKey
	 * @param ledgerManager
	 * @param opHandler
	 * @param batchSize
	 * @param batchCount
	 * @param silent
	 */
	private static void testContract(HashDigest ledgerHash, AsymmetricKeypair adminKey, LedgerManager ledgerManager,
			DefaultOperationHandleRegisteration opHandler, int batchSize, int batchCount, boolean silent) {
		LedgerRepository ledger = ledgerManager.getLedger(ledgerHash);
		ConsoleUtils.info("\r\n\r\n================= 准备测试交易 [执行合约] =================");

		LedgerBlock latestBlock = ledger.getLatestBlock();
		LedgerDataQuery previousDataSet = ledger.getLedgerData(latestBlock);
		LedgerEditor newEditor = ledger.createNextBlock();
		TransactionBatchProcessor txProc = new TransactionBatchProcessor(DEFAULT_SECURITY_MANAGER, newEditor,
				ledger, opHandler);

		// 准备请求
		int totalCount = batchSize * batchCount;
		List<TransactionRequest> contractTxList = prepareContractRequests(ledgerHash, adminKey, totalCount, false,
				txProc);

		Prompter consolePrompter = new PresetAnswerPrompter("N");

		if (!silent) {
//			ConsoleUtils.confirm("\r\nTest is ready! Any key to continue...");
			consolePrompter.confirm("testContract", "Test is ready! Any key to continue...");
		}

		execPerformanceTest(batchCount, batchSize, contractTxList, ledger, ledgerManager, opHandler, true);

		if (!silent) {
//			ConsoleUtils.confirm("\r\nTest completed! Any key to quit...");
			consolePrompter.confirm("testContract", "Test completed! Any key to quit...");
		}

	}

	private static void execPerformanceTest(int batchCount, int batchSize, List<TransactionRequest> txList,
			LedgerRepository ledger, LedgerManager ledgerManager, DefaultOperationHandleRegisteration opHandler,
			boolean statistic) {
		double[] tpss = new double[batchCount];
		long batchStartTs = System.currentTimeMillis();
		for (int i = 0; i < batchCount; i++) {
			LedgerBlock latestBlock = ledger.getLatestBlock();
			LedgerDataQuery previousDataSet = ledger.getLedgerData(latestBlock);
			if (statistic) {
				ConsoleUtils.info("------ 开始执行交易, 即将生成区块[高度：%s] ------", (latestBlock.getHeight() + 1));
			}
			long startTs = System.currentTimeMillis();

			LedgerEditor newEditor = ledger.createNextBlock();
			TransactionBatchProcessor txProc = new TransactionBatchProcessor(DEFAULT_SECURITY_MANAGER, newEditor,
					ledger, opHandler);

			testTxExec(txList, i * batchSize, batchSize, txProc);

			if (statistic) {
				long elapsedTs = System.currentTimeMillis() - startTs;

				tpss[i] = batchSize * 1000.00D / elapsedTs;
				ConsoleUtils.info("新区块已生成! 交易数=%s; 总耗时= %s ms; TPS=%.2f", batchSize, elapsedTs, tpss[i]);
			}
		}
		if (!statistic) {
			return;
		}
		long batchElapsedTs = System.currentTimeMillis() - batchStartTs;
		double globalTPS = batchSize * batchCount * 1000.00D / batchElapsedTs;
		double avgTPS = DoubleStream.of(tpss).average().getAsDouble();
		double maxTPS = DoubleStream.of(tpss).max().getAsDouble();
		double variance = DoubleStream.of(tpss).reduce(0, (r, i) -> r + Math.pow(i - avgTPS, 2)) / tpss.length;
		double stdDeviation = Math.sqrt(variance);

		ConsoleUtils.info("\r\n**********************************************");
		ConsoleUtils.info("区块数：%s; 交易总数：%s; 总体TPS：%.2f;\r\n单区块TPS均值：%.2f；单区块TPS峰值：%.2f；单区块TPS波动(标准差)：%.2f", batchCount,
				batchSize * batchCount, globalTPS, avgTPS, maxTPS, stdDeviation);
		ConsoleUtils.info("**********************************************\r\n");
	}

	private static void testTxExec(List<TransactionRequest> txList, int from, int count,
			TransactionBatchProcessor txProc) {
		for (int i = 0; i < count; i++) {
			txProc.schedule(txList.get(from + i));
		}
		TransactionBatchResultHandle handle = txProc.prepare();
		handle.commit();
	}

	public static List<TransactionRequest> prepareUserRegisterRequests(HashDigest ledgerHash, int count,
			AsymmetricKeypair adminKey) {
		long startTs = System.currentTimeMillis();
		List<TransactionRequest> txList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			TxBuilder txbuilder = new TxBuilder(ledgerHash);
			BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
			txbuilder.users().register(userKey.getIdentity());
			TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
			reqBuilder.signAsEndpoint(adminKey);
			txList.add(reqBuilder.buildRequest());
		}
		long elapsedTs = System.currentTimeMillis() - startTs;
		ConsoleUtils.info(
				"============ Performance of Preparing user registering tx requests...     TOTAL=%s; TPS=%.2f; TIME=%s millis ============",
				count, (count * 1000.0 / elapsedTs), elapsedTs);
		ConsoleUtils.info("=====================================================");
		return txList;
	}

	public static List<TransactionRequest> prepareDataAccountRegisterRequests(HashDigest ledgerHash,
			BlockchainIdentity[] dataAccounts, AsymmetricKeypair adminKey, boolean statistic) {
		int count = dataAccounts.length;
		long startTs = System.currentTimeMillis();
		List<TransactionRequest> txList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			TxBuilder txbuilder = new TxBuilder(ledgerHash);
			BlockchainKeypair dataAccountKey = BlockchainKeyGenerator.getInstance().generate();
			dataAccounts[i] = dataAccountKey.getIdentity();
			txbuilder.dataAccounts().register(dataAccounts[i]);
			TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
			reqBuilder.signAsEndpoint(adminKey);
			txList.add(reqBuilder.buildRequest());
		}
		if (statistic) {
			long elapsedTs = System.currentTimeMillis() - startTs;
			ConsoleUtils.info(
					"============ Performance of Preparing data account registering tx requests...     TOTAL=%s; TPS=%.2f; TIME=%s millis ============",
					count, (count * 1000.0 / elapsedTs), elapsedTs);
			ConsoleUtils.info("=====================================================");
		}
		return txList;
	}

	public static List<TransactionRequest> prepareDataWriteRequests(HashDigest ledgerHash,
			BlockchainIdentity[] dataAccounts, int count, AsymmetricKeypair adminKey, boolean statistic) {
		long startTs = System.currentTimeMillis();
		List<TransactionRequest> txList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			TxBuilder txbuilder = new TxBuilder(ledgerHash);
			// BlockchainKeyPair dataAccountKey =
			// BlockchainKeyGenerator.getInstance().generate();
			BlockchainIdentity targetAccount = dataAccounts[count % dataAccounts.length];
			txbuilder.dataAccount(targetAccount.getAddress()).setText("key-" + startTs + "-" + i, "value-" + i, -1L);
			TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
			reqBuilder.signAsEndpoint(adminKey);
			txList.add(reqBuilder.buildRequest());
		}
		if (statistic) {
			long elapsedTs = System.currentTimeMillis() - startTs;
			ConsoleUtils.info(
					"============ Performance of Preparing data account registering tx requests...     TOTAL=%s; TPS=%.2f; TIME=%s millis ============",
					count, (count * 1000.0 / elapsedTs), elapsedTs);
			ConsoleUtils.info("=====================================================");
		}
		return txList;
	}

	public static ConsensusProvider getConsensusProvider(String provider) {
		return ConsensusProviders.getProvider(provider);
	}

	public static List<TransactionRequest> prepareContractRequests(HashDigest ledgerHash, AsymmetricKeypair adminKey,
			int count, boolean statistic, TransactionBatchProcessor txProc) {

		// deploy contract
		byte[] chainCode;
		try {
//			InputStream input = LedgerPerformanceTest.class.getClassLoader().getResourceAsStream("Setkv.contract");
			InputStream input = LedgerPerformanceTest.class.getClassLoader().getResourceAsStream("example1.jar");
			chainCode = new byte[input.available()];
			input.read(chainCode);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		TxBuilder txbuilder = new TxBuilder(ledgerHash);
		BlockchainKeypair contractAccountKey = BlockchainKeyGenerator.getInstance().generate();
		BlockchainIdentity contractIdentity = contractAccountKey.getIdentity();
		txbuilder.contracts().deploy(contractIdentity, chainCode);

		// create data account
		BlockchainKeypair dataAccountKey = BlockchainKeyGenerator.getInstance().generate();
		BlockchainIdentity dataIdentity = dataAccountKey.getIdentity();

		txbuilder.dataAccounts().register(dataIdentity);

		TransactionRequestBuilder reqBuilder = txbuilder.prepareRequest();
		reqBuilder.signAsEndpoint(adminKey);
		TransactionResponse resp = txProc.schedule(reqBuilder.buildRequest());
		System.out.println(resp.isSuccess());
		TransactionBatchResultHandle handle = txProc.prepare();
		handle.commit();
		try {

			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long startTs = System.currentTimeMillis();
		List<TransactionRequest> txList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			txbuilder = new TxBuilder(ledgerHash);
			String args = dataIdentity.getAddress().toString() + "##" + Integer.toString(i) + "##"
					+ Integer.toString(i);
			txbuilder.contractEvents().send(contractIdentity.getAddress(), "print", BytesDataList.singleText("hello"));
//			txbuilder.contractEvents().send(contractIdentity.getAddress(), "print", args.getBytes());
			reqBuilder = txbuilder.prepareRequest();
			reqBuilder.signAsEndpoint(adminKey);
			txList.add(reqBuilder.buildRequest());
		}
		if (statistic) {
			long elapsedTs = System.currentTimeMillis() - startTs;
			ConsoleUtils.info(
					"============ Performance of Preparing contract execute tx requests...     TOTAL=%s; TPS=%.2f; TIME=%s millis ============",
					count, (count * 1000.0 / elapsedTs), elapsedTs);
			ConsoleUtils.info("=====================================================");
		}
		return txList;
	}

	public static NodeContext[] initLedgers(boolean optimized, CryptoAlgorithm hashAlg, DBType dbType, String provider,
			String config) {
		Map<NetworkAddress, LedgerInitConsensusService> serviceRegisterMap = new ConcurrentHashMap<>();

		Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();
		LedgerInitProperties initSetting = loadInitSetting();
		Properties props = loadConsensusSetting(config);
		ConsensusProvider csProvider = getConsensusProvider(provider);
		ConsensusSettings csProps = csProvider.getSettingsFactory().getConsensusSettingsBuilder().createSettings(props,
				Utils.loadParticipantNodes());

		DBSetting dbsetting0;
		DBSetting dbsetting1;
		DBSetting dbsetting2;
		DBSetting dbsetting3;
		if (dbType == DBType.REDIS) {
			dbsetting0 = DBSetting.createRedisDBSetting("redis://127.0.0.1:6079");
			dbsetting1 = DBSetting.createRedisDBSetting("redis://127.0.0.1:6179");
			dbsetting2 = DBSetting.createRedisDBSetting("redis://127.0.0.1:6279");
			dbsetting3 = DBSetting.createRedisDBSetting("redis://127.0.0.1:6379");

			cleanRedisDB(dbsetting0);
			cleanRedisDB(dbsetting1);
			cleanRedisDB(dbsetting2);
			cleanRedisDB(dbsetting3);
		} else if (dbType == DBType.ROCKSDB) {
			String currDir = FileUtils.getCurrentDir() + File.separator + "rocks.db";

			String dbDir0 = new File(currDir, "rocksdb0.db").getAbsolutePath();
			String dbDir1 = new File(currDir, "rocksdb1.db").getAbsolutePath();
			String dbDir2 = new File(currDir, "rocksdb2.db").getAbsolutePath();
			String dbDir3 = new File(currDir, "rocksdb3.db").getAbsolutePath();

			// clean db first;
			FileUtils.deleteFile(dbDir0);
			FileUtils.deleteFile(dbDir1);
			FileUtils.deleteFile(dbDir2);
			FileUtils.deleteFile(dbDir3);

			dbsetting0 = DBSetting.createRocksDBSetting("rocksdb://" + dbDir0);
			dbsetting1 = DBSetting.createRocksDBSetting("rocksdb://" + dbDir1);
			dbsetting2 = DBSetting.createRocksDBSetting("rocksdb://" + dbDir2);
			dbsetting3 = DBSetting.createRocksDBSetting("rocksdb://" + dbDir3);
		} else {
			dbsetting0 = DBSetting.createMemoryDBSetting("memory://local/0");
			dbsetting1 = DBSetting.createMemoryDBSetting("memory://local/1");
			dbsetting2 = DBSetting.createMemoryDBSetting("memory://local/2");
			dbsetting3 = DBSetting.createMemoryDBSetting("memory://local/3");
		}

		NodeContext node0 = new NodeContext(initSetting.getConsensusParticipant(0).getInitializerAddress(),
				serviceRegisterMap, dbsetting0.connectionFactory);
		NodeContext node1 = new NodeContext(initSetting.getConsensusParticipant(1).getInitializerAddress(),
				serviceRegisterMap, dbsetting1.connectionFactory);
		NodeContext node2 = new NodeContext(initSetting.getConsensusParticipant(2).getInitializerAddress(),
				serviceRegisterMap, dbsetting2.connectionFactory);
		NodeContext node3 = new NodeContext(initSetting.getConsensusParticipant(3).getInitializerAddress(),
				serviceRegisterMap, dbsetting3.connectionFactory);

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(Utils.PRIV_KEYS[0], Utils.PASSWORD);
		AsyncCallback<HashDigest> callback0 = node0.startInit(0, privkey0, initSetting, csProps, csProvider,
				dbsetting0.connectionConfig, consolePrompter, !optimized, hashAlg);

		PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(Utils.PRIV_KEYS[1], Utils.PASSWORD);
		AsyncCallback<HashDigest> callback1 = node1.startInit(1, privkey1, initSetting, csProps, csProvider,
				dbsetting1.connectionConfig, consolePrompter, !optimized, hashAlg);

		PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(Utils.PRIV_KEYS[2], Utils.PASSWORD);
		AsyncCallback<HashDigest> callback2 = node2.startInit(2, privkey2, initSetting, csProps, csProvider,
				dbsetting2.connectionConfig, consolePrompter, !optimized, hashAlg);

		PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(Utils.PRIV_KEYS[3], Utils.PASSWORD);
		AsyncCallback<HashDigest> callback3 = node3.startInit(3, privkey3, initSetting, csProps, csProvider,
				dbsetting3.connectionConfig, consolePrompter, !optimized, hashAlg);

		HashDigest ledgerHash0 = callback0.waitReturn();
		HashDigest ledgerHash1 = callback1.waitReturn();
		HashDigest ledgerHash2 = callback2.waitReturn();
		HashDigest ledgerHash3 = callback3.waitReturn();

		node0.registLedger(ledgerHash0, dbsetting0.connectionConfig);
		node1.registLedger(ledgerHash1, dbsetting1.connectionConfig);
		node2.registLedger(ledgerHash2, dbsetting2.connectionConfig);
		node3.registLedger(ledgerHash3, dbsetting3.connectionConfig);

		return new NodeContext[] { node0, node1, node2, node3 };
	}

	private static void cleanRedisDB(DBSetting dbsetting) {
		RedisConnectionFactory redisConnFactory = (RedisConnectionFactory) dbsetting.connectionFactory;
		JedisConnection dbConn = (JedisConnection) redisConnFactory.connect(dbsetting.connectionConfig.getUri());
		RedisStorageService redisStorage = (RedisStorageService) dbConn.getStorageService();
		redisStorage.clearDB();
		dbConn.close();
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

	public static Properties loadConsensusSetting(String config) {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource(config);
		try (InputStream in = ledgerInitSettingResource.getInputStream()) {
			return FileUtils.readProperties(in);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static class DBSetting {

		private DBConnectionConfig connectionConfig;

		private DbConnectionFactory connectionFactory;

		public static DBSetting createMemoryDBSetting(String uri) {
			DBSetting setting = new DBSetting();
			setting.connectionConfig = new DBConnectionConfig(uri);
			setting.connectionFactory = new MemoryDBConnFactory();
			return setting;
		}

		public static DBSetting createRedisDBSetting(String uri) {
			DBSetting setting = new DBSetting();
			setting.connectionConfig = new DBConnectionConfig(uri);
			setting.connectionFactory = new RedisConnectionFactory();
			return setting;
		}

		public static DBSetting createRocksDBSetting(String uri) {
			DBSetting setting = new DBSetting();
			setting.connectionConfig = new DBConnectionConfig(uri);
			setting.connectionFactory = new RocksDBConnectionFactory();
			return setting;
		}

	}

	private static class FreedomLedgerSecurityManager implements LedgerSecurityManager {

		public static final FreedomLedgerSecurityManager INSTANCE = new FreedomLedgerSecurityManager();

		@Override
		public SecurityPolicy createSecurityPolicy(Set<Bytes> endpoints, Set<Bytes> nodes) {
			return new FreedomSecurityPolicy(endpoints, nodes);
		}

	}

	private static class FreedomSecurityPolicy implements SecurityPolicy {

		private Set<Bytes> endpoints;
		private Set<Bytes> nodes;

		public FreedomSecurityPolicy(Set<Bytes> endpoints, Set<Bytes> nodes) {
			this.endpoints = endpoints;
			this.nodes = nodes;
		}

		@Override
		public Set<Bytes> getEndpoints() {
			return endpoints;
		}

		@Override
		public Set<Bytes> getNodes() {
			return nodes;
		}

		@Override
		public boolean isEndpointEnable(LedgerPermission permission, MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public boolean isEndpointEnable(TransactionPermission permission, MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public boolean isNodeEnable(LedgerPermission permission, MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public boolean isNodeEnable(TransactionPermission permission, MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public void checkEndpointPermission(LedgerPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
		}

		@Override
		public void checkEndpointPermission(TransactionPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
		}

		@Override
		public void checkNodePermission(LedgerPermission permission, MultiIDsPolicy midPolicy) throws LedgerSecurityException {
		}

		@Override
		public void checkNodePermission(TransactionPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
		}

		@Override
		public boolean isEndpointValid(MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public boolean isNodeValid(MultiIDsPolicy midPolicy) {
			return true;
		}

		@Override
		public void checkEndpointValidity(MultiIDsPolicy midPolicy) throws LedgerSecurityException {
		}

		@Override
		public void checkNodeValidity(MultiIDsPolicy midPolicy) throws LedgerSecurityException {
		}

	}
}
