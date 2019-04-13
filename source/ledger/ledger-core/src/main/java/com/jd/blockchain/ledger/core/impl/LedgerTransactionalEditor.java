package com.jd.blockchain.ledger.core.impl;

import java.util.Stack;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockBody;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerDataSnapshot;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerSetting;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.TransactionSet;
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

	private final String ledgerKeyPrefix;

	private CryptoSetting cryptoSetting;

	private LedgerBlockData newlyBlock;

	private Stack<StagedSnapshot> stagedSnapshots = new Stack<>();

	private boolean prepared = false;

	private boolean canceled = false;

	private boolean committed = false;

	private BufferedKVStorage bufferedStorage;

	/**
	 * 最近一个交易上下文；
	 */
	private LedgerDataContext lastTxCtx;

	private LedgerDataContext newTxCtx;

	private LedgerTransactionalEditor(CryptoSetting cryptoSetting, LedgerBlockData newlyBlock,
			StagedSnapshot startingPoint, String ledgerKeyPrefix, BufferedKVStorage bufferedStorage) {
		this.ledgerKeyPrefix = ledgerKeyPrefix;
		this.cryptoSetting = cryptoSetting;
		this.newlyBlock = newlyBlock;
		this.bufferedStorage = bufferedStorage;

		this.stagedSnapshots.push(startingPoint);
	}

	public static LedgerTransactionalEditor createEditor(LedgerSetting ledgerSetting, LedgerBlock previousBlock,
			String ledgerKeyPrefix, ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage) {
		// new block;
		LedgerBlockData currBlock = new LedgerBlockData(previousBlock.getHeight() + 1, previousBlock.getLedgerHash(),
				previousBlock.getHash());

		// init storage;
		BufferedKVStorage txStagedStorage = new BufferedKVStorage(ledgerExStorage, ledgerVerStorage, PARALLEL_DB_WRITE);

		StagedSnapshot startingPoint = new TxSnapshot(previousBlock, previousBlock.getTransactionSetHash());

		// instantiate editor;
		return new LedgerTransactionalEditor(ledgerSetting.getCryptoSetting(), currBlock, startingPoint,
				ledgerKeyPrefix, txStagedStorage);
	}

	public static LedgerTransactionalEditor createEditor(LedgerInitSetting initSetting, String ledgerKeyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage) {
		LedgerBlockData genesisBlock = new LedgerBlockData(0, null, null);
		StagedSnapshot startingPoint = new GenesisSnapshot(initSetting);
		// init storage;
		BufferedKVStorage txStagedStorage = new BufferedKVStorage(ledgerExStorage, ledgerVerStorage, false);
		return new LedgerTransactionalEditor(initSetting.getCryptoSetting(), genesisBlock, startingPoint,
				ledgerKeyPrefix, txStagedStorage);
	}

	private void commitTxSnapshot(TxSnapshot snapshot) {
		lastTxCtx = newTxCtx;
		newTxCtx = null;
		stagedSnapshots.push(snapshot);
	}

	private void rollbackNewTx() {
		newTxCtx = null;
	}

	// public LedgerDataSet getLatestDataSet() {
	// if (lastTxCtx == null) {
	// return null;
	// }
	// return lastTxCtx.getDataSet();
	// }

	public LedgerBlock getNewlyBlock() {
		return newlyBlock;
	}

	@Override
	public LedgerTransactionContext newTransaction(TransactionRequest txRequest) {
		checkState();
		// TODO:验证交易签名；

		BufferedKVStorage txBuffStorage = null;
		LedgerDataSetImpl txDataset = null;
		TransactionSet txset = null;
		if (lastTxCtx == null) {
			// init storage of new transaction;
			// txBuffStorage = new BufferedKVStorage(bufferedStorage, bufferedStorage,
			// false);
			txBuffStorage = bufferedStorage;

			// load the starting point of the new transaction;
			StagedSnapshot previousSnapshot = stagedSnapshots.peek();
			if (previousSnapshot instanceof GenesisSnapshot) {
				// Genesis;
				GenesisSnapshot snpht = (GenesisSnapshot) previousSnapshot;
				txDataset = LedgerRepositoryImpl.newDataSet(snpht.initSetting, ledgerKeyPrefix, txBuffStorage,
						txBuffStorage);
				txset = LedgerRepositoryImpl.newTransactionSet(txDataset.getAdminAccount().getSetting(),
						ledgerKeyPrefix, txBuffStorage, txBuffStorage);
			} else {
				// TxSnapshot; reload dataset and txset;
				TxSnapshot snpht = (TxSnapshot) previousSnapshot;
				// load dataset;
				txDataset = LedgerRepositoryImpl.loadDataSet(snpht.dataSnapshot, ledgerKeyPrefix, txBuffStorage,
						txBuffStorage, false);

				// load tx set;
				txset = LedgerRepositoryImpl.loadTransactionSet(snpht.transactionSetHash, this.cryptoSetting,
						ledgerKeyPrefix, txBuffStorage, txBuffStorage, false);
			}

			lastTxCtx = new LedgerDataContext(txDataset, txset, txBuffStorage);
		} else {
			// Reuse previous object to optimize performance;
			txBuffStorage = lastTxCtx.storage;
			txDataset = lastTxCtx.dataset;
			txset = lastTxCtx.txset;
		}

		// newTxCtx = new LedgerTransactionContextImpl(newlyBlock.getHeight(),
		// txRequest, txDataset, txset, txBuffStorage,
		// this);
		// return newTxCtx;

		return new LedgerTransactionContextImpl(newlyBlock.getHeight(), txRequest, txDataset, txset, txBuffStorage,
				this);
	}

	@Override
	public LedgerBlock prepare() {
		checkState();

		if (newTxCtx != null) {
			throw new IllegalStateException("There is a opening transaction which isn't committed or rollbacked!");
		}
		if (lastTxCtx == null) {
			// Genesis;
			throw new IllegalStateException("No transaction to prepare!");
		}

		// do commit when transaction isolation level is BLOCK;
		lastTxCtx.dataset.commit();
		lastTxCtx.txset.commit();

		newlyBlock.setAdminAccountHash(lastTxCtx.dataset.getAdminAccount().getHash());
		newlyBlock.setContractAccountSetHash(lastTxCtx.dataset.getContractAccountSet().getRootHash());
		newlyBlock.setDataAccountSetHash(lastTxCtx.dataset.getDataAccountSet().getRootHash());
		newlyBlock.setUserAccountSetHash(lastTxCtx.dataset.getUserAccountSet().getRootHash());
		newlyBlock.setTransactionSetHash(lastTxCtx.txset.getRootHash());

		// compute block hash;
		byte[] blockBodyBytes = BinaryEncodingUtils.encode(newlyBlock, BlockBody.class);
		HashDigest blockHash = CryptoServiceProviders.getHashFunction(cryptoSetting.getHashAlgorithm())
				.hash(blockBodyBytes);
		newlyBlock.setHash(blockHash);
		if (newlyBlock.getLedgerHash() == null) {
			// init GenesisBlock's ledger hash;
			newlyBlock.setLedgerHash(blockHash);
		}

		// persist block bytes;
		// only one version per block;
		byte[] blockBytes = BinaryEncodingUtils.encode(newlyBlock, LedgerBlock.class);
		Bytes blockStorageKey = LedgerRepositoryImpl.encodeBlockStorageKey(newlyBlock.getHash());
		long v = bufferedStorage.set(blockStorageKey, blockBytes, -1);
		if (v < 0) {
			throw new IllegalStateException(
					"Block already exist! --[BlockHash=" + Base58Utils.encode(newlyBlock.getHash().toBytes()) + "]");
		}

		// persist block hash to ledger index;
		HashDigest ledgerHash = newlyBlock.getLedgerHash();
		Bytes ledgerIndexKey = LedgerRepositoryImpl.encodeLedgerIndexKey(ledgerHash);
		long expectedVersion = newlyBlock.getHeight() - 1;
		v = bufferedStorage.set(ledgerIndexKey, newlyBlock.getHash().toBytes(), expectedVersion);
		if (v < 0) {
			throw new IllegalStateException("Index of BlockHash already exist! --[BlockHash="
					+ Base58Utils.encode(newlyBlock.getHash().toBytes()) + "]");
		}

		prepared = true;
		return newlyBlock;
	}

	@Override
	public void commit() {
		if (committed) {
			throw new IllegalStateException("LedgerEditor had been committed!");
		}
		if (canceled) {
			throw new IllegalStateException("LedgerEditor had been canceled!");
		}
		if (!prepared) {
			// 未就绪；
			throw new IllegalStateException("LedgerEditor has not prepared!");
		}

		bufferedStorage.flush();

		committed = true;
	}

	@Override
	public void cancel() {
		if (committed) {
			throw new IllegalStateException("LedgerEditor had been committed!");
		}
		if (canceled) {
			return;
		}

		canceled = true;
		// if (newTxCtx != null) {
		// newTxCtx.rollback();
		// newTxCtx = null;
		// }
		bufferedStorage.cancel();
	}

	private void checkState() {
		if (prepared) {
			throw new IllegalStateException("LedgerEditor had been prepared!");
		}
		if (committed) {
			throw new IllegalStateException("LedgerEditor had been committed!");
		}
		if (canceled) {
			throw new IllegalStateException("LedgerEditor had been canceled!");
		}
	}

	// --------------------------- inner type --------------------------

	private static interface StagedSnapshot {

	}

	private static class GenesisSnapshot implements StagedSnapshot {

		private LedgerInitSetting initSetting;

		public GenesisSnapshot(LedgerInitSetting initSetting) {
			this.initSetting = initSetting;
		}
	}

	private static class TxSnapshot implements StagedSnapshot {

		/**
		 * 账本数据的快照；
		 */
		private LedgerDataSnapshot dataSnapshot;

		/**
		 * 交易集合的快照（根哈希）；
		 */
		private HashDigest transactionSetHash;

		public TxSnapshot(LedgerDataSnapshot dataSnapshot, HashDigest txSetHash) {
			this.dataSnapshot = dataSnapshot;
			this.transactionSetHash = txSetHash;
		}

	}

	private static class LedgerDataContext {

		protected LedgerDataSetImpl dataset;

		protected TransactionSet txset;

		protected BufferedKVStorage storage;

		public LedgerDataContext(LedgerDataSetImpl dataset, TransactionSet txset, BufferedKVStorage storage) {
			this.dataset = dataset;
			this.txset = txset;
			this.storage = storage;
		}

	}

	private static class LedgerTransactionContextImpl extends LedgerDataContext implements LedgerTransactionContext {

		private long blockHeight;

		private LedgerTransactionalEditor editor;

		private TransactionRequest txRequest;

		// private LedgerDataSetImpl dataset;
		//
		// private TransactionSet txset;
		//
		// private BufferedKVStorage storage;

		private boolean committed = false;

		private boolean rollbacked = false;

		private LedgerTransactionContextImpl(long blockHeight, TransactionRequest txRequest, LedgerDataSetImpl dataset,
				TransactionSet txset, BufferedKVStorage storage, LedgerTransactionalEditor editor) {
			super(dataset, txset, storage);
			this.txRequest = txRequest;
			// this.dataset = dataset;
			// this.txset = txset;
			// this.storage = storage;
			this.editor = editor;
			this.blockHeight = blockHeight;
		}

		@Override
		public LedgerDataSet getDataSet() {
			return dataset;
		}

		@Override
		public TransactionRequest getRequestTX() {
			return txRequest;
		}

		@Override
		public LedgerTransaction commit(TransactionState txResult) {
			checkTxState();

			// capture snapshot
			// this.dataset.commit();
			// TransactionStagedSnapshot txDataSnapshot = takeSnapshot();

			// LedgerTransactionData tx = new LedgerTransactionData(blockHeight, txRequest,
			// txResult, txDataSnapshot);
			LedgerTransactionData tx = new LedgerTransactionData(blockHeight, txRequest, txResult, null);
			this.txset.add(tx);
			// this.txset.commit();

			// this.storage.flush();

			// TODO: 未处理出错时 dataset 和 txset 的内部状态恢复，有可能出现不一致的情况；

			// put snapshot into stack;
			// TxSnapshot snapshot = new TxSnapshot(txDataSnapshot, txset.getRootHash());
			// editor.commitTxSnapshot(snapshot);

			committed = true;
			return tx;
		}

		@Override
		public LedgerTransaction discardAndCommit(TransactionState txResult) {
			checkTxState();

			// 未处理
			// dataset.cancel();

			// TransactionStagedSnapshot txDataSnapshot = takeSnapshot();
			// LedgerTransactionData tx = new LedgerTransactionData(blockHeight, txRequest,
			// txResult, txDataSnapshot);
			LedgerTransactionData tx = new LedgerTransactionData(blockHeight, txRequest, txResult, null);
			this.txset.add(tx);
			// this.txset.commit();

			// this.storage.flush();

			// TODO: 未处理出错时 dataset 和 txset 的内部状态恢复，有可能出现不一致的情况；

			// put snapshot into stack;
			// TxSnapshot snapshot = new TxSnapshot(txDataSnapshot, txset.getRootHash());
			// editor.commitTxSnapshot(snapshot);

			committed = true;
			return tx;
		}

		private TransactionStagedSnapshot takeSnapshot() {
			TransactionStagedSnapshot txDataSnapshot = new TransactionStagedSnapshot();
			txDataSnapshot.setAdminAccountHash(dataset.getAdminAccount().getHash());
			txDataSnapshot.setContractAccountSetHash(dataset.getContractAccountSet().getRootHash());
			txDataSnapshot.setDataAccountSetHash(dataset.getDataAccountSet().getRootHash());
			txDataSnapshot.setUserAccountSetHash(dataset.getUserAccountSet().getRootHash());
			return txDataSnapshot;
		}

		@Override
		public void rollback() {
			if (this.rollbacked) {
				return;
			}
			if (this.committed) {
				throw new IllegalStateException("Transaction had been committed!");
			}
			// dataset.cancel();
			// storage.cancel();

			// editor.rollbackNewTx();

			rollbacked = true;
		}

		private void checkTxState() {
			if (this.committed) {
				throw new IllegalStateException("Transaction had been committed!");
			}
			if (this.rollbacked) {
				throw new IllegalStateException("Transaction had been rollbacked!");
			}
		}
	}

}