package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerAdminSettings;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitException;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.RoleInitSettings;
import com.jd.blockchain.ledger.RolesConfigureOperation;
import com.jd.blockchain.ledger.SecurityInitSettings;
import com.jd.blockchain.ledger.TransactionBuilder;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.UserAuthInitSettings;
import com.jd.blockchain.ledger.UserAuthorizeOperation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.transaction.SignatureUtils;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.transaction.TxRequestBuilder;

public class LedgerInitializer {

	private static final FullPermissionedSecurityManager FULL_PERMISSION_SECURITY_MANAGER = new FullPermissionedSecurityManager();
	
	private static final LedgerQuery EMPTY_LEDGER =new EmptyLedgerQuery();

	private static final LedgerDataQuery EMPTY_LEDGER_DATA_QUERY = new EmptyLedgerDataset();

	private static final OperationHandleRegisteration DEFAULT_OP_HANDLE_REG = new DefaultOperationHandleRegisteration();

//	private LedgerService EMPTY_LEDGERS = new LedgerManager();

	private LedgerInitSetting initSetting;

	private TransactionContent initTxContent;

	private volatile LedgerBlock genesisBlock;

	private volatile LedgerEditor ledgerEditor;

	private volatile boolean committed = false;

	private volatile boolean canceled = false;

	private TransactionBatchResultHandle txResultsHandle;

	/**
	 * 初始化生成的账本hash； <br>
	 * 
	 * 在成功执行 {@link #prepareLedger(KVStorageService, DigitalSignature...)} 之前总是返回
	 * null；
	 * 
	 * @return
	 */
	public HashDigest getLedgerHash() {
		return genesisBlock == null ? null : genesisBlock.getHash();
	}

	/**
	 * @param initSetting
	 * @param initTxContent
	 */
	private LedgerInitializer(LedgerInitSetting initSetting, TransactionContent initTxContent) {
		this.initSetting = initSetting;
		this.initTxContent = initTxContent;
	}

	public TransactionContent getTransactionContent() {
		return initTxContent;
	}

	public static LedgerInitializer create(LedgerInitSetting initSetting, SecurityInitSettings securityInitSettings) {
		// 生成创世交易；
		TransactionContent initTxContent = buildGenesisTransaction(initSetting, securityInitSettings);

		return new LedgerInitializer(initSetting, initTxContent);
	}

	/**
	 * 根据初始化配置，生成创始交易；
	 * <p>
	 * 
	 * “创世交易”按顺序由以下操作组成：<br>
	 * (1) 账本初始化 {@link LedgerInitOperation}：此操作仅用于锚定了原始的交易配置，对应的
	 * {@link OperationHandle} 执行空操作，由“创世交易”其余的操作来表达对账本的实际修改；<br>
	 * (2) 注册用户 {@link UserRegisterOperation}：有一项或者多项；<br>
	 * (3) 配置角色 {@link RolesConfigureOperation}：有一项或者多项；<br>
	 * (4) 授权用户 {@link UserAuthorizeOperation}：有一项或者多项；<br>
	 * 
	 * @param initSetting
	 * @param securityInitSettings
	 * @return
	 */
	public static TransactionContent buildGenesisTransaction(LedgerInitSetting initSetting,
			SecurityInitSettings securityInitSettings) {
		// 账本初始化交易的账本 hash 为 null；
		TransactionBuilder initTxBuilder = new TxBuilder(null);

		// 定义账本初始化操作；
		initTxBuilder.ledgers().create(initSetting);

		// TODO: 注册参与方; 目前由 LedgerInitSetting 定义，在 LedgerAdminDataset 中解释执行；

		//  注册用户；
		for (ParticipantNode p : initSetting.getConsensusParticipants()) {
			// TODO：暂时只支持注册用户的初始化操作；
			BlockchainIdentity superUserId = new BlockchainIdentityData(p.getPubKey());
			initTxBuilder.users().register(superUserId);
		}

		// 配置角色；
		for (RoleInitSettings roleSettings : securityInitSettings.getRoles()) {
			initTxBuilder.security().roles().configure(roleSettings.getRoleName())
					.enable(roleSettings.getLedgerPermissions()).enable(roleSettings.getTransactionPermissions());
		}

		// 授权用户；
		for (UserAuthInitSettings userAuthSettings : securityInitSettings.getUserAuthorizations()) {
			initTxBuilder.security().authorziations().forUser(userAuthSettings.getUserAddress())
					.authorize(userAuthSettings.getRoles())
					.setPolicy(userAuthSettings.getPolicy());
		}

		// 账本初始化配置声明的创建时间来初始化交易时间戳；注：不能用本地时间，因为共识节点之间的本地时间系统不一致；
		return initTxBuilder.prepareContent(initSetting.getCreatedTime());
	}

	public SignatureDigest signTransaction(PrivKey privKey) {
		return SignatureUtils.sign(initTxContent, privKey);
	}
	
	public DigitalSignature signTransaction(BlockchainKeypair key) {
		return SignatureUtils.sign(initTxContent, key);
	}

	/**
	 * 准备创建账本；
	 * 
	 * @param storageService 存储服务；
	 * @param nodeSignatures 节点签名列表；
	 * @return
	 */
	public LedgerBlock prepareLedger(KVStorageService storageService, DigitalSignature... nodeSignatures) {
		if (genesisBlock != null) {
			throw new LedgerInitException("The ledger has been prepared!");
		}
		// 生成账本；
		this.ledgerEditor = createLedgerEditor(this.initSetting, storageService);
		this.genesisBlock = prepareLedger(ledgerEditor, nodeSignatures);

		return genesisBlock;
	}

	public void commit() {
		if (committed) {
			throw new LedgerInitException("The ledger has been committed!");
		}
		if (canceled) {
			throw new LedgerInitException("The ledger has been canceled!");
		}
		committed = true;
		this.txResultsHandle.commit();
	}

	public void cancel() {
		if (canceled) {
			throw new LedgerInitException("The ledger has been canceled!");
		}
		if (committed) {
			throw new LedgerInitException("The ledger has been committed!");
		}
		this.ledgerEditor.cancel();
	}

	public static LedgerEditor createLedgerEditor(LedgerInitSetting initSetting, KVStorageService storageService) {
		LedgerEditor genesisBlockEditor = LedgerTransactionalEditor.createEditor(initSetting,
				LedgerManage.LEDGER_PREFIX, storageService.getExPolicyKVStorage(),
				storageService.getVersioningKVStorage());
		return genesisBlockEditor;
	}

	/**
	 * 初始化账本数据，返回创始区块；
	 * 
	 * @param ledgerEditor
	 * @return
	 */
	private LedgerBlock prepareLedger(LedgerEditor ledgerEditor, DigitalSignature... nodeSignatures) {
		// 初始化时，自动将参与方注册为账本的用户；
		TxRequestBuilder txReqBuilder = new TxRequestBuilder(this.initTxContent);
		txReqBuilder.addNodeSignature(nodeSignatures);

		TransactionRequest txRequest = txReqBuilder.buildRequest();

		TransactionBatchProcessor txProcessor = new TransactionBatchProcessor(FULL_PERMISSION_SECURITY_MANAGER,
				ledgerEditor, EMPTY_LEDGER, DEFAULT_OP_HANDLE_REG);

		txProcessor.schedule(txRequest);

		txResultsHandle = txProcessor.prepare();
		return txResultsHandle.getBlock();
	}
	
	private static class EmptyLedgerQuery implements LedgerQuery{
		
		private EmptyLedgerDataset dataset;

		@Override
		public HashDigest getHash() {
			return null;
		}

		@Override
		public long getLatestBlockHeight() {
			return 0;
		}

		@Override
		public HashDigest getLatestBlockHash() {
			return null;
		}

		@Override
		public LedgerBlock getLatestBlock() {
			return null;
		}

		@Override
		public HashDigest getBlockHash(long height) {
			return null;
		}

		@Override
		public LedgerBlock getBlock(long height) {
			return null;
		}

		@Override
		public LedgerAdminInfo getAdminInfo() {
			return null;
		}

		@Override
		public LedgerAdminInfo getAdminInfo(LedgerBlock block) {
			return null;
		}
		
		@Override
		public LedgerAdminSettings getAdminSettings() {
			return null;
		}
		
		@Override
		public LedgerAdminSettings getAdminSettings(LedgerBlock block) {
			return null;
		}

		@Override
		public LedgerBlock getBlock(HashDigest hash) {
			return null;
		}

		@Override
		public LedgerDataQuery getLedgerData(LedgerBlock block) {
			return dataset;
		}

		@Override
		public TransactionQuery getTransactionSet(LedgerBlock block) {
			return null;
		}

		@Override
		public UserAccountQuery getUserAccountSet(LedgerBlock block) {
			return dataset.getUserAccountSet();
		}

		@Override
		public DataAccountQuery getDataAccountSet(LedgerBlock block) {
			return dataset.getDataAccountSet();
		}

		@Override
		public ContractAccountQuery getContractAccountSet(LedgerBlock block) {
			return dataset.getContractAccountset();
		}

		@Override
		public LedgerBlock retrieveLatestBlock() {
			return null;
		}

		@Override
		public long retrieveLatestBlockHeight() {
			return 0;
		}

		@Override
		public HashDigest retrieveLatestBlockHash() {
			return null;
		}
		
	}

}
