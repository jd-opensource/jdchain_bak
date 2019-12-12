package com.jd.blockchain.mocker;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.jd.blockchain.ledger.*;
import org.mockito.Mockito;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.ClientIdentification;
import com.jd.blockchain.consensus.ClientIdentifications;
import com.jd.blockchain.consensus.action.ActionRequest;
import com.jd.blockchain.consensus.action.ActionResponse;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.DefaultOperationHandleRegisteration;
import com.jd.blockchain.ledger.core.LedgerDataQuery;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQueryService;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerSecurityManager;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionBatchProcessor;
import com.jd.blockchain.mocker.config.MockerConstant;
import com.jd.blockchain.mocker.config.PresetAnswerPrompter;
import com.jd.blockchain.mocker.handler.MockerContractExeHandle;
import com.jd.blockchain.mocker.proxy.ContractProxy;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.storage.service.utils.MemoryDBConnFactory;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.web.LedgerInitConfiguration;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.web.serializes.ByteArrayObjectUtil;

public class MockerNodeContext implements BlockchainQueryService {

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private static final DBConnectionConfig dbConnectionConfig = new DBConnectionConfig("memory://local/0");

	private DbConnectionFactory dbConnFactory = new MemoryDBConnFactory();

	private DefaultOperationHandleRegisteration opHandler = new DefaultOperationHandleRegisteration();

	private MockerContractExeHandle contractExeHandle = new MockerContractExeHandle();

	private Map<String, BlockchainKeypair> participants = new HashMap<>();

	private LedgerManager ledgerManager = new LedgerManager();

	private BlockchainKeypair defaultKeypair;

	private LedgerRepository ledgerRepository;

	private LedgerQueryService queryService;

	private HashDigest ledgerHash;

	private String ledgerSeed;

	static {
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(TransactionContentBody.class);
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(NodeRequest.class);
		DataContractRegistry.register(EndpointRequest.class);
		DataContractRegistry.register(TransactionResponse.class);

		DataContractRegistry.register(Operation.class);
		DataContractRegistry.register(ContractCodeDeployOperation.class);
		DataContractRegistry.register(ContractEventSendOperation.class);
		DataContractRegistry.register(DataAccountRegisterOperation.class);
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(ParticipantRegisterOperation.class);
		DataContractRegistry.register(DataAccountKVSetOperation.class);
		DataContractRegistry.register(DataAccountKVSetOperation.KVWriteEntry.class);
		DataContractRegistry.register(ParticipantStateUpdateOperation.class);

		DataContractRegistry.register(ActionRequest.class);
		DataContractRegistry.register(ActionResponse.class);
		DataContractRegistry.register(ClientIdentifications.class);
		DataContractRegistry.register(ClientIdentification.class);

//		DataContractRegistry.register(LedgerAdminInfo.class);

		ByteArrayObjectUtil.init();
	}

	public MockerNodeContext() {
	}

	public MockerNodeContext(String ledgerSeed) {
		this.ledgerSeed = ledgerSeed;
	}

	public MockerNodeContext participants(String name, BlockchainKeypair partiKey) {
		participants.put(name, partiKey);
		return this;
	}

	public MockerNodeContext participants(BlockchainKeypair partiKey) {
		participants.put(partiKey.getPubKey().toBase58(), partiKey);
		return this;
	}

	public MockerNodeContext build() {
		if (ledgerSeed == null || ledgerSeed.length() == 0) {
			ledgerSeed = MockerConstant.DEFAULT_LEDGER_SEED;
		}
		if (participants.size() < 4) {
			// 缺少的需要补充，使用常量中的内容进行补充
			for (int i = 0; i < MockerConstant.PUBLIC_KEYS.length; i++) {
				String pubKeyString = MockerConstant.PUBLIC_KEYS[i];
				boolean isExist = false;
				// 通过公钥进行判断
				for (Map.Entry<String, BlockchainKeypair> entry : participants.entrySet()) {
					String existPubKey = KeyGenUtils.encodePubKey(entry.getValue().getPubKey());
					if (pubKeyString.equals(existPubKey)) {
						isExist = true;
					}
				}
				if (!isExist) {
					// 加入系统中
					PrivKey privKey = KeyGenUtils.decodePrivKeyWithRawPassword(MockerConstant.PRIVATE_KEYS[i],
							MockerConstant.PASSWORD);
					PubKey pubKey = KeyGenUtils.decodePubKey(MockerConstant.PUBLIC_KEYS[i]);
					participants(new BlockchainKeypair(pubKey, privKey));
				}
				if (participants.size() >= 4) {
					break;
				}
			}
		}

		LedgerInitProperties ledgerInitProperties = loadInitSetting();

		MockerLedgerInitializer mockLedgerInitializer = new MockerLedgerInitializer(dbConnFactory, ledgerManager);

		LedgerInitConfiguration initConfig = LedgerInitConfiguration.create(ledgerInitProperties);
		initConfig.getLedgerSettings().setCryptoSetting(cryptoConfig());

		ledgerHash = mockLedgerInitializer.initialize(0, defaultKeypair.getPrivKey(), initConfig, dbConnectionConfig,
				new PresetAnswerPrompter("N"));

		ledgerRepository = registerLedger(ledgerHash, dbConnectionConfig);

		queryService = new LedgerQueryService(ledgerRepository);

		contractExeHandle.initLedger(ledgerManager, ledgerHash);

		opHandler.registerHandle(contractExeHandle);

		return this;
	}

	public String registerUser(BlockchainKeypair user) {
		TxBuilder txBuilder = txBuilder();
		txBuilder.users().register(user.getIdentity());
		txProcess(txRequest(txBuilder));
		return user.getAddress().toBase58();
	}

	public String registerDataAccount(BlockchainKeypair dataAccount) {
		TxBuilder txBuilder = txBuilder();
		txBuilder.dataAccounts().register(dataAccount.getIdentity());
		txProcess(txRequest(txBuilder));
		return dataAccount.getAddress().toBase58();
	}

	public String registerDataAccount() {
		return registerDataAccount(BlockchainKeyGenerator.getInstance().generate());
	}

	public void writeKv(String address, String key, String value, long version) {
		TxBuilder txBuilder = txBuilder();
		txBuilder.dataAccount(address).setText(key, value, version);
		txProcess(txRequest(txBuilder));
	}

	public void writeKv(String address, String key, long value, long version) {
		TxBuilder txBuilder = txBuilder();
		txBuilder.dataAccount(address).setInt64(key, value, version);
		txProcess(txRequest(txBuilder));
	}

	public void writeKv(String address, String key, byte[] value, long version) {
		TxBuilder txBuilder = txBuilder();
		txBuilder.dataAccount(address).setBytes(key, value, version);
		txProcess(txRequest(txBuilder));
	}

	public void writeKv(String address, String key, Bytes value, long version) {
		TxBuilder txBuilder = txBuilder();
		txBuilder.dataAccount(address).setBytes(key, value, version);
		txProcess(txRequest(txBuilder));
	}

	public <T> T deployContract(T contract) {
		// 首先发布合约
		BlockchainIdentity identity = deployContract2Ledger(contract);
		// 生成代理对象
		ContractProxy<T> contractProxy = new ContractProxy<>(identity, this, contract, contractExeHandle);

		T proxy = (T) newProxyInstance(contract.getClass().getClassLoader(), contract.getClass().getInterfaces(),
				contractProxy);

		return proxy;
	}

	private BlockchainIdentity deployContract2Ledger(Object contract) {
		BlockchainIdentity contractIdentity = BlockchainKeyGenerator.getInstance().generate().getIdentity();
		// 合约发布
		// 注意此处只是将其放入内存中，而不需要真正编译为字节码
		TxBuilder txBuilder = txBuilder();
		txBuilder.contracts().deploy(contractIdentity, BytesUtils.toBytes(contract.getClass().getName()));
		// 执行
		txProcess(txRequest(txBuilder));
		return contractIdentity;
	}

	public String getLedgerSeed() {
		return ledgerSeed;
	}

	public HashDigest getLedgerHash() {
		return ledgerHash;
	}

	@Override
	public HashDigest[] getLedgerHashs() {
		return queryService.getLedgerHashs();
	}

	@Override
	public LedgerInfo getLedger(HashDigest ledgerHash) {
		return queryService.getLedger(ledgerHash);
	}

	@Override
	public LedgerAdminInfo getLedgerAdminInfo(HashDigest ledgerHash) {
		return queryService.getLedgerAdminInfo(ledgerHash);
	}

	@Override
	public ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash) {
		return queryService.getConsensusParticipants(ledgerHash);
	}

	@Override
	public LedgerMetadata getLedgerMetadata(HashDigest ledgerHash) {
		return queryService.getLedgerMetadata(ledgerHash);
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, long height) {
		return queryService.getBlock(ledgerHash, height);
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, HashDigest blockHash) {
		return queryService.getBlock(ledgerHash, blockHash);
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, long height) {
		return queryService.getTransactionCount(ledgerHash, height);
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, HashDigest blockHash) {
		return queryService.getTransactionCount(ledgerHash, blockHash);
	}

	@Override
	public long getTransactionTotalCount(HashDigest ledgerHash) {
		return queryService.getTransactionTotalCount(ledgerHash);
	}

	@Override
	public long getDataAccountCount(HashDigest ledgerHash, long height) {
		return queryService.getDataAccountCount(ledgerHash, height);
	}

	@Override
	public long getDataAccountCount(HashDigest ledgerHash, HashDigest blockHash) {
		return queryService.getDataAccountCount(ledgerHash, blockHash);
	}

	@Override
	public long getDataAccountTotalCount(HashDigest ledgerHash) {
		return queryService.getDataAccountTotalCount(ledgerHash);
	}

	@Override
	public long getUserCount(HashDigest ledgerHash, long height) {
		return queryService.getUserCount(ledgerHash, height);
	}

	@Override
	public long getUserCount(HashDigest ledgerHash, HashDigest blockHash) {
		return queryService.getUserCount(ledgerHash, blockHash);
	}

	@Override
	public long getUserTotalCount(HashDigest ledgerHash) {
		return queryService.getUserTotalCount(ledgerHash);
	}

	@Override
	public long getContractCount(HashDigest ledgerHash, long height) {
		return queryService.getContractCount(ledgerHash, height);
	}

	@Override
	public long getContractCount(HashDigest ledgerHash, HashDigest blockHash) {
		return queryService.getContractCount(ledgerHash, blockHash);
	}

	@Override
	public long getContractTotalCount(HashDigest ledgerHash) {
		return queryService.getContractTotalCount(ledgerHash);
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, long height, int fromIndex, int count) {
		return queryService.getTransactions(ledgerHash, height, fromIndex, count);
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, HashDigest blockHash, int fromIndex, int count) {
		return queryService.getTransactions(ledgerHash, blockHash, fromIndex, count);
	}

	@Override
	public LedgerTransaction getTransactionByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		return queryService.getTransactionByContentHash(ledgerHash, contentHash);
	}

	@Override
	public TransactionState getTransactionStateByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		return queryService.getTransactionStateByContentHash(ledgerHash, contentHash);
	}

	@Override
	public UserInfo getUser(HashDigest ledgerHash, String address) {
		return queryService.getUser(ledgerHash, address);
	}

	@Override
	public AccountHeader getDataAccount(HashDigest ledgerHash, String address) {
		return queryService.getDataAccount(ledgerHash, address);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, String... keys) {
		return queryService.getDataEntries(ledgerHash, address, keys);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, KVInfoVO kvInfoVO) {
		return queryService.getDataEntries(ledgerHash, address, kvInfoVO);
	}

	@Override
	public long getDataEntriesTotalCount(HashDigest ledgerHash, String address) {
		return queryService.getDataEntriesTotalCount(ledgerHash, address);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, int fromIndex, int count) {
		return queryService.getDataEntries(ledgerHash, address, fromIndex, count);
	}

	@Override
	public ContractInfo getContract(HashDigest ledgerHash, String address) {
		return queryService.getContract(ledgerHash, address);
	}

	@Override
	public AccountHeader[] getUsers(HashDigest ledgerHash, int fromIndex, int count) {
		return queryService.getUsers(ledgerHash, fromIndex, count);
	}

	@Override
	public AccountHeader[] getDataAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		return queryService.getDataAccounts(ledgerHash, fromIndex, count);
	}

	@Override
	public AccountHeader[] getContractAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		return queryService.getContractAccounts(ledgerHash, fromIndex, count);
	}

	public TxBuilder txBuilder() {
		return new TxBuilder(ledgerHash);
	}

	public TransactionRequest txRequest(TxBuilder txBuilder) {
		TransactionRequestBuilder reqBuilder = txBuilder.prepareRequest();
		reqBuilder.signAsEndpoint(defaultKeypair);
		return reqBuilder.buildRequest();
	}

	private static LedgerSecurityManager getSecurityManager() {
		LedgerSecurityManager securityManager = Mockito.mock(LedgerSecurityManager.class);

		SecurityPolicy securityPolicy = Mockito.mock(SecurityPolicy.class);
		when(securityPolicy.isEndpointEnable(any(LedgerPermission.class), any())).thenReturn(true);
		when(securityPolicy.isEndpointEnable(any(TransactionPermission.class), any())).thenReturn(true);
		when(securityPolicy.isNodeEnable(any(LedgerPermission.class), any())).thenReturn(true);
		when(securityPolicy.isNodeEnable(any(TransactionPermission.class), any())).thenReturn(true);

		when(securityManager.createSecurityPolicy(any(), any())).thenReturn(securityPolicy);

		return securityManager;
	}

	public OperationResult[] txProcess(TransactionRequest txRequest) {
		LedgerEditor newEditor = ledgerRepository.createNextBlock();
		LedgerBlock latestBlock = ledgerRepository.getLatestBlock();
		LedgerDataQuery previousDataSet = ledgerRepository.getLedgerData(latestBlock);
		TransactionBatchProcessor txProc = new TransactionBatchProcessor(getSecurityManager(), newEditor,
				ledgerRepository, opHandler);
		TransactionResponse txResp = txProc.schedule(txRequest);
		TransactionBatchResultHandle handle = txProc.prepare();
		handle.commit();
		return txResp.getOperationResults();
	}

	private LedgerRepository registerLedger(HashDigest ledgerHash, DBConnectionConfig dbConnConf) {
		return ledgerManager.register(ledgerHash, dbConnFactory.connect(dbConnConf.getUri()).getStorageService());
	}

	private CryptoConfig cryptoConfig() {
		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}
		CryptoConfig cryptoSetting = new CryptoConfig();
		cryptoSetting.setSupportedProviders(supportedProviders);
		cryptoSetting.setAutoVerifyHash(false);
		cryptoSetting.setHashAlgorithm(ClassicAlgorithm.SHA256);
		return cryptoSetting;
	}

	private LedgerInitProperties loadInitSetting() {

		Properties ledgerProp = new Properties();

		ledgerProp.put(LedgerInitProperties.LEDGER_SEED, ledgerSeed);

		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat(LedgerInitProperties.CREATED_TIME_FORMAT);
		ledgerProp.put(LedgerInitProperties.CREATED_TIME, format.format(now));

		ledgerProp.put("ledger.name", MockerConstant.LEDGER_NAME);

		ledgerProp.put(LedgerInitProperties.CONSENSUS_SERVICE_PROVIDER,
				"com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider");

		ledgerProp.put(LedgerInitProperties.CONSENSUS_CONFIG, "classpath:bftsmart.config");

		ledgerProp.put(LedgerInitProperties.CRYPTO_SERVICE_PROVIDERS,
				"com.jd.blockchain.crypto.service.classic.ClassicCryptoService,"
						+ "com.jd.blockchain.crypto.service.sm.SMCryptoService");

		ledgerProp.put(LedgerInitProperties.PART_COUNT, String.valueOf(participants.size()));

		int partiIndex = 0;
		for (Map.Entry<String, BlockchainKeypair> entry : participants.entrySet()) {
			String name = entry.getKey();
			BlockchainKeypair keypair = entry.getValue();
			if (partiIndex == 0) {
				defaultKeypair = keypair;
			}
			String partiPrefix = String.format(LedgerInitProperties.PART_ID_PATTERN, partiIndex) + ".";
			ledgerProp.put(partiPrefix + LedgerInitProperties.PART_NAME, name);
			ledgerProp.put(partiPrefix + LedgerInitProperties.PART_PUBKEY_PATH, "");
			ledgerProp.put(partiPrefix + LedgerInitProperties.PART_PUBKEY,
					KeyGenUtils.encodePubKey(keypair.getPubKey()));
			ledgerProp.put(partiPrefix + LedgerInitProperties.PART_INITIALIZER_HOST, MockerConstant.LOCAL_ADDRESS);
			ledgerProp.put(partiPrefix + LedgerInitProperties.PART_INITIALIZER_PORT,
					String.valueOf(MockerConstant.LEDGER_INIT_PORT_START + partiIndex * 10));
			ledgerProp.put(partiPrefix + LedgerInitProperties.PART_INITIALIZER_SECURE, String.valueOf(false));
			partiIndex++;
		}

		return LedgerInitProperties.resolve(ledgerProp);
	}
}
