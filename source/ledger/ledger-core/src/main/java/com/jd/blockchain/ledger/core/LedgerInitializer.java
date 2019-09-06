package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitException;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.SecurityInitSettings;
import com.jd.blockchain.ledger.TransactionBuilder;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.transaction.SignatureUtils;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.transaction.TxRequestBuilder;

public class LedgerInitializer {

	private LedgerInitSetting initSetting;

	private TransactionContent initTxContent;

	private volatile LedgerBlock genesisBlock;

	private volatile LedgerEditor ledgerEditor;

	private volatile boolean committed = false;

	private volatile boolean canceled = false;

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

	private static SecurityInitSettings createDefaultSecurityInitSettings() {
		throw new IllegalStateException("Not implemented!");
	}

	public static LedgerInitializer create(LedgerInitSetting initSetting) {
		return create(initSetting, createDefaultSecurityInitSettings());
	}

	public static LedgerInitializer create(LedgerInitSetting initSetting, SecurityInitSettings securityInitSettings) {
		// 生成初始化交易；
		TransactionBuilder initTxBuilder = new TxBuilder(null);// 账本初始化交易的账本 hash 为 null；
		initTxBuilder.ledgers().create(initSetting);
		for (ParticipantNode p : initSetting.getConsensusParticipants()) {
			// TODO：暂时只支持注册用户的初始化操作；
			BlockchainIdentity superUserId = new BlockchainIdentityData(p.getPubKey());
			initTxBuilder.users().register(superUserId);
		}
		// 账本初始化配置声明的创建时间来初始化交易时间戳；注：不能用本地时间，因为共识节点之间的本地时间系统不一致；
		TransactionContent initTxContent = initTxBuilder.prepareContent(initSetting.getCreatedTime());

		return new LedgerInitializer(initSetting, initTxContent);
	}

	public SignatureDigest signTransaction(PrivKey privKey) {
		return SignatureUtils.sign(initTxContent, privKey);
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
		this.ledgerEditor.commit();
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
}
