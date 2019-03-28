package com.jd.blockchain.tools.initializer.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionBuilder;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitPermission;
import com.jd.blockchain.ledger.core.LedgerInitPermissionData;
import com.jd.blockchain.ledger.core.LedgerManage;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.data.DigitalSignatureBlob;
import com.jd.blockchain.ledger.data.LedgerInitSettingData;
import com.jd.blockchain.ledger.data.TxBuilder;
import com.jd.blockchain.ledger.data.TxRequestBuilder;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.InitializingStep;
import com.jd.blockchain.tools.initializer.LedgerInitException;
import com.jd.blockchain.tools.initializer.LedgerInitProcess;
import com.jd.blockchain.tools.initializer.LedgerInitProperties;
import com.jd.blockchain.tools.initializer.LedgerInitProperties.ConsensusParticipantConfig;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.concurrent.InvocationResult;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.net.NetworkAddress;
import com.jd.blockchain.tools.initializer.Prompter;

/**
 * 账本初始化控制器；
 * 
 * @author huanghaiquan
 *
 */
@RestController
public class LedgerInitializeWebController implements LedgerInitProcess, LedgerInitConsensusService {

	static {
		DataContractRegistry.register(TransactionRequest.class);
	}

	private static final CryptoAlgorithm SIGN_ALG = CryptoAlgorithm.ED25519;

	private volatile LedgerInitPermission localPermission;

	private TransactionContent initTxContent;

	private volatile int currentId = -1;

	private volatile LedgerInitSetting ledgerInitSetting;

	private volatile LedgerInitPermission[] permissions;

	private volatile NetworkAddress[] initializerAddresses;

	private volatile Prompter prompter;

	private volatile ConsensusProvider consensusProvider;

	private volatile LedgerBlock genesisBlock;

	private volatile LedgerInitDecision localDecision;

	private volatile DecisionResultHandle[] decisions;

	private volatile DbConnection dbConn;

	private volatile LedgerEditor ledgerEditor;

	@Autowired
	private LedgerManage ledgerManager;

	@Autowired
	private DbConnectionFactory dbConnFactory;

	@Autowired
	private InitConsensusServiceFactory initCsServiceFactory;

	public LedgerInitializeWebController() {
	}

	public LedgerInitializeWebController(LedgerManage ledgerManager, DbConnectionFactory dbConnFactory,
			InitConsensusServiceFactory initCsServiceFactory) {
		this.ledgerManager = ledgerManager;
		this.dbConnFactory = dbConnFactory;
		this.initCsServiceFactory = initCsServiceFactory;
	}

	public int getId() {
		return currentId;
	}

	public TransactionContent getInitTxContent() {
		return initTxContent;
	}

	public LedgerInitPermission getLocalPermission() {
		return localPermission;
	}

	public LedgerInitDecision getLocalDecision() {
		return localDecision;
	}

	public void setPrompter(Prompter prompter) {
		this.prompter = prompter;
	}

	public ConsensusProvider getConsensusProvider() {
		return consensusProvider;
	}

	public void setConsensusProvider(ConsensusProvider consensusProvider) {
		this.consensusProvider = consensusProvider;
	}

	@Override
	public HashDigest initialize(int currentId, PrivKey privKey, LedgerInitProperties ledgerInitProps,
			ConsensusSettings csSettings, ConsensusProvider csProvider, DBConnectionConfig dbConnConfig,
			Prompter prompter) {
		return initialize(currentId, privKey, ledgerInitProps, csSettings, csProvider, dbConnConfig, prompter,
				createDefaultCryptoSetting());
	}

	@Override
	public HashDigest initialize(int currentId, PrivKey privKey, LedgerInitProperties ledgerInitProps,
			ConsensusSettings consensusProps, ConsensusProvider csProvider, DBConnectionConfig dbConnConfig,
			Prompter prompter, CryptoSetting cryptoSetting) {

		if (this.ledgerInitSetting != null) {
			throw new IllegalStateException("ledger init process has already started.");
		}

		setPrompter(prompter);
		setConsensusProvider(csProvider);
		prompter.info("Init settings and sign permision...");

		prepareLocalPermission(currentId, privKey, ledgerInitProps, consensusProps, cryptoSetting);

		prompter.confirm(InitializingStep.PERMISSION_READY.toString(),
				"Ledger init permission has already prepared! Any key to continue...");

		prompter.info("Start consensus with each other participant...");

		// 从其它参与者获得许可；
		boolean allPermitted = consensusPermissions(currentId, privKey);

		if (allPermitted) {
			prompter.info("All participants permitted!");
		} else {
			prompter.error("Initialization is broken because of not all participants permitting!");
			return null;
		}

		try {
			// 连接数据库；
			connectDb(dbConnConfig);

			// 生成账本；
			makeLocalDecision(privKey);

			// 获取其它参与方的账本生成结果；
			return consensusDecisions(privKey);
		} finally {
			closeDb();
		}
	}

	public boolean consensusPermisions(PrivKey privKey) {
		return consensusPermissions(currentId, privKey);
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
		this.decisions[currentId].setResult(localDecision);
		return localDecision;
	}

	public HashDigest consensusDecisions(PrivKey privKey) {
		// 获取其它参与方的账本生成结果；
		boolean allDecided = startRequestDecisions(privKey, prompter);
		if (!allDecided) {
			prompter.error(
					"Rollback ledger initialization because of not all nodes make same decision! --[Current Participant=%s]",
					currentId);
			ledgerEditor.cancel();
			return null;
		}

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

	/**
	 * 在所有参与者之间进行第一阶段的共识：账本创建许可；
	 * 
	 * @param privKey
	 * @return
	 */
	private boolean consensusPermissions(int currentId, PrivKey privKey) {
		// 从其它参与者获得许可；
		boolean allPermitted = false;
		int retry = 0;
		do {
			allPermitted = startRequestPermissions(currentId, privKey);
			if (!allPermitted) {
				if (retry < 16) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// ignore interrupted exception;
					}
				} else {
					String r = prompter.confirm(
							"\r\n Some permissions were rejected! Do you retry again? Press 'Y' to retry, any others to quit.  :>");
					if (!"Y".equalsIgnoreCase(r)) {
						throw new LedgerInitException("Ledger init process has broken!");
					}
					retry = 0;
				}

				retry++;
				prompter.info("Retry requesting permissions...[%s]", retry);
			}
		} while (!allPermitted);

		return allPermitted;
	}

	public CryptoSetting createDefaultCryptoSetting() {
		CryptoConfig defCryptoSetting = new CryptoConfig();
		defCryptoSetting.setAutoVerifyHash(true);
		defCryptoSetting.setHashAlgorithm(CryptoAlgorithm.SHA256);

		return defCryptoSetting;
	}

	public LedgerInitPermission prepareLocalPermission(int currentId, PrivKey privKey, LedgerInitProperties ledgerProps,
			ConsensusSettings consensusProps) {
		CryptoSetting defCryptoSetting = createDefaultCryptoSetting();
		return prepareLocalPermission(currentId, privKey, ledgerProps, consensusProps, defCryptoSetting);
	}

	public LedgerInitPermission prepareLocalPermission(int currentId, PrivKey privKey, LedgerInitProperties ledgerProps,
			ConsensusSettings csSettings, CryptoSetting cryptoSetting) {
		// 创建初始化配置；
		LedgerInitSettingData initSetting = new LedgerInitSettingData();
		initSetting.setLedgerSeed(ledgerProps.getLedgerSeed());
		initSetting.setCryptoSetting(cryptoSetting);
		ConsensusParticipantConfig[] parties = ledgerProps.getConsensusParticipantArray();
		ConsensusParticipantConfig[] orderedParties = sortAndVerify(parties);
		initSetting.setConsensusParticipants(orderedParties);

		// 创建默认的共识配置；
		try {
			// ConsensusConfig csConfig = new ConsensusConfig();
			byte[] csSettingBytes = consensusProvider.getSettingsFactory().getConsensusSettingsEncoder()
					.encode(csSettings);
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
		SignatureDigest testSign = CryptoUtils.sign(SIGN_ALG).sign(privKey, testBytes);
		PubKey myPubKey = orderedParties[currentId].getPubKey();
		if (!CryptoUtils.sign(SIGN_ALG).verify(testSign, myPubKey, testBytes)) {
			throw new LedgerInitException("Your pub-key specified in the init-settings isn't match your priv-key!");
		}
		this.initializerAddresses = new NetworkAddress[orderedParties.length];
		// 记录每个参与方的账本初始化服务地址；
		for (int i = 0; i < orderedParties.length; i++) {
			initializerAddresses[i] = orderedParties[i].getInitializerAddress();
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
		SignatureDigest permissionSign = TxRequestBuilder.sign(initTxContent, privKey);
		LedgerInitPermissionData permission = new LedgerInitPermissionData(currentId, permissionSign);

		this.currentId = currentId;
		this.permissions = new LedgerInitPermission[initSetting.getConsensusParticipants().length];
		this.permissions[currentId] = permission;
		this.localPermission = permission;

		return permission;
	}

	private LedgerInitDecision makeDecision(int participantId, HashDigest ledgerHash, PrivKey privKey) {
		byte[] dataBytes = getDecisionBytes(participantId, ledgerHash);
		SignatureDigest signature = CryptoUtils.sign(privKey.getAlgorithm()).sign(privKey, dataBytes);

		LedgerInitDecisionData decision = new LedgerInitDecisionData();
		decision.setParticipantId(participantId);
		decision.setLedgerHash(ledgerHash);
		decision.setSignature(signature);
		return decision;
	}

	private LedgerBlock initLedgerDataset(LedgerEditor ledgerEditor) {
		// 初始化时，自动将参与方注册为账本的用户；
		TxRequestBuilder txReqBuilder = new TxRequestBuilder(this.initTxContent);
		ParticipantNode[] parties = this.ledgerInitSetting.getConsensusParticipants();
		for (int i = 0; i < parties.length; i++) {
			PubKey pubKey = parties[i].getPubKey();
			SignatureDigest signDigest = this.permissions[i].getTransactionSignature();
			DigitalSignatureBlob digitalSignature = new DigitalSignatureBlob(pubKey, signDigest);
			txReqBuilder.addNodeSignature(digitalSignature);
		}
		TransactionRequest txRequest = txReqBuilder.buildRequest();

		LedgerTransactionContext txCtx = ledgerEditor.newTransaction(txRequest);
		Operation[] ops = txRequest.getTransactionContent().getOperations();
		// 注册用户； 注：第一个操作是 LedgerInitOperation；
		// TODO：暂时只支持注册用户的初始化操作；
		for (int i = 1; i < ops.length; i++) {
			UserRegisterOperation userRegOP = (UserRegisterOperation) ops[i];
			txCtx.getDataSet().getUserAccountSet().register(userRegOP.getUserID().getAddress(),
					userRegOP.getUserID().getPubKey());
		}

		txCtx.commit(TransactionState.SUCCESS);

		return ledgerEditor.prepare();
	}

	/**
	 * 请求所有其它参与方的账本创建许可；
	 * 
	 * @param privKey
	 * @return
	 */
	private boolean startRequestPermissions(int currentId, PrivKey privKey) {
		SignatureDigest reqAuthSign = signPermissionRequest(currentId, privKey);

		ParticipantNode[] participants = ledgerInitSetting.getConsensusParticipants();

		// 异步请求结果列表；不包括已经获得许可的参与方；
		InvocationResult<?>[] results = new InvocationResult<?>[participants.length];
		int unpermittedCount = 0;
		for (int i = 0; i < participants.length; i++) {
			if (this.permissions[i] == null) {
				unpermittedCount++;
			}
		}

		// 发起请求；
		CountDownLatch latch = new CountDownLatch(unpermittedCount);
		for (int i = 0; i < participants.length; i++) {
			if (this.permissions[i] == null) {
				results[i] = doRequestPermission(participants[i].getId(), reqAuthSign, latch);
			}
		}

		// 等待结果；
		try {
			while (!latch.await(5000, TimeUnit.MILLISECONDS)) {
				List<String> waitingIds = new ArrayList<>();
				for (int i = 0; i < results.length; i++) {
					if (results[i] != null) {
						if (results[i].getResult() == null) {
							waitingIds.add("" + (i + 1));
						}
					}
				}

				prompter.info("\r\nWaiting for permissions of participants[%s] ...", String.join(",", waitingIds));
			}
		} catch (InterruptedException e) {
			throw new LedgerInitException(
					"Process of requesting participant permissions was interrupted! --" + e.getMessage(), e);
		}

		// 校验接入许可；
		boolean allPermitted = true;
		for (int i = 0; i < results.length; i++) {
			if (results[i] == null) {// 忽略自己；
				continue;
			}
			if (results[i].getError() != null) {
				prompter.error(results[i].getError(),
						"Error occurred on requesting permission from participant[Id=%s, name=%s]!",
						participants[i].getAddress(), participants[i].getName());
				allPermitted = false;
				continue;
			}
			PubKey pubKey = participants[i].getPubKey();
			LedgerInitPermission permission = (LedgerInitPermission) results[i].getResult();
			if (permission.getParticipantId() != participants[i].getId()) {
				prompter.error("\r\nThe id of received permission isn't equal to it's participant ! --[Id=%s][name=%s]",
						participants[i].getAddress(), participants[i].getName());
				allPermitted = false;
				continue;
			}

			if (!TxRequestBuilder.verifySignature(this.initTxContent, permission.getTransactionSignature(), pubKey)) {
				prompter.error("Invalid permission from participant! --[Id=%s][name=%s]", participants[i].getAddress(),
						participants[i].getName());
				allPermitted = false;
				continue;
			}
			this.permissions[i] = permission;
		}
		return allPermitted;
	}

	private byte[] getDecisionBytes(int participantId, HashDigest ledgerHash) {
		return BytesUtils.concat(BytesUtils.toBytes(participantId), ledgerHash.toBytes());
	}

	private LedgerInitConsensusService connectToParticipant(int participantId) {
		return initCsServiceFactory.connect(this.initializerAddresses[participantId]);
	}

	public SignatureDigest signPermissionRequest(int requesterId, PrivKey privKey) {
		byte[] reqAuthBytes = BytesUtils.concat(BytesUtils.toBytes(requesterId), ledgerInitSetting.getLedgerSeed());
		SignatureDigest reqAuthSign = CryptoUtils.sign(SIGN_ALG).sign(privKey, reqAuthBytes);
		return reqAuthSign;
	}

	/**
	 * 向指定的参与方请求其对于账本初始化参数的许可签名；
	 * 
	 * @param targetId
	 * @param reqAuthSign
	 * @param latch
	 * @return
	 */
	private InvocationResult<LedgerInitPermission> doRequestPermission(int targetId, SignatureDigest reqAuthSign,
			CountDownLatch latch) {
		InvocationResult<LedgerInitPermission> result = new InvocationResult<>();
		try {
			LedgerInitConsensusService initConsensus = connectToParticipant(targetId);
			Thread thrd = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						LedgerInitPermission permission = initConsensus.requestPermission(currentId, reqAuthSign);
						result.setResult(permission);
					} catch (Exception e) {
						result.setError(e);
					} finally {
						latch.countDown();
					}
				}
			});
			thrd.start();
		} catch (Exception e) {
			result.setError(e);
		}
		return result;
	}

	@RequestMapping(path = "/legerinit/permission/{requesterId}", method = RequestMethod.POST, produces = LedgerInitMessageConverter.CONTENT_TYPE_VALUE, consumes = LedgerInitMessageConverter.CONTENT_TYPE_VALUE)
	@Override
	public LedgerInitPermission requestPermission(@PathVariable(name = "requesterId") int requesterId,
			@RequestBody SignatureDigest signature) {
		if (requesterId == currentId) {
			throw new LedgerInitException("There is a id conflict!");
		}
		int retry = 0;
		while (currentId == -1 || ledgerInitSetting == null || localPermission == null) {
			// 本地尚未完成初始化；
			if (retry < 30) {
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					// ignore interrupted exception;
				}
			} else {
				return null;
			}
			retry++;
		}

		ParticipantNode[] participants = ledgerInitSetting.getConsensusParticipants();
		if (requesterId < 0 || requesterId >= participants.length) {
			throw new LedgerInitException("The id of requester is out of the bound of participant list!");
		}
		byte[] requestCodeBytes = BytesUtils.concat(BytesUtils.toBytes(requesterId), ledgerInitSetting.getLedgerSeed());
		PubKey requesterPubKey = participants[requesterId].getPubKey();
		if (!CryptoUtils.sign(SIGN_ALG).verify(signature, requesterPubKey, requestCodeBytes)) {
			throw new LedgerInitException("The requester signature is invalid!");
		}
		return localPermission;
	}

	/**
	 * 开始请求所有成员的账本创建决定；
	 */
	private boolean startRequestDecisions(PrivKey privKey, Prompter prompter) {
		// 进行随机化选择请求的目标列表的顺序；
		Random rand = new Random();
		DecisionResultHandle[] randDecHdls = Arrays.copyOf(this.decisions, this.decisions.length);
		DecisionResultHandle temp;
		for (int i = 0; i < randDecHdls.length; i++) {
			int a = rand.nextInt(randDecHdls.length);
			int b = rand.nextInt(randDecHdls.length);
			temp = randDecHdls[a];
			randDecHdls[a] = randDecHdls[b];
			randDecHdls[b] = temp;
		}

		// 请求；
		boolean allDecided = false;
		while (!allDecided) {
			allDecided = true;
			for (int i = 0; i < randDecHdls.length; i++) {
				if (randDecHdls[i].getResult() != null) {
					// 忽略当前参与方自己(在初始化“决定”时已经置为非空)，以及已经收到主动提交“决定”的参与方;
					continue;
				}
				boolean decided = doSynchronizeDecision(randDecHdls[i].PARTICIPANT_ID, privKey, randDecHdls[i],
						prompter);
				allDecided = allDecided & decided;
			}
			if (!allDecided) {
				String r = prompter.confirm(
						"\r\nSome decisions were rejected! Do you retry again? Press 'Y' to retry, any others to quit. :>");
				if (!"Y".equalsIgnoreCase(r)) {
					return false;
				}
			}
		}

		return allDecided;
	}

	/**
	 * 与指定的参与方同步“账本初始化决定({@link LedgerInitDecision})”；
	 * 
	 * @param targetId
	 * @param privKey
	 * @param resultHandle
	 * @param prompter
	 * @return
	 */
	private boolean doSynchronizeDecision(int targetId, PrivKey privKey, DecisionResultHandle resultHandle,
			Prompter prompter) {
		try {
			LedgerInitConsensusService initConsensus = connectToParticipant(targetId);
			LedgerInitDecision targetDecision = null;
			int retry = 0;
			do {
				prompter.info("Start synchronizling decision from participant[%s] to participant[%s] ......", currentId,
						targetId);
				try {
					targetDecision = initConsensus.synchronizeDecision(localDecision);
				} catch (Exception e1) {
					prompter.info("Error occurred on synchronizing decision . --%s", e1.getMessage());
				}
				if (targetDecision == null) {
					if (resultHandle.getResult() != null) {
						// 已经验证过；
						return true;
					}
					// 对方的账本初始化尚未就绪；隔5秒重试；重试超过3后提示确认是否继续重试；
					if (retry == 16) {
						String r = prompter.confirm(
								"Target participant[%s] isn't ready to do decision! Do you want to retry again?\r\n"
										+ " Press 'Y' to retry, and any others to break this participant and to continue synchronizing with others. :>",
								targetId);
						if (!"Y".equalsIgnoreCase(r)) {
							return false;
						}
						retry = 0;
					}
					prompter.info(
							"Target participant[%s] isn't ready to do decision! Waiting 5 seconds and retry again...",
							targetId);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// ignore InterruptedException;
					}
					retry++;
				}
			} while (targetDecision == null);

			if (targetDecision.getParticipantId() != targetId && resultHandle.getResult() == null) {
				prompter.error(
						"The received id of participant isn't equal to id of request target participant! --[Id=%s]",
						targetId);
				return false;
			}

			return validateAndRecordDecision(targetDecision, resultHandle, privKey.getAlgorithm());
		} catch (Exception e) {
			prompter.error(e, "Error occurred on synchronizing decision from participant[%s] to participant[%s] ! --%s",
					currentId, targetId, e.getMessage());
			return false;
		}
	}

	/**
	 * 校验并记录指定的参与方做出的决定；
	 * <p>
	 * 注：对 {@link DecisionResultHandle#setResult(LedgerInitDecision)}
	 * 方法的调用不是线程安全的，但由于是在满足有效性校验之后才调用，具有幂等性，所以不必对该方法的多处调用进行同步；
	 * 
	 * @param targetDecision
	 * @param resultHandle
	 * @param hashAlgorithm
	 * @return
	 */
	private synchronized boolean validateAndRecordDecision(LedgerInitDecision targetDecision,
			DecisionResultHandle resultHandle, CryptoAlgorithm hashAlgorithm) {
		if ((!localDecision.getLedgerHash().equals(targetDecision.getLedgerHash()))
				&& resultHandle.getResult() == null) {
			// 如果结果已经被
			prompter.error(
					"The received ledger hash of participant isn't equal to ledger hash of current participant! --[Id=%s]",
					targetDecision.getParticipantId());
			return false;
		}

		// 检查签名；
		PubKey targetPubKey = ledgerInitSetting.getConsensusParticipants()[targetDecision.getParticipantId()]
				.getPubKey();
		byte[] deciBytes = getDecisionBytes(targetDecision.getParticipantId(), targetDecision.getLedgerHash());
		if ((!CryptoUtils.sign(hashAlgorithm).verify(targetDecision.getSignature(), targetPubKey, deciBytes))
				&& resultHandle.getResult() == null) {
			prompter.error("The signature of received decision is invalid! --[Id=%s]",
					targetDecision.getParticipantId());
			return false;
		}

		resultHandle.setResult(targetDecision);
		return true;
	}

	@RequestMapping(path = "/legerinit/decision", method = RequestMethod.POST, produces = LedgerInitMessageConverter.CONTENT_TYPE_VALUE, consumes = LedgerInitMessageConverter.CONTENT_TYPE_VALUE)
	@Override
	public LedgerInitDecision synchronizeDecision(@RequestBody LedgerInitDecision initDecision) {
		int remoteId = initDecision.getParticipantId();
		if (remoteId == currentId) {
			throw new LedgerInitException(
					String.format("Reject decision because of self-synchronization! --[Id=%s]", remoteId));
		}
		if (this.genesisBlock == null) {
			// 当前参与者尚未准备就绪,返回 null；
			return null;
		}
		PubKey pubKey = ledgerInitSetting.getConsensusParticipants()[remoteId].getPubKey();
		DecisionResultHandle resultHandle = this.decisions[remoteId];
		if (!validateAndRecordDecision(initDecision, resultHandle, pubKey.getAlgorithm())) {
			// 签名无效；
			throw new LedgerInitException(
					String.format("Reject decision because of invalid signature! --[Id=%s]", remoteId));
		}
		return localDecision;
	}

	/**
	 * 对参与者列表按照 id 进行升序排列，并校验id是否从 1 开始且没有跳跃；
	 * 
	 * @param parties
	 * @return
	 */
	private ConsensusParticipantConfig[] sortAndVerify(ConsensusParticipantConfig[] parties) {
		Arrays.sort(parties, new Comparator<ConsensusParticipantConfig>() {
			@Override
			public int compare(ConsensusParticipantConfig o1, ConsensusParticipantConfig o2) {
				return o1.getId() - o2.getId();
			}
		});
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
