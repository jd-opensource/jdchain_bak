package test.com.jd.blockchain.intgr.perf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitPermission;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.impl.LedgerManager;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerInitProcess;
import com.jd.blockchain.tools.initializer.LedgerInitProperties;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.InitConsensusServiceFactory;
import com.jd.blockchain.tools.initializer.web.LedgerInitConsensusService;
import com.jd.blockchain.tools.initializer.web.LedgerInitializeWebController;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

public class Utils {

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

		private DbConnectionFactory dbConnFactory;

		private InitConsensusServiceFactory initCsServiceFactory;

		private LedgerInitProcess initProcess;

		private CryptoKeyPair partiKey;

		public CryptoKeyPair getPartiKey() {
			return partiKey;
		}

		public LedgerManager getLedgerManager() {
			return ledgerManager;
		}

		public DbConnectionFactory getStorageDb() {
			return dbConnFactory;
		}

		public NodeContext(NetworkAddress address, Map<NetworkAddress, LedgerInitConsensusService> serviceRegisterMap,
				DbConnectionFactory dbConnFactory) {
			this.dbConnFactory = dbConnFactory;
			this.initCsServiceFactory = new MultiThreadInterInvokerFactory(serviceRegisterMap);
			LedgerInitializeWebController initController = new LedgerInitializeWebController(ledgerManager,
					dbConnFactory, initCsServiceFactory);
			serviceRegisterMap.put(address, initController);
			this.initProcess = initController;
		}

		public AsyncCallback<HashDigest> startInit(int currentId, PrivKey privKey, LedgerInitProperties setting,
				ConsensusSettings csProps, ConsensusProvider consensusProvider, DBConnectionConfig dbConnConfig,
				Prompter prompter) {

			partiKey = new CryptoKeyPair(setting.getConsensusParticipant(0).getPubKey(), privKey);

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					return initProcess.initialize(currentId, privKey, setting, csProps, consensusProvider, dbConnConfig,
							prompter);
				}
			};

			return invoker.start();
		}

		public AsyncCallback<HashDigest> startInit(int currentId, PrivKey privKey, LedgerInitProperties setting,
				ConsensusSettings csProps, ConsensusProvider consensusProvider, DBConnectionConfig dbConnConfig,
				Prompter prompter, boolean autoVerifyHash) {
			return startInit(currentId, privKey, setting, csProps, consensusProvider, dbConnConfig, prompter,
					autoVerifyHash, CryptoAlgorithm.SHA256);
		}

		public AsyncCallback<HashDigest> startInit(int currentId, PrivKey privKey, LedgerInitProperties setting,
				ConsensusSettings csProps, ConsensusProvider consensusProvider, DBConnectionConfig dbConnConfig,
				Prompter prompter, boolean autoVerifyHash, CryptoAlgorithm hashAlg) {
			CryptoConfig cryptoSetting = new CryptoConfig();
			cryptoSetting.setAutoVerifyHash(autoVerifyHash);
			cryptoSetting.setHashAlgorithm(hashAlg);

			return startInit(currentId, privKey, setting, csProps, consensusProvider, dbConnConfig, prompter,
					cryptoSetting);
		}

		public AsyncCallback<HashDigest> startInit(int currentId, PrivKey privKey, LedgerInitProperties setting,
				ConsensusSettings csProps, ConsensusProvider consensusProvider, DBConnectionConfig dbConnConfig,
				Prompter prompter, CryptoSetting cryptoSetting) {

			partiKey = new CryptoKeyPair(setting.getConsensusParticipant(0).getPubKey(), privKey);

			ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
				@Override
				protected HashDigest invoke() throws Exception {
					return initProcess.initialize(currentId, privKey, setting, csProps, consensusProvider, dbConnConfig,
							prompter, cryptoSetting);
				}
			};

			return invoker.start();
		}

		public LedgerRepository registLedger(HashDigest ledgerHash, DBConnectionConfig dbConnConf) {
			return ledgerManager.register(ledgerHash, dbConnFactory.connect(dbConnConf.getUri()).getStorageService());
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
