package com.jd.blockchain.mocker;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.*;
import com.jd.blockchain.tools.initializer.LedgerInitProperties.ConsensusParticipantConfig;
import com.jd.blockchain.tools.initializer.web.LedgerInitConsensusService;
import com.jd.blockchain.tools.initializer.web.LedgerInitDecisionData;
import com.jd.blockchain.transaction.*;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.concurrent.InvocationResult;
import com.jd.blockchain.utils.io.BytesUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * 账本初始化控制器；
 * 
 * @author huanghaiquan
 *
 */
public class MockerLedgerInitializer implements LedgerInitProcess, LedgerInitConsensusService {

	static {
		DataContractRegistry.register(TransactionRequest.class);
	}

	private static final String[] SUPPORTED_PROVIDERS = {
			ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private static final String DEFAULT_SIGN_ALGORITHM = "ED25519";

	private final SignatureFunction SIGN_FUNC;

	private volatile LedgerInitProposal localPermission;

	private TransactionContent initTxContent;

	private volatile int currentId = -1;

	private volatile LedgerInitSetting ledgerInitSetting;

//	private volatile LedgerInitPermission[] permissions;
//	private volatile LedgerInitPermission permission;

	private volatile Prompter prompter;

	private volatile ConsensusProvider consensusProvider;

	private volatile LedgerBlock genesisBlock;

	private volatile LedgerInitDecision localDecision;

	private volatile DecisionResultHandle[] decisions;

	private volatile DbConnection dbConn;

	private volatile LedgerEditor ledgerEditor;

	private LedgerManager ledgerManager;

	private DbConnectionFactory dbConnFactory;

	public MockerLedgerInitializer() {
		this.SIGN_FUNC = Crypto.getSignatureFunction(DEFAULT_SIGN_ALGORITHM);
	}

	public MockerLedgerInitializer(DbConnectionFactory dbConnFactory, LedgerManager ledgerManager) {
		this.SIGN_FUNC = Crypto.getSignatureFunction(DEFAULT_SIGN_ALGORITHM);
		this.dbConnFactory = dbConnFactory;
		this.ledgerManager = ledgerManager;
	}

	public int getId() {
		return currentId;
	}

	public TransactionContent getInitTxContent() {
		return initTxContent;
	}

	public LedgerInitProposal getLocalPermission() {
		return localPermission;
	}

	public LedgerInitDecision getLocalDecision() {
		return localDecision;
	}

	public void setPrompter(Prompter prompter) {
		this.prompter = prompter;
	}

	private void setConsensusProvider(ConsensusProvider consensusProvider) {
		this.consensusProvider = consensusProvider;
	}

	@Override
	public HashDigest initialize(int currentId, PrivKey privKey, LedgerInitProperties ledgerInitProps,
			DBConnectionConfig dbConnConfig, Prompter prompter) {
		return initialize(currentId, privKey, ledgerInitProps, dbConnConfig, prompter, createDefaultCryptoSetting());
	}

	@Override
	public synchronized HashDigest initialize(int currentId, PrivKey privKey, LedgerInitProperties ledgerInitProps,
			DBConnectionConfig dbConnConfig, Prompter prompter, CryptoSetting cryptoSetting) {

		if (this.ledgerInitSetting != null) {
			throw new IllegalStateException("ledger init process has already started.");
		}

		setPrompter(prompter);

		ConsensusProvider csProvider = ConsensusProviders.getProvider(ledgerInitProps.getConsensusProvider());
		setConsensusProvider(csProvider);

		prompter.info("Init settings and sign permision...");

		prepareLocalProposal(currentId, privKey, ledgerInitProps, null, cryptoSetting);

		try {
			// 连接数据库；
			connectDb(dbConnConfig);

			// 生成账本；
			makeLocalDecision(privKey);

			// 获取其它参与方的账本生成结果；
			return consensusDecisions();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeDb();
		}
	}

	public DbConnection connectDb(DBConnectionConfig dbConnConfig) {
		this.dbConn = dbConnFactory.connect(dbConnConfig.getUri(), dbConnConfig.getPassword());
		return dbConn;
	}

	public LedgerInitDecision makeLocalDecision(PrivKey privKey) {
		// 生成账本；
		this.ledgerEditor = ledgerManager.newLedger(this.ledgerInitSetting, dbConn.getStorageService());
		this.genesisBlock = initLedgerDataset(ledgerEditor);

		// 生成签名决定；
		this.localDecision = makeDecision(currentId, genesisBlock.getHash(), privKey);
		this.decisions = new DecisionResultHandle[this.ledgerInitSetting.getConsensusParticipants().length];
		for (int i = 0; i < decisions.length; i++) {
			// 参与者的 id 是依次递增的；
			this.decisions[i] = new DecisionResultHandle(i);
		}
		// 预置当前参与方的“决定”到列表，避免向自己发起请求；
		this.decisions[currentId].setValue(localDecision);
		return localDecision;
	}

	public HashDigest consensusDecisions() {
		// 执行提交提交；
		ledgerEditor.commit();
		return genesisBlock.getHash();
	}

	public void closeDb() {
		if (dbConn != null) {
			DbConnection connection = dbConn;
			dbConn = null;
			try {
				connection.close();
			} catch (IOException e) {
				prompter.error(e, "Error occurred on closing db connection! --" + e.getMessage());
			}
		}
	}

	public CryptoSetting createDefaultCryptoSetting() {
		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}
		CryptoConfig defCryptoSetting = new CryptoConfig();
		defCryptoSetting.setSupportedProviders(supportedProviders);
		defCryptoSetting.setAutoVerifyHash(true);
		defCryptoSetting.setHashAlgorithm(Crypto.getAlgorithm("SHA256"));

		return defCryptoSetting;
	}

	public LedgerInitProposal prepareLocalProposal(int currentId, PrivKey privKey, LedgerInitProperties ledgerProps,
			ConsensusSettings csSettings, CryptoSetting cryptoSetting) {
		// 创建初始化配置；
		LedgerInitData initSetting = new LedgerInitData();
		initSetting.setLedgerSeed(ledgerProps.getLedgerSeed());
		initSetting.setCryptoSetting(cryptoSetting);

		List<ConsensusParticipantConfig> partiList = ledgerProps.getConsensusParticipants();
		ConsensusParticipantConfig[] parties = partiList.toArray(new ConsensusParticipantConfig[partiList.size()]);
		ConsensusParticipantConfig[] orderedParties = sortAndVerify(parties);
		initSetting.setConsensusParticipants(orderedParties);

		// 创建默认的共识配置；
		try {
			byte[] csSettingBytes = new byte[1024];
			new Random().nextBytes(csSettingBytes);

			initSetting.setConsensusProvider(consensusProvider.getName());
			initSetting.setConsensusSettings(new Bytes(csSettingBytes));
		} catch (Exception e) {
			throw new LedgerInitException("Create default consensus config failed! --" + e.getMessage(), e);
		}

		if (currentId < 0 || currentId >= orderedParties.length) {
			throw new LedgerInitException("Your id is out of bound of participant list!");
		}
		this.currentId = currentId;
		this.ledgerInitSetting = initSetting;

		// 校验当前的公钥、私钥是否匹配；
		byte[] testBytes = BytesUtils.toBytes(currentId);
		SignatureDigest testSign = SIGN_FUNC.sign(privKey, testBytes);
		PubKey myPubKey = orderedParties[currentId].getPubKey();
		if (!SIGN_FUNC.verify(testSign, myPubKey, testBytes)) {
			throw new LedgerInitException("Your pub-key specified in the init-settings isn't match your priv-key!");
		}
		// 生成初始化交易，并签署许可；
		TransactionBuilder initTxBuilder = new TxBuilder(null);// 账本初始化交易的账本 hash 为 null；
		initTxBuilder.ledgers().create(initSetting);
		for (ParticipantNode p : initSetting.getConsensusParticipants()) {
			// TODO：暂时只支持注册用户的初始化操作；
			BlockchainIdentity superUserId = new BlockchainIdentityData(p.getPubKey());
			initTxBuilder.users().register(superUserId);
		}
		this.initTxContent = initTxBuilder.prepareContent();

		// 对初始交易签名，生成当前参与者的账本初始化许可；
		SignatureDigest permissionSign = SignatureUtils.sign(initTxContent, privKey);
		localPermission = new LedgerInitProposalData(currentId, permissionSign);

		this.currentId = currentId;
		return localPermission;
	}

	private LedgerInitDecision makeDecision(int participantId, HashDigest ledgerHash, PrivKey privKey) {
		byte[] dataBytes = getDecisionBytes(participantId, ledgerHash);
		SignatureFunction signFunc = Crypto.getSignatureFunction(privKey.getAlgorithm());
		SignatureDigest signature = signFunc.sign(privKey, dataBytes);

		LedgerInitDecisionData decision = new LedgerInitDecisionData();
		decision.setParticipantId(participantId);
		decision.setLedgerHash(ledgerHash);
		decision.setSignature(signature);
		return decision;
	}

	private LedgerBlock initLedgerDataset(LedgerEditor ledgerEditor) {
		// 初始化时，自动将参与方注册为账本的用户；
		TxRequestBuilder txReqBuilder = new TxRequestBuilder(this.initTxContent);
//		ParticipantNode[] parties = this.ledgerInitSetting.getConsensusParticipants();
		ParticipantNode parti = this.ledgerInitSetting.getConsensusParticipants()[currentId];

		PubKey pubKey = parti.getPubKey();
		SignatureDigest signDigest = this.localPermission.getTransactionSignature();
		DigitalSignatureBlob digitalSignature = new DigitalSignatureBlob(pubKey, signDigest);
		txReqBuilder.addNodeSignature(digitalSignature);

		TransactionRequest txRequest = txReqBuilder.buildRequest();

		LedgerTransactionContext txCtx = ledgerEditor.newTransaction(txRequest);
		Operation[] ops = txRequest.getTransactionContent().getOperations();
		// 注册用户； 注：第一个操作是 LedgerInitOperation；
		// TODO：暂时只支持注册用户的初始化操作；
		for (int i = 1; i < ops.length; i++) {
			UserRegisterOperation userRegOP = (UserRegisterOperation) ops[i];
			txCtx.getDataset().getUserAccountSet().register(userRegOP.getUserID().getAddress(),
					userRegOP.getUserID().getPubKey());
		}

		txCtx.commit(TransactionState.SUCCESS, null);

		return ledgerEditor.prepare();
	}

	private byte[] getDecisionBytes(int participantId, HashDigest ledgerHash) {
		return BytesUtils.concat(BytesUtils.toBytes(participantId), ledgerHash.toBytes());
	}

	@Override
	public LedgerInitProposal requestPermission(int requesterId, SignatureDigest signature) {
		return localPermission;
	}

	@Override
	public LedgerInitDecision synchronizeDecision(@RequestBody LedgerInitDecision initDecision) {
		return localDecision;
	}

	/**
	 * 对参与者列表按照 id 进行升序排列，并校验id是否从 1 开始且没有跳跃；
	 * 
	 * @param parties
	 * @return
	 */
	private ConsensusParticipantConfig[] sortAndVerify(ConsensusParticipantConfig[] parties) {
		Arrays.sort(parties, (o1, o2) -> o1.getId() - o2.getId());
		for (int i = 0; i < parties.length; i++) {
			if (parties[i].getId() != i) {
				throw new LedgerInitException(
						"The ids of participants are not match their positions in the participant-list!");
			}
		}
		return parties;
	}

	private static class DecisionResultHandle extends InvocationResult<LedgerInitDecision> {

		private final int PARTICIPANT_ID;

		public DecisionResultHandle(int participantId) {
			this.PARTICIPANT_ID = participantId;
		}

	}

}
