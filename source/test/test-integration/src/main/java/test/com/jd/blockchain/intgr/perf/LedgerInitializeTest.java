package test.com.jd.blockchain.intgr.perf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.storage.service.utils.MemoryDBConnFactory;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.PrivKey;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitPermission;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.ledger.core.UserAccountSet;
import com.jd.blockchain.ledger.core.impl.LedgerManager;
//import com.jd.blockchain.storage.service.utils.MemoryBasedDb;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerInitProcess;
import com.jd.blockchain.tools.initializer.LedgerInitProperties;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.InitConsensusServiceFactory;
import com.jd.blockchain.tools.initializer.web.LedgerInitConsensusService;
import com.jd.blockchain.tools.initializer.web.LedgerInitializeWebController;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.PresetAnswerPrompter;

public class LedgerInitializeTest {

	public static final String PASSWORD = "abc";

	public static final String[] PUB_KEYS = { "endPsK36koyFr1D245Sa9j83vt6pZUdFBJoJRB3xAsWM6cwhRbna",
			"endPsK36sC5JdPCDPDAXUwZtS3sxEmqEhFcC4whayAsTTh8Z6eoZ",
			"endPsK36jEG281HMHeh6oSqzqLkT95DTnCM6REDURjdb2c67uR3R",
			"endPsK36nse1dck4uF19zPvAMijCV336Y3zWdgb4rQG8QoRj5ktR" };

	public static final String[] PRIV_KEYS = {
			"177gjsj5PHeCpbAtJE7qnbmhuZMHAEKuMsd45zHkv8F8AWBvTBbff8yRKdCyT3kwrmAjSnY",
			"177gjw9u84WtuCsK8u2WeH4nWqzgEoJWY7jJF9AU6XwLHSosrcNX3H6SSBsfvR53HgX7KR2",
			"177gk2FpjufgEon92mf2oRRFXDBZkRy8SkFci7Jxc5pApZEJz3oeCoxieWatDD3Xg7i1QEN",
			"177gjvv7qvfCAXroFezSn23UFXLVLFofKS3y6DXkJ2DwVWS4LcRNtxRgiqWmQEeWNz4KQ3J" };

	private Map<NetworkAddress, LedgerInitConsensusService> serviceRegisterMap = new ConcurrentHashMap<>();

	public static ConsensusProvider getConsensusProvider() {
		return ConsensusProviders.getProvider("com.jd.blockchain.consensus.mq.MsgQueueConsensusProvider");
	}

	public void testInitWith4Nodes() {
		Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();
		LedgerInitProperties initSetting = loadInitSetting();
		Properties props = loadConsensusSetting();
		ConsensusProvider csProvider = getConsensusProvider();
		ConsensusSettings csProps = csProvider.getSettingsFactory().getConsensusSettingsBuilder().createSettings(props);

		NodeContext node0 = new NodeContext(initSetting.getConsensusParticipant(0).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node1 = new NodeContext(initSetting.getConsensusParticipant(1).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node2 = new NodeContext(initSetting.getConsensusParticipant(2).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node3 = new NodeContext(initSetting.getConsensusParticipant(3).getInitializerAddress(),
				serviceRegisterMap);

		String[] memoryConnString = new String[]{"memory://local/0", "memory://local/1", "memory://local/2", "memory://local/3"};

		PrivKey privkey0 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD);
		DBConnectionConfig testDb0 = new DBConnectionConfig();
		testDb0.setConnectionUri(memoryConnString[0]);
		AsyncCallback<HashDigest> callback0 = node0.startInit(0, privkey0, initSetting, csProps, csProvider, testDb0,
				consolePrompter);

		PrivKey privkey1 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[1], PASSWORD);
		DBConnectionConfig testDb1 = new DBConnectionConfig();
		testDb1.setConnectionUri(memoryConnString[1]);
		AsyncCallback<HashDigest> callback1 = node1.startInit(1, privkey1, initSetting, csProps, csProvider, testDb1,
				consolePrompter);

		PrivKey privkey2 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[2], PASSWORD);
		DBConnectionConfig testDb2 = new DBConnectionConfig();
		testDb2.setConnectionUri(memoryConnString[2]);
		AsyncCallback<HashDigest> callback2 = node2.startInit(2, privkey2, initSetting, csProps, csProvider, testDb2,
				consolePrompter);

		PrivKey privkey3 = KeyGenCommand.decodePrivKeyWithRawPassword(PRIV_KEYS[3], PASSWORD);
		DBConnectionConfig testDb03 = new DBConnectionConfig();
		testDb03.setConnectionUri(memoryConnString[3]);
		AsyncCallback<HashDigest> callback3 = node3.startInit(3, privkey3, initSetting, csProps, csProvider, testDb03,
				consolePrompter);

		HashDigest ledgerHash0 = callback0.waitReturn();
		HashDigest ledgerHash1 = callback1.waitReturn();
		HashDigest ledgerHash2 = callback2.waitReturn();
		HashDigest ledgerHash3 = callback3.waitReturn();

		LedgerRepository ledger0 = node0.registLedger(ledgerHash0, memoryConnString[0]);
		LedgerRepository ledger1 = node1.registLedger(ledgerHash1, memoryConnString[1]);
		LedgerRepository ledger2 = node2.registLedger(ledgerHash2, memoryConnString[2]);
		LedgerRepository ledger3 = node3.registLedger(ledgerHash3, memoryConnString[3]);

		LedgerBlock genesisBlock = ledger0.getLatestBlock();

		UserAccountSet userset0 = ledger0.getUserAccountSet(genesisBlock);

		PubKey pubKey0 = KeyGenCommand.decodePubKey(PUB_KEYS[0]);
		Bytes address0 = AddressEncoding.generateAddress(pubKey0);
		UserAccount user0_0 = userset0.getUser(address0);

		PubKey pubKey1 = KeyGenCommand.decodePubKey(PUB_KEYS[1]);
		Bytes address1 = AddressEncoding.generateAddress(pubKey1);
		UserAccount user1_0 = userset0.getUser(address1);

		PubKey pubKey2 = KeyGenCommand.decodePubKey(PUB_KEYS[2]);
		Bytes address2 = AddressEncoding.generateAddress(pubKey2);
		UserAccount user2_0 = userset0.getUser(address2);

		PubKey pubKey3 = KeyGenCommand.decodePubKey(PUB_KEYS[3]);
		Bytes address3 = AddressEncoding.generateAddress(pubKey3);
		UserAccount user3_0 = userset0.getUser(address3);
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

	public static class NodeContext {

		private LedgerManager ledgerManager = new LedgerManager();

		private MemoryDBConnFactory memoryDBConnFactory = new MemoryDBConnFactory();
//		private MemoryBasedDb storageDb = new MemoryBasedDb();

		private InitConsensusServiceFactory initCsServiceFactory;

		private LedgerInitProcess initProcess;

		private CryptoKeyPair partiKey;

		public CryptoKeyPair getPartiKey() {
			return partiKey;
		}

		public LedgerManager getLedgerManager() {
			return ledgerManager;
		}

		public MemoryDBConnFactory getMemoryDBConnFactory() {
			return memoryDBConnFactory;
		}

//		public MemoryBasedDb () {
//			return storageDb;
//		}

		public NodeContext(NetworkAddress address, Map<NetworkAddress, LedgerInitConsensusService> serviceRegisterMap) {
			this.initCsServiceFactory = new MultiThreadInterInvokerFactory(serviceRegisterMap);
			LedgerInitializeWebController initController = new LedgerInitializeWebController(ledgerManager, memoryDBConnFactory,
					initCsServiceFactory);
			serviceRegisterMap.put(address, initController);
			this.initProcess = initController;
		}

		public AsyncCallback<HashDigest> startInit(int currentId, PrivKey privKey, LedgerInitProperties setting,
				ConsensusSettings csProps, ConsensusProvider csProvider, DBConnectionConfig dbConnConfig,
				Prompter prompter) {

			partiKey = new CryptoKeyPair(setting.getConsensusParticipant(0).getPubKey(), privKey);

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					return initProcess.initialize(currentId, privKey, setting, csProps, csProvider, dbConnConfig,
							prompter);
				}
			};

			return invoker.start();
		}

		public AsyncCallback<HashDigest> startInit(int currentId, PrivKey privKey, LedgerInitProperties setting,
				ConsensusSettings csProps, ConsensusProvider csProvider, DBConnectionConfig dbConnConfig,
				Prompter prompter, boolean autoVerifyHash) {

			CryptoConfig cryptoSetting = new CryptoConfig();
			cryptoSetting.setAutoVerifyHash(autoVerifyHash);
			cryptoSetting.setHashAlgorithm(CryptoAlgorithm.SHA256);

			partiKey = new CryptoKeyPair(setting.getConsensusParticipant(0).getPubKey(), privKey);

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					return initProcess.initialize(currentId, privKey, setting, csProps, csProvider, dbConnConfig,
							prompter, cryptoSetting);
				}
			};

			return invoker.start();
		}

		public LedgerRepository registLedger(HashDigest ledgerHash, String connString) {
			return ledgerManager.register(ledgerHash, memoryDBConnFactory.connect(connString).getStorageService());
		}
	}

	private static class MultiThreadInterInvokerFactory implements InitConsensusServiceFactory {

		private Map<NetworkAddress, LedgerInitConsensusService> nodeConsesusServices;

		public MultiThreadInterInvokerFactory(Map<NetworkAddress, LedgerInitConsensusService> nodeConsesusServices) {
			this.nodeConsesusServices = nodeConsesusServices;
		}

		@Override
		public LedgerInitConsensusService connect(NetworkAddress endpointAddress) {
			return new InitConsensusServiceProxy(nodeConsesusServices.get(endpointAddress));
		}

	}

	private static class InitConsensusServiceProxy implements LedgerInitConsensusService {

		private LedgerInitConsensusService initCsService;

		public InitConsensusServiceProxy(LedgerInitConsensusService initCsService) {
			this.initCsService = initCsService;
		}

		@Override
		public LedgerInitPermission requestPermission(int requesterId, SignatureDigest signature) {
			ThreadInvoker<LedgerInitPermission> invoker = new ThreadInvoker<LedgerInitPermission>() {
				@Override
				protected LedgerInitPermission invoke() {
					return initCsService.requestPermission(requesterId, signature);
				}
			};
			return invoker.startAndWait();
		}

		@Override
		public LedgerInitDecision synchronizeDecision(LedgerInitDecision initDecision) {
			ThreadInvoker<LedgerInitDecision> invoker = new ThreadInvoker<LedgerInitDecision>() {
				@Override
				protected LedgerInitDecision invoke() {
					return initCsService.synchronizeDecision(initDecision);
				}
			};
			return invoker.startAndWait();
		}

	}

}
