package test.com.jd.blockchain.intgr;

import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.bftsmart.BftsmartConsensusSettings;
import com.jd.blockchain.consensus.mq.settings.MsgQueueConsensusSettings;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.gateway.GatewayConfigProperties.KeyPairConfig;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.FileUtils;

import org.junit.Test;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeTest;
import test.com.jd.blockchain.intgr.initializer.LedgerInitializeWeb4Nodes;

import java.util.HashMap;
import java.util.Map;

import static test.com.jd.blockchain.intgr.IntegrationBase.*;

public class IntegrationTest4MQ {

	private static final boolean isRegisterUser = true;

	private static final boolean isRegisterDataAccount = true;

	private static final boolean isRegisterParticipant = true;

	private static final boolean isParticipantStateUpdate = true;

	private static final boolean isWriteKv = true;
	private static final boolean isContract = false;

	private static final boolean isOnline = true;
	private static final int online_time = 60000*60;

	private static final String DB_TYPE_MEM = "mem";

	private static final String DB_TYPE_REDIS = "redis";

	private static final String DB_TYPE_ROCKSDB = "rocksdb";

	private static final String DATA_RETRIEVAL_URL= "http://192.168.151.39:10001";

	public static final  String MQ_PROVIDER = "com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider";

	@Test
	public void test4Memory() {
		test(LedgerInitConsensusConfig.mqProvider, DB_TYPE_MEM, LedgerInitConsensusConfig.memConnectionStrings);
	}

	@Test
	public void test4Redis() {
//		test(LedgerInitConsensusConfig.mqProvider, DB_TYPE_REDIS, LedgerInitConsensusConfig.redisConnectionStrings);
	}

	@Test
	public void test4Rocksdb() {
		test(LedgerInitConsensusConfig.mqProvider, DB_TYPE_ROCKSDB, LedgerInitConsensusConfig.rocksdbConnectionStrings);
	}

	public void test(String[] providers, String dbType, String[] dbConnections) {

		// 内存账本初始化
		HashDigest ledgerHash = initLedger(dbType, dbConnections);

		// 启动Peer节点
		PeerTestRunner[] peerNodes = peerNodeStart(ledgerHash, dbType);

		DbConnectionFactory dbConnectionFactory0 = peerNodes[0].getDBConnectionFactory();
		DbConnectionFactory dbConnectionFactory1 = peerNodes[1].getDBConnectionFactory();
		DbConnectionFactory dbConnectionFactory2 = peerNodes[2].getDBConnectionFactory();
		DbConnectionFactory dbConnectionFactory3 = peerNodes[3].getDBConnectionFactory();

		String encodedBase58Pwd = KeyGenUtils.encodePasswordAsBase58(LedgerInitializeTest.PASSWORD);

		KeyPairConfig gwkey0 = new KeyPairConfig();
		gwkey0.setPubKeyValue(IntegrationBase.PUB_KEYS[0]);
		gwkey0.setPrivKeyValue(IntegrationBase.PRIV_KEYS[0]);
		gwkey0.setPrivKeyPassword(encodedBase58Pwd);

		Map<String,Object> otherMap = new HashMap<String,Object>();
		otherMap.put("DATA_RETRIEVAL_URL",DATA_RETRIEVAL_URL);
		GatewayTestRunner gateway = new GatewayTestRunner("127.0.0.1", 11000, gwkey0,
				peerNodes[0].getServiceAddress(), providers, otherMap);

		AsyncCallback<Object> gwStarting = gateway.start();

		gwStarting.waitReturn();

		// 执行测试用例之前，校验每个节点的一致性；
		LedgerQuery[] ledgers = buildLedgers(new LedgerBindingConfig[]{
				peerNodes[0].getLedgerBindingConfig(),
				peerNodes[1].getLedgerBindingConfig(),
				peerNodes[2].getLedgerBindingConfig(),
				peerNodes[3].getLedgerBindingConfig(),
                },
				new DbConnectionFactory[]{
						dbConnectionFactory0,
						dbConnectionFactory1,
						dbConnectionFactory2,
						dbConnectionFactory3});

		IntegrationBase.testConsistencyAmongNodes(ledgers);

		LedgerQuery ledgerRepository = ledgers[0];

		GatewayServiceFactory gwsrvFact = GatewayServiceFactory.connect(gateway.getServiceAddress());

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(IntegrationBase.PRIV_KEYS[0], IntegrationBase.PASSWORD);

		PubKey pubKey0 = KeyGenUtils.decodePubKey(IntegrationBase.PUB_KEYS[0]);

		AsymmetricKeypair adminKey = new AsymmetricKeypair(pubKey0, privkey0);

		BlockchainService blockchainService = gwsrvFact.getBlockchainService();

		if (isRegisterUser) {
			IntegrationBase.KeyPairResponse userResponse = IntegrationBase.testSDK_RegisterUser(adminKey, ledgerHash, blockchainService);

			validKeyPair(userResponse, ledgerRepository, IntegrationBase.KeyPairType.USER);
		}

		if (isRegisterDataAccount) {
			IntegrationBase.KeyPairResponse dataAccountResponse = IntegrationBase.testSDK_RegisterDataAccount(adminKey, ledgerHash, blockchainService);

			validKeyPair(dataAccountResponse, ledgerRepository, IntegrationBase.KeyPairType.DATAACCOUNT);

			if (isWriteKv) {
				BlockchainKeypair da = dataAccountResponse.keyPair;
				IntegrationBase.KvResponse kvResponse = IntegrationBase.testSDK_InsertData(adminKey, ledgerHash, blockchainService, da.getAddress());
				validKvWrite(kvResponse, ledgerRepository, blockchainService);
			}
		}

		if(isContract){
			IntegrationBase integrationBase = new IntegrationBase();
			integrationBase.testSDK_Contract(adminKey, ledgerHash, blockchainService,ledgerRepository);
		}

		long participantCount = ledgerRepository.getAdminInfo(ledgerRepository.retrieveLatestBlock()).getParticipantCount();

		long userCount = ledgerRepository.getUserAccountSet(ledgerRepository.retrieveLatestBlock()).getTotal();

		System.out.printf("before add participant: participantCount = %d, userCount = %d\r\n", (int)participantCount, (int)userCount);

		IntegrationBase.KeyPairResponse participantResponse;
		if (isRegisterParticipant) {
			participantResponse = IntegrationBase.testSDK_RegisterParticipant(adminKey, ledgerHash, blockchainService);
		}

		participantCount = ledgerRepository.getAdminInfo(ledgerRepository.retrieveLatestBlock()).getParticipantCount();

		userCount = ledgerRepository.getUserAccountSet(ledgerRepository.retrieveLatestBlock()).getTotal();

		System.out.printf("after add participant: participantCount = %d, userCount = %d\r\n", (int)participantCount, (int)userCount);

		MsgQueueConsensusSettings consensusSettings = (MsgQueueConsensusSettings) ConsensusProviders.getProvider(MQ_PROVIDER).getSettingsFactory().getConsensusSettingsEncoder().decode(ledgerRepository.getAdminInfo().getSettings().getConsensusSetting().toBytes());

		System.out.printf("update participant state before ,old consensus env node num = %d\r\n", consensusSettings.getNodes().length);

		for (int i = 0; i < participantCount; i++) {
			System.out.printf("part%d state = %d\r\n",i, ledgerRepository.getAdminInfo(ledgerRepository.retrieveLatestBlock()).getParticipants()[i].getParticipantNodeState().CODE);
		}

		if (isParticipantStateUpdate) {
			IntegrationBase.testSDK_UpdateParticipantState(adminKey, new BlockchainKeypair(participantResponse.getKeyPair().getPubKey(), participantResponse.getKeyPair().getPrivKey()), ledgerHash, blockchainService);
		}

		BftsmartConsensusSettings consensusSettingsNew = (BftsmartConsensusSettings) ConsensusProviders.getProvider(MQ_PROVIDER).getSettingsFactory().getConsensusSettingsEncoder().decode(ledgerRepository.getAdminInfo(ledgerRepository.retrieveLatestBlock()).getSettings().getConsensusSetting().toBytes());

		System.out.printf("update participant state after ,new consensus env node num = %d\r\n", consensusSettingsNew.getNodes().length);

		for (int i = 0; i < participantCount; i++) {
			System.out.printf("part%d state = %d\r\n",i, ledgerRepository.getAdminInfo(ledgerRepository.retrieveLatestBlock()).getParticipants()[i].getParticipantNodeState().CODE);
		}

		IntegrationBase.testConsistencyAmongNodes(ledgers);

		if(isOnline){
			try {
				Thread.sleep(online_time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private HashDigest initLedger(String dbType, String[] dbConnections) {
		if (dbType.equalsIgnoreCase(DB_TYPE_ROCKSDB)) {
			// rocksdb 需要先删除文件
			for (String dbDir : LedgerInitConsensusConfig.rocksdbDirStrings) {
				try {
					FileUtils.deleteFile(dbDir);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		LedgerInitializeWeb4Nodes ledgerInit = new LedgerInitializeWeb4Nodes();
		HashDigest ledgerHash = ledgerInit.testInitWith4Nodes(LedgerInitConsensusConfig.mqConfig, dbConnections);
		System.out.printf("LedgerHash = %s \r\n", ledgerHash.toBase58());
		return ledgerHash;
	}
}
