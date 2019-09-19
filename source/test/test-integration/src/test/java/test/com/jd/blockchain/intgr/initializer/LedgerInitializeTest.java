package test.com.jd.blockchain.intgr.initializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.RolesConfigureOperation;
import com.jd.blockchain.ledger.UserAuthorizeOperation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitProposal;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.UserAccount;
import com.jd.blockchain.ledger.core.UserAccountQuery;
import com.jd.blockchain.storage.service.utils.MemoryDBConnFactory;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerInitProcess;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.InitConsensusServiceFactory;
import com.jd.blockchain.tools.initializer.web.LedgerInitConsensusService;
import com.jd.blockchain.tools.initializer.web.LedgerInitializeWebController;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

import test.com.jd.blockchain.intgr.IntegrationBase;
import test.com.jd.blockchain.intgr.LedgerInitConsensusConfig;
import test.com.jd.blockchain.intgr.PresetAnswerPrompter;

public class LedgerInitializeTest {

	static {
		DataContractRegistry.register(LedgerInitOperation.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(RolesConfigureOperation.class);
		DataContractRegistry.register(UserAuthorizeOperation.class);
	}

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	public static final String PASSWORD = IntegrationBase.PASSWORD;

	public static final String[] PUB_KEYS = IntegrationBase.PUB_KEYS;

	public static final String[] PRIV_KEYS = IntegrationBase.PRIV_KEYS;

	private Map<NetworkAddress, LedgerInitConsensusService> serviceRegisterMap = new ConcurrentHashMap<>();

	public void testMQInitByMemWith4Nodes() {
		testInitWith4Nodes(LedgerInitConsensusConfig.mqConfig, LedgerInitConsensusConfig.memConnectionStrings);
	}

	public void testMQInitByRedisWith4Nodes() {
		testInitWith4Nodes(LedgerInitConsensusConfig.mqConfig, LedgerInitConsensusConfig.redisConnectionStrings);
	}

	@Test
	public void testBftsmartInitWith4Nodes() {
		testInitWith4Nodes(LedgerInitConsensusConfig.bftsmartConfig, LedgerInitConsensusConfig.memConnectionStrings);
	}

	public void testInitWith4Nodes(LedgerInitConsensusConfig.ConsensusConfig config, String[] dbConnections) {
		Prompter consolePrompter = new PresetAnswerPrompter("N"); // new ConsolePrompter();
		LedgerInitProperties initSetting = loadInitSetting();
		Properties props = loadConsensusSetting(config.getConfigPath());
//		ConsensusProvider csProvider = LedgerInitConsensusConfig.getConsensusProvider(config.getProvider());
//		ConsensusSettings csProps = csProvider.getSettingsFactory().getConsensusSettingsBuilder().createSettings(props);

		NodeContext node0 = new NodeContext(initSetting.getConsensusParticipant(0).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node1 = new NodeContext(initSetting.getConsensusParticipant(1).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node2 = new NodeContext(initSetting.getConsensusParticipant(2).getInitializerAddress(),
				serviceRegisterMap);
		NodeContext node3 = new NodeContext(initSetting.getConsensusParticipant(3).getInitializerAddress(),
				serviceRegisterMap);

		PrivKey privkey0 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[0], PASSWORD);
		DBConnectionConfig testDb0 = new DBConnectionConfig();
		testDb0.setConnectionUri(dbConnections[0]);
		AsyncCallback<HashDigest> callback0 = node0.startInit(0, privkey0, initSetting, testDb0, consolePrompter);

		PrivKey privkey1 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[1], PASSWORD);
		DBConnectionConfig testDb1 = new DBConnectionConfig();
		testDb1.setConnectionUri(dbConnections[1]);
		AsyncCallback<HashDigest> callback1 = node1.startInit(1, privkey1, initSetting, testDb1, consolePrompter);

		PrivKey privkey2 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[2], PASSWORD);
		DBConnectionConfig testDb2 = new DBConnectionConfig();
		testDb2.setConnectionUri(dbConnections[2]);
		AsyncCallback<HashDigest> callback2 = node2.startInit(2, privkey2, initSetting, testDb2, consolePrompter);

		PrivKey privkey3 = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV_KEYS[3], PASSWORD);
		DBConnectionConfig testDb03 = new DBConnectionConfig();
		testDb03.setConnectionUri(dbConnections[3]);
		AsyncCallback<HashDigest> callback3 = node3.startInit(3, privkey3, initSetting, testDb03, consolePrompter);

		HashDigest ledgerHash0 = callback0.waitReturn();
		HashDigest ledgerHash1 = callback1.waitReturn();
		HashDigest ledgerHash2 = callback2.waitReturn();
		HashDigest ledgerHash3 = callback3.waitReturn();

		assertNotNull(ledgerHash0);
		assertEquals(ledgerHash0, ledgerHash1);
		assertEquals(ledgerHash0, ledgerHash2);
		assertEquals(ledgerHash0, ledgerHash3);

		LedgerQuery ledger0 = node0.registLedger(ledgerHash0, dbConnections[0]);
		LedgerQuery ledger1 = node1.registLedger(ledgerHash1, dbConnections[1]);
		LedgerQuery ledger2 = node2.registLedger(ledgerHash2, dbConnections[2]);
		LedgerQuery ledger3 = node3.registLedger(ledgerHash3, dbConnections[3]);

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
		UserAccount user0_0 = userset0.getAccount(address0);
		assertNotNull(user0_0);

		PubKey pubKey1 = KeyGenUtils.decodePubKey(PUB_KEYS[1]);
		Bytes address1 = AddressEncoding.generateAddress(pubKey1);
		UserAccount user1_0 = userset0.getAccount(address1);
		assertNotNull(user1_0);

		PubKey pubKey2 = KeyGenUtils.decodePubKey(PUB_KEYS[2]);
		Bytes address2 = AddressEncoding.generateAddress(pubKey2);
		UserAccount user2_0 = userset0.getAccount(address2);
		assertNotNull(user2_0);

		PubKey pubKey3 = KeyGenUtils.decodePubKey(PUB_KEYS[3]);
		Bytes address3 = AddressEncoding.generateAddress(pubKey3);
		UserAccount user3_0 = userset0.getAccount(address3);
		assertNotNull(user3_0);
	}

	public static LedgerInitProperties loadInitSetting() {
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger_init_test.init");
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

	public static class NodeContext {

		private LedgerManager ledgerManager = new LedgerManager();

		private MemoryDBConnFactory storageDb = new MemoryDBConnFactory();

		private InitConsensusServiceFactory initCsServiceFactory;

		private LedgerInitProcess initProcess;

		private AsymmetricKeypair partiKey;

		public AsymmetricKeypair getPartiKey() {
			return partiKey;
		}

		public LedgerManager getLedgerManager() {
			return ledgerManager;
		}

		public MemoryDBConnFactory getStorageDb() {
			return storageDb;
		}

		public NodeContext(NetworkAddress address, Map<NetworkAddress, LedgerInitConsensusService> serviceRegisterMap) {
			this.initCsServiceFactory = new MultiThreadInterInvokerFactory(serviceRegisterMap);
			LedgerInitializeWebController initController = new LedgerInitializeWebController(storageDb,
					initCsServiceFactory);
			serviceRegisterMap.put(address, initController);
			this.initProcess = initController;
		}

		public AsyncCallback<HashDigest> startInit(int currentId, PrivKey privKey, LedgerInitProperties setting,
				DBConnectionConfig dbConnConfig, Prompter prompter) {

			partiKey = new AsymmetricKeypair(setting.getConsensusParticipant(0).getPubKey(), privKey);

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					return initProcess.initialize(currentId, privKey, setting, dbConnConfig, prompter);
				}
			};

			return invoker.start();
		}

		public AsyncCallback<HashDigest> startInit(int currentId, PrivKey privKey, LedgerInitProperties setting,
				DBConnectionConfig dbConnConfig, Prompter prompter, boolean autoVerifyHash) {

			CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
			for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
				supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
			}
			CryptoConfig cryptoSetting = new CryptoConfig();
			cryptoSetting.setSupportedProviders(supportedProviders);
			cryptoSetting.setAutoVerifyHash(autoVerifyHash);
			cryptoSetting.setHashAlgorithm(Crypto.getAlgorithm("SHA256"));
			
			setting.getCryptoProperties().setHashAlgorithm("SHA256");
			setting.getCryptoProperties().setVerifyHash(autoVerifyHash);

			partiKey = new AsymmetricKeypair(setting.getConsensusParticipant(0).getPubKey(), privKey);

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					return initProcess.initialize(currentId, privKey, setting, dbConnConfig, prompter);
				}
			};

			return invoker.start();
		}

		public LedgerQuery registLedger(HashDigest ledgerHash, String memConn) {
			return ledgerManager.register(ledgerHash, storageDb.connect(memConn).getStorageService());
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
		public LedgerInitProposal requestPermission(int requesterId, SignatureDigest signature) {
			ThreadInvoker<LedgerInitProposal> invoker = new ThreadInvoker<LedgerInitProposal>() {
				@Override
				protected LedgerInitProposal invoke() {
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
