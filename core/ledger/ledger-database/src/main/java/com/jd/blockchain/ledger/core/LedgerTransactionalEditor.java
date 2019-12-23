package com.jd.blockchain.ledger.core;

import java.util.List;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockBody;
import com.jd.blockchain.ledger.BlockRollbackException;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.IllegalTransactionException;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerDataSnapshot;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerSettings;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRollbackException;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.utils.BufferedKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.codec.Base58Utils;

public class LedgerTransactionalEditor implements LedgerEditor {

	private static final boolean PARALLEL_DB_WRITE;

	static {
		PARALLEL_DB_WRITE = Boolean.getBoolean("parallel-dbwrite");
		System.out.println("------ [[ parallel-dbwrite=" + PARALLEL_DB_WRITE + " ]] ------");
	}

	/**
	 * 账本Hash，创世区块的编辑器则返回 null；
	 */
	private HashDigest ledgerHash;

	private final String ledgerKeyPrefix;

	private CryptoSetting cryptoSetting;

	private LedgerBlockData currentBlock;

//	private Stack<StagedSnapshot> stagedSnapshots = new Stack<>();

	private boolean prepared = false;

	private boolean canceled = false;

	private boolean committed = false;

	private StagedSnapshot startingPoint;

	/**
	 * 当前区块的存储；
	 */
	private BufferedKVStorage baseStorage;

	/**
	 * 上一个交易产生的账本快照；
	 */
	private TxSnapshot previousTxSnapshot;

	/**
	 * 当前交易的上下文；
	 */
	private volatile LedgerTransactionContextImpl currentTxCtx;

	/**
	 * 最后提交的账本数据集；
	 */
	private volatile LedgerDataset latestLedgerDataset;

	/**
	 * 最后提交的交易集合；
	 */
	private volatile TransactionSet latestTransactionSet;

	/**
	 * @param ledgerHash
	 * @param cryptoSetting
	 * @param currentBlock
	 * @param startingPoint
	 * @param ledgerKeyPrefix
	 * @param bufferedStorage
	 * @param verifyTx        是否校验交易请求；当外部调用者在调用前已经实施了验证时，将次参数设置为 false 能够提升性能；
	 */
	private LedgerTransactionalEditor(HashDigest ledgerHash, CryptoSetting cryptoSetting, LedgerBlockData currentBlock,
			StagedSnapshot startingPoint, String ledgerKeyPrefix, BufferedKVStorage bufferedStorage) {
		this.ledgerHash = ledgerHash;
		this.ledgerKeyPrefix = ledgerKeyPrefix;
		this.cryptoSetting = cryptoSetting;
		this.currentBlock = currentBlock;
		this.baseStorage = bufferedStorage;

		this.startingPoint = startingPoint;

//		this.stagedSnapshots.push(startingPoint);
	}

	/**
	 * 创建账本新区块的编辑器；
	 * 
	 * @param ledgerHash       账本哈希；
	 * @param ledgerSetting    账本设置；
	 * @param previousBlock    前置区块；
	 * @param ledgerKeyPrefix  账本数据前缀；
	 * @param ledgerExStorage  账本数据存储；
	 * @param ledgerVerStorage 账本数据版本化存储；
	 * @param verifyTx         是否校验交易请求；当外部调用者在调用前已经实施了验证时，将次参数设置为 false 能够提升性能；
	 * @return
	 */
	public static LedgerTransactionalEditor createEditor(LedgerBlock previousBlock, LedgerSettings ledgerSetting,
			String ledgerKeyPrefix, ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage) {
		// new block;
		HashDigest ledgerHash = previousBlock.getLedgerHash();
		if (ledgerHash == null) {
			ledgerHash = previousBlock.getHash();
		}
		if (ledgerHash == null) {
			throw new IllegalArgumentException("Illegal previous block was specified!");
		}
		LedgerBlockData currBlock = new LedgerBlockData(previousBlock.getHeight() + 1, ledgerHash,
				previousBlock.getHash());

		// init storage;
		BufferedKVStorage txStagedStorage = new BufferedKVStorage(ledgerExStorage, ledgerVerStorage, PARALLEL_DB_WRITE);

		StagedSnapshot startingPoint = new TxSnapshot(previousBlock, previousBlock.getTransactionSetHash());

		// instantiate editor;
		return new LedgerTransactionalEditor(ledgerHash, ledgerSetting.getCryptoSetting(), currBlock, startingPoint,
				ledgerKeyPrefix, txStagedStorage);
	}

	/**
	 * 创建创世区块的编辑器；
	 * 
	 * @param initSetting
	 * @param ledgerKeyPrefix
	 * @param ledgerExStorage
	 * @param ledgerVerStorage
	 * @param verifyTx         是否校验交易请求；当外部调用者在调用前已经实施了验证时，将次参数设置为 false 能够提升性能；
	 * @return
	 */
	public static LedgerTransactionalEditor createEditor(LedgerInitSetting initSetting, String ledgerKeyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage) {
		LedgerBlockData genesisBlock = new LedgerBlockData(0, null, null);
		StagedSnapshot startingPoint = new GenesisSnapshot(initSetting);
		// init storage;
		BufferedKVStorage txStagedStorage = new BufferedKVStorage(ledgerExStorage, ledgerVerStorage, false);
		return new LedgerTransactionalEditor(null, initSetting.getCryptoSetting(), genesisBlock, startingPoint,
				ledgerKeyPrefix, txStagedStorage);
	}

	private void commitTxSnapshot(TxSnapshot snapshot) {
		previousTxSnapshot = snapshot;
		latestLedgerDataset = currentTxCtx.getDataset();
		latestLedgerDataset.setReadonly();
		latestTransactionSet = currentTxCtx.getTransactionSet();
		latestTransactionSet.setReadonly();
		currentTxCtx = null;
	}

	private void rollbackCurrentTx() {
		currentTxCtx = null;
	}

	LedgerBlock getCurrentBlock() {
		return currentBlock;
	}

	@Override
	public long getBlockHeight() {
		return currentBlock.getHeight();
	}

	@Override
	public HashDigest getLedgerHash() {
		return ledgerHash;
	}

	@Override
	public LedgerDataset getLedgerDataset() {
		return latestLedgerDataset;
	}

	@Override
	public TransactionSet getTransactionSet() {
		return latestTransactionSet;
	}

	/**
	 * 检查当前账本是否是指定交易请求的账本；
	 * 
	 * @param txRequest
	 * @return
	 */
	private boolean isRequestMatched(TransactionRequest txRequest) {
		HashDigest reqLedgerHash = txRequest.getTransactionContent().getLedgerHash();
		if (ledgerHash == reqLedgerHash) {
			return true;
		}
		if (ledgerHash == null || reqLedgerHash == null) {
			return false;
		}
		return ledgerHash.equals(reqLedgerHash);
	}

	/**
	 * 注：此方法不验证交易完整性和签名有效性，仅仅设计为进行交易记录的管理；调用者应在此方法之外进行数据完整性和签名有效性的检查；
	 */
	@Override
	public synchronized LedgerTransactionContext newTransaction(TransactionRequest txRequest) {
//		if (SettingContext.txSettings().verifyLedger() && !isRequestMatched(txRequest)) {
		if (!isRequestMatched(txRequest)) {
			throw new IllegalTransactionException(
					"Transaction request is dispatched to a wrong ledger! --[TxHash="
							+ txRequest.getTransactionContent().getHash() + "]!",
					TransactionState.IGNORED_BY_WRONG_LEDGER);
		}

		if (currentTxCtx != null) {
			throw new IllegalStateException(
					"Unable to open another new transaction before the current transaction is completed! --[TxHash="
							+ txRequest.getTransactionContent().getHash() + "]!");
		}

		// 检查状态是否允许创建新的交易请求；；
		checkState();

		// init storage of new transaction;
		BufferedKVStorage txBufferedStorage = new BufferedKVStorage(baseStorage, baseStorage, false);

		LedgerDataset txDataset = null;
		TransactionSet txset = null;
		if (previousTxSnapshot == null) {
			// load the starting point of the new transaction;
			if (startingPoint instanceof GenesisSnapshot) {
				// 准备生成创世区块；
				GenesisSnapshot snpht = (GenesisSnapshot) startingPoint;
				txDataset = LedgerRepositoryImpl.newDataSet(snpht.initSetting, ledgerKeyPrefix, txBufferedStorage,
						txBufferedStorage);
				txset = LedgerRepositoryImpl.newTransactionSet(txDataset.getAdminDataset().getSettings(),
						ledgerKeyPrefix, txBufferedStorage, txBufferedStorage);
			} else if (startingPoint instanceof TxSnapshot) {
				// 新的区块；
				// TxSnapshot; reload dataset and txset;
				TxSnapshot snpht = (TxSnapshot) startingPoint;
				// load dataset;
				txDataset = LedgerRepositoryImpl.loadDataSet(snpht.dataSnapshot, cryptoSetting, ledgerKeyPrefix,
						txBufferedStorage, txBufferedStorage, false);

				// load txset;
				txset = LedgerRepositoryImpl.loadTransactionSet(snpht.txsetHash, cryptoSetting, ledgerKeyPrefix,
						txBufferedStorage, txBufferedStorage, false);
			} else {
				// Unreachable;
				throw new IllegalStateException("Unreachable code was accidentally executed!");
			}

		} else {
			// Reuse previous object to optimize performance;
			// load dataset;
			txDataset = LedgerRepositoryImpl.loadDataSet(previousTxSnapshot.dataSnapshot, cryptoSetting,
					ledgerKeyPrefix, txBufferedStorage, txBufferedStorage, false);

			// load txset;
			txset = LedgerRepositoryImpl.loadTransactionSet(previousTxSnapshot.txsetHash, cryptoSetting,
					ledgerKeyPrefix, txBufferedStorage, txBufferedStorage, false);
		}

		currentTxCtx = new LedgerTransactionContextImpl(txRequest, txDataset, txset, txBufferedStorage, this);

		return currentTxCtx;
	}

	@Override
	public LedgerBlock prepare() {
		checkState();

		if (currentTxCtx != null) {
			// 有进行中的交易尚未提交或回滚；
			throw new IllegalStateException(
					"There is an ongoing transaction that has been not committed or rolled back!");
		}
		if (previousTxSnapshot == null) {
			// 当前区块没有加入过交易，不允许产生空区块；
			throw new IllegalStateException(
					"There is no transaction in the current block, and no empty blocks is allowed!");
		}

		// do commit when transaction isolation level is BLOCK;
		currentBlock.setAdminAccountHash(previousTxSnapshot.getAdminAccountHash());
		currentBlock.setUserAccountSetHash(previousTxSnapshot.getUserAccountSetHash());
		currentBlock.setDataAccountSetHash(previousTxSnapshot.getDataAccountSetHash());
		currentBlock.setContractAccountSetHash(previousTxSnapshot.getContractAccountSetHash());
		currentBlock.setTransactionSetHash(previousTxSnapshot.getTransactionSetHash());

		// TODO: 根据所有交易的时间戳的平均值来生成区块的时间戳；
//		long timestamp = 
//		currentBlock.setTimestamp(timestamp);

		// compute block hash;
		byte[] blockBodyBytes = BinaryProtocol.encode(currentBlock, BlockBody.class);
		HashDigest blockHash = Crypto.getHashFunction(cryptoSetting.getHashAlgorithm()).hash(blockBodyBytes);
		currentBlock.setHash(blockHash);

//		if (currentBlock.getLedgerHash() == null) {
//			// init GenesisBlock's ledger hash;
//			currentBlock.setLedgerHash(blockHash);
//		}

		// persist block bytes;
		// only one version per block;
		byte[] blockBytes = BinaryProtocol.encode(currentBlock, LedgerBlock.class);
		Bytes blockStorageKey = LedgerRepositoryImpl.encodeBlockStorageKey(currentBlock.getHash());
		long v = baseStorage.set(blockStorageKey, blockBytes, -1);
		if (v < 0) {
			throw new IllegalStateException(
					"Block already exist! --[BlockHash=" + Base58Utils.encode(currentBlock.getHash().toBytes()) + "]");
		}

		// persist block hash to ledger index;
		HashDigest ledgerHash = currentBlock.getLedgerHash();
		if (ledgerHash == null) {
			ledgerHash = blockHash;
		}
		Bytes ledgerIndexKey = LedgerRepositoryImpl.encodeLedgerIndexKey(ledgerHash);
		long expectedVersion = currentBlock.getHeight() - 1;
		v = baseStorage.set(ledgerIndexKey, currentBlock.getHash().toBytes(), expectedVersion);
		if (v < 0) {
			throw new IllegalStateException(
					String.format("Index of BlockHash already exist! --[BlockHeight=%s][BlockHash=%s]",
							currentBlock.getHeight(), currentBlock.getHash()));
		}

		prepared = true;
		return currentBlock;
	}

	@Override
	public void commit() {
		if (committed) {
			throw new IllegalStateException("The current block has been committed!");
		}
		if (canceled) {
			throw new IllegalStateException("The current block has been canceled!");
		}
		if (!prepared) {
			// 未就绪；
			throw new IllegalStateException("The current block is not ready yet!");
		}

		try {
			baseStorage.flush();
		} catch (Exception e) {
			throw new BlockRollbackException(e.getMessage(), e);
		}

		committed = true;
	}

	@Override
	public void cancel() {
		if (committed) {
			throw new IllegalStateException("The current block has been committed!");
		}
		if (canceled) {
			return;
		}

		canceled = true;

		baseStorage.cancel();
	}

	private void checkState() {
		if (prepared) {
			throw new IllegalStateException("The current block is ready!");
		}
		if (committed) {
			throw new IllegalStateException("The current block has been committed!");
		}
		if (canceled) {
			throw new IllegalStateException("The current block has been canceled!");
		}
	}

	// --------------------------- inner type --------------------------

	/**
	 * 用于暂存交易上下文数据的快照对象；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static interface StagedSnapshot {

	}

	/**
	 * 创世区块的快照对象；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class GenesisSnapshot implements StagedSnapshot {

		private LedgerInitSetting initSetting;

		public GenesisSnapshot(LedgerInitSetting initSetting) {
			this.initSetting = initSetting;
		}
	}

	/**
	 * 交易执行完毕后的快照对象；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class TxSnapshot implements StagedSnapshot {

		/**
		 * 账本数据的快照；
		 */
		private LedgerDataSnapshot dataSnapshot;

		/**
		 * 交易集合的快照（根哈希）；
		 */
		private HashDigest txsetHash;

		public HashDigest getAdminAccountHash() {
			return dataSnapshot.getAdminAccountHash();
		}

		public HashDigest getUserAccountSetHash() {
			return dataSnapshot.getUserAccountSetHash();
		}

		public HashDigest getDataAccountSetHash() {
			return dataSnapshot.getDataAccountSetHash();
		}

		public HashDigest getContractAccountSetHash() {
			return dataSnapshot.getContractAccountSetHash();
		}

		public HashDigest getTransactionSetHash() {
			return txsetHash;
		}

		public TxSnapshot(LedgerDataSnapshot dataSnapshot, HashDigest txsetHash) {
			this.dataSnapshot = dataSnapshot;
			this.txsetHash = txsetHash;
		}

	}

	/**
	 * 交易的上下文；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class LedgerTransactionContextImpl implements LedgerTransactionContext {

		private LedgerTransactionalEditor blockEditor;

		private TransactionRequest txRequest;

		private LedgerDataset dataset;

		private TransactionSet txset;

		private BufferedKVStorage storage;

		private boolean committed = false;

		private boolean rollbacked = false;

		private LedgerTransaction transaction;

		private HashDigest txRootHash;

		private LedgerTransactionContextImpl(TransactionRequest txRequest, LedgerDataset dataset,
				TransactionSet txset, BufferedKVStorage storage, LedgerTransactionalEditor editor) {
			this.txRequest = txRequest;
			this.dataset = dataset;
			this.txset = txset;
			this.storage = storage;
			this.blockEditor = editor;
		}

		@Override
		public LedgerDataset getDataset() {
			return dataset;
		}

		@Override
		public TransactionSet getTransactionSet() {
			return txset;
		}

		@Override
		public TransactionRequest getTransactionRequest() {
			return txRequest;
		}

		@Override
		public LedgerTransaction commit(TransactionState txResult) {
			return commit(txResult, null);
		}

		@Override
		public LedgerTransaction commit(TransactionState txResult, List<OperationResult> operationResults) {
			checkTxState();

			// capture snapshot
			this.dataset.commit();
			TransactionStagedSnapshot txDataSnapshot = takeDataSnapshot();

			LedgerTransactionData tx;
			try {
				tx = new LedgerTransactionData(blockEditor.getBlockHeight(), txRequest, txResult, txDataSnapshot,
						operationResultArray(operationResults));
				this.txset.add(tx);
				this.txset.commit();
			} catch (Exception e) {
				throw new TransactionRollbackException(e.getMessage(), e);
			}

			try {
				this.storage.flush();
			} catch (Exception e) {
				throw new BlockRollbackException(e.getMessage(), e);
			}

			// put snapshot into stack;
			TxSnapshot snapshot = new TxSnapshot(txDataSnapshot, txset.getRootHash());
			blockEditor.commitTxSnapshot(snapshot);

			committed = true;
			return tx;
		}

		@Override
		public LedgerTransaction discardAndCommit(TransactionState txResult) {
			return discardAndCommit(txResult, null);
		}

		@Override
		public LedgerTransaction discardAndCommit(TransactionState txResult, List<OperationResult> operationResults) {
			checkTxState();

			// 未处理
			dataset.cancel();

			TransactionStagedSnapshot txDataSnapshot = takeDataSnapshot();

			LedgerTransactionData tx;
			try {
				tx = new LedgerTransactionData(blockEditor.getBlockHeight(), txRequest, txResult, txDataSnapshot,
						operationResultArray(operationResults));
				this.txset.add(tx);
				this.txset.commit();
			} catch (Exception e) {
				throw new TransactionRollbackException(e.getMessage(), e);
			}

			try {
				this.storage.flush();
			} catch (Exception e) {
				throw new BlockRollbackException(e.getMessage(), e);
			}

			// put snapshot into stack;
			TxSnapshot snapshot = new TxSnapshot(txDataSnapshot, txset.getRootHash());
			blockEditor.commitTxSnapshot(snapshot);

			committed = true;
			return tx;
		}

		private TransactionStagedSnapshot takeDataSnapshot() {
			TransactionStagedSnapshot txDataSnapshot = new TransactionStagedSnapshot();
			txDataSnapshot.setAdminAccountHash(dataset.getAdminDataset().getHash());
			txDataSnapshot.setContractAccountSetHash(dataset.getContractAccountset().getRootHash());
			txDataSnapshot.setDataAccountSetHash(dataset.getDataAccountSet().getRootHash());
			txDataSnapshot.setUserAccountSetHash(dataset.getUserAccountSet().getRootHash());
			return txDataSnapshot;
		}

		private OperationResult[] operationResultArray(List<OperationResult> operationResults) {
			OperationResult[] operationResultArray = null;
			if (operationResults != null && !operationResults.isEmpty()) {
				operationResultArray = new OperationResult[operationResults.size()];
				operationResults.toArray(operationResultArray);
			}
			return operationResultArray;
		}

		@Override
		public void rollback() {
			if (this.rollbacked) {
				return;
			}
			if (this.committed) {
				throw new IllegalStateException("This transaction had been committed!");
			}
			dataset.cancel();
			storage.cancel();

			blockEditor.rollbackCurrentTx();

			rollbacked = true;
		}

		private void checkTxState() {
			if (this.committed) {
				throw new IllegalStateException("This transaction had been committed!");
			}
			if (this.rollbacked) {
				throw new IllegalStateException("This transaction had been rollbacked!");
			}
		}
	}

}