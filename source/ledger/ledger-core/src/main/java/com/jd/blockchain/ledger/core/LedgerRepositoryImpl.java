package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.ledger.BlockBody;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerAdminSettings;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerDataSnapshot;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerSettings;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.codec.Base58Utils;

/**
 * 账本的存储结构： <br>
 * 
 * 1、账本数据以版本化KV存储({@link VersioningKVStorage})为基础； <br>
 * 
 * 2、以账本hash为 key，保存账本的每一个区块的hash，对应的版本序号恰好一致地表示了区块高度； <br>
 * 
 * 3、区块数据以区块 hash 加上特定前缀({@link #BLOCK_PREFIX}) 构成 key
 * 进行保存，每个区块只有唯一个版本，在存储时会进行版本唯一性校验； <br>
 * 
 * @author huanghaiquan
 *
 */
class LedgerRepositoryImpl implements LedgerRepository {

	private static final Bytes LEDGER_PREFIX = Bytes.fromString("IDX" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes BLOCK_PREFIX = Bytes.fromString("BLK" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes USER_SET_PREFIX = Bytes.fromString("USRS" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes DATA_SET_PREFIX = Bytes.fromString("DATS" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes CONTRACT_SET_PREFIX = Bytes.fromString("CTRS" + LedgerConsts.KEY_SEPERATOR);

	private static final Bytes TRANSACTION_SET_PREFIX = Bytes.fromString("TXS" + LedgerConsts.KEY_SEPERATOR);

	private static final AccountAccessPolicy DEFAULT_ACCESS_POLICY = new OpeningAccessPolicy();

	private HashDigest ledgerHash;

	private final String keyPrefix;

	private Bytes ledgerIndexKey;

	private VersioningKVStorage versioningStorage;

	private ExPolicyKVStorage exPolicyStorage;

	private volatile LedgerState latestState;

	private volatile LedgerEditor nextBlockEditor;

	private volatile boolean closed = false;

	public LedgerRepositoryImpl(HashDigest ledgerHash, String keyPrefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage versioningStorage) {
		this.keyPrefix = keyPrefix;

		this.ledgerHash = ledgerHash;
		this.versioningStorage = versioningStorage;
		this.exPolicyStorage = exPolicyStorage;
		this.ledgerIndexKey = encodeLedgerIndexKey(ledgerHash);

		if (getLatestBlockHeight() < 0) {
			throw new RuntimeException("Ledger doesn't exist!");
		}

		retrieveLatestState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.LedgerRepository#getHash()
	 */
	@Override
	public HashDigest getHash() {
		return ledgerHash;
	}

	@Override
	public HashDigest getLatestBlockHash() {
		if (latestState == null) {
			return innerGetBlockHash(innerGetLatestBlockHeight());
		}
		return latestState.block.getHash();
	}

	@Override
	public long getLatestBlockHeight() {
		if (latestState == null) {
			return innerGetLatestBlockHeight();
		}
		return latestState.block.getHeight();
	}

	@Override
	public LedgerBlock getLatestBlock() {
		return latestState.block;
	}

	/**
	 * 重新检索加载最新的状态；
	 * 
	 * @return
	 */
	private LedgerState retrieveLatestState() {
		LedgerBlock latestBlock = innerGetBlock(innerGetLatestBlockHeight());
		LedgerDataset ledgerDataset = innerGetLedgerDataset(latestBlock);
		TransactionQuery txSet = loadTransactionSet(latestBlock.getTransactionSetHash(),
				ledgerDataset.getAdminDataset().getSettings().getCryptoSetting(), keyPrefix, exPolicyStorage,
				versioningStorage, true);
		this.latestState = new LedgerState(latestBlock, ledgerDataset, txSet);
		return latestState;
	}

	@Override
	public LedgerBlock retrieveLatestBlock() {
		return retrieveLatestState().block;
	}

	@Override
	public HashDigest retrieveLatestBlockHash() {
		HashDigest latestBlockHash = innerGetBlockHash(innerGetLatestBlockHeight());
		if (latestState != null && !latestBlockHash.equals(latestState.block.getHash())) {
			latestState = null;
		}
		return latestBlockHash;
	}

	@Override
	public long retrieveLatestBlockHeight() {
		long latestBlockHeight = innerGetLatestBlockHeight();
		if (latestState != null && latestBlockHeight != latestState.block.getHeight()) {
			latestState = null;
		}
		return latestBlockHeight;
	}

	private long innerGetLatestBlockHeight() {
		return versioningStorage.getVersion(ledgerIndexKey);
	}

	@Override
	public HashDigest getBlockHash(long height) {
		LedgerBlock blk = latestState == null ? null : latestState.block;
		if (blk != null && height == blk.getHeight()) {
			return blk.getHash();
		}
		return innerGetBlockHash(height);
	}

	private HashDigest innerGetBlockHash(long height) {
		if (height < 0) {
			return null;
		}
		// get block hash by height;
		byte[] hashBytes = versioningStorage.get(ledgerIndexKey, height);
		if (hashBytes == null || hashBytes.length == 0) {
			return null;
		}
		return new HashDigest(hashBytes);
	}

	@Override
	public LedgerBlock getBlock(long height) {
		LedgerBlock blk = latestState == null ? null : latestState.block;
		if (blk != null && height == blk.getHeight()) {
			return blk;
		}
		return innerGetBlock(height);
	}

	private LedgerBlock innerGetBlock(long height) {
		if (height < 0) {
			return null;
		}
		return innerGetBlock(innerGetBlockHash(height));
	}

	@Override
	public LedgerBlock getBlock(HashDigest blockHash) {
		LedgerBlock blk = latestState == null ? null : latestState.block;
		if (blk != null && blockHash.equals(blk.getHash())) {
			return blk;
		}
		return innerGetBlock(blockHash);
	}

	private LedgerBlock innerGetBlock(HashDigest blockHash) {
		Bytes key = encodeBlockStorageKey(blockHash);
		// Every one block has only one version;
		byte[] blockBytes = versioningStorage.get(key, 0);
		LedgerBlockData block = new LedgerBlockData(deserialize(blockBytes));

		if (!blockHash.equals(block.getHash())) {
			throw new RuntimeException("Block hash not equals to it's storage key!");
		}

		// verify block hash;
		byte[] blockBodyBytes = null;
		if (block.getHeight() == 0) {
			// 计算创世区块的 hash 时，不包括 ledgerHash 字段；
			blockBodyBytes = BinaryProtocol.encode(block, BlockBody.class);
		} else {
			blockBodyBytes = BinaryProtocol.encode(block, BlockBody.class);
		}
		HashFunction hashFunc = Crypto.getHashFunction(blockHash.getAlgorithm());
		boolean pass = hashFunc.verify(blockHash, blockBodyBytes);
		if (!pass) {
			throw new RuntimeException("Block hash verification fail!");
		}

		// verify height;
		HashDigest indexedHash = getBlockHash(block.getHeight());
		if (indexedHash == null || !indexedHash.equals(blockHash)) {
			throw new RuntimeException(
					"Illegal ledger state in storage that ledger height index doesn't match it's block data in height["
							+ block.getHeight() + "] and block hash[" + Base58Utils.encode(blockHash.toBytes())
							+ "] !");
		}

		return block;
	}

	/**
	 * 获取最新区块的账本参数；
	 * 
	 * @return
	 */
	private LedgerSettings getLatestSettings() {
		return getAdminInfo().getSettings();
	}

	@Override
	public LedgerAdminInfo getAdminInfo() {
		return createAdminData(getLatestBlock());
	}

	private LedgerBlock deserialize(byte[] blockBytes) {
		return BinaryProtocol.decode(blockBytes);
	}

	@Override
	public TransactionQuery getTransactionSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		if (height == block.getHeight()) {
			// 从缓存中返回最新区块的数据集；
			return latestState.getTransactionSet();
		}
		LedgerAdminInfo adminAccount = getAdminInfo(block);
		// All of existing block is readonly;
		return loadTransactionSet(block.getTransactionSetHash(), adminAccount.getSettings().getCryptoSetting(),
				keyPrefix, exPolicyStorage, versioningStorage, true);
	}

	@Override
	public LedgerAdminInfo getAdminInfo(LedgerBlock block) {
		return createAdminData(block);
	}
	
	@Override
	public LedgerAdminSettings getAdminSettings() {
		return getAdminSettings(getLatestBlock());
	}
	
	@Override
	public LedgerAdminSettings getAdminSettings(LedgerBlock block) {
		long height = getLatestBlockHeight();
		if (height == block.getHeight()) {
			return latestState.getAdminDataset();
		}

		return createAdminDataset(block);
	}

	/**
	 * 生成LedgerAdminInfoData对象
	 *     该对象主要用于页面展示
	 *
	 * @param block
	 * @return
	 */
	private LedgerAdminInfoData createAdminData(LedgerBlock block) {
		return new LedgerAdminInfoData(createAdminDataset(block));
	}

	/**
	 * 生成LedgerAdminDataset对象
	 *
	 * @param block
	 * @return
	 */
	private LedgerAdminDataset createAdminDataset(LedgerBlock block) {
		return new LedgerAdminDataset(block.getAdminAccountHash(), keyPrefix, exPolicyStorage, versioningStorage, true);
	}

	@Override
	public UserAccountQuery getUserAccountSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		if (height == block.getHeight()) {
			return latestState.getUserAccountSet();
		}
		LedgerAdminSettings adminAccount = getAdminSettings(block);
		return createUserAccountSet(block, adminAccount.getSettings().getCryptoSetting());
	}

	private UserAccountSet createUserAccountSet(LedgerBlock block, CryptoSetting cryptoSetting) {
		return loadUserAccountSet(block.getUserAccountSetHash(), cryptoSetting, keyPrefix, exPolicyStorage,
				versioningStorage, true);
	}

	@Override
	public DataAccountQuery getDataAccountSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		if (height == block.getHeight()) {
			return latestState.getDataAccountSet();
		}

		LedgerAdminSettings adminAccount = getAdminSettings(block);
		return createDataAccountSet(block, adminAccount.getSettings().getCryptoSetting());
	}

	private DataAccountSet createDataAccountSet(LedgerBlock block, CryptoSetting setting) {
		return loadDataAccountSet(block.getDataAccountSetHash(), setting, keyPrefix, exPolicyStorage, versioningStorage,
				true);
	}

	@Override
	public ContractAccountQuery getContractAccountSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		if (height == block.getHeight()) {
			return latestState.getContractAccountSet();
		}

		LedgerAdminSettings adminAccount = getAdminSettings(block);
		return createContractAccountSet(block, adminAccount.getSettings().getCryptoSetting());
	}

	private ContractAccountSet createContractAccountSet(LedgerBlock block, CryptoSetting cryptoSetting) {
		return loadContractAccountSet(block.getContractAccountSetHash(), cryptoSetting, keyPrefix, exPolicyStorage,
				versioningStorage, true);
	}

	@Override
	public LedgerDataset getLedgerData(LedgerBlock block) {
		long height = getLatestBlockHeight();
		if (height == block.getHeight()) {
			return latestState.getLedgerDataset();
		}

		// All of existing block is readonly;
		return innerGetLedgerDataset(block);
	}

	private LedgerDataset innerGetLedgerDataset(LedgerBlock block) {
		LedgerAdminDataset adminDataset = createAdminDataset(block);
		CryptoSetting cryptoSetting = adminDataset.getSettings().getCryptoSetting();

		UserAccountSet userAccountSet = createUserAccountSet(block, cryptoSetting);
		DataAccountSet dataAccountSet = createDataAccountSet(block, cryptoSetting);
		ContractAccountSet contractAccountSet = createContractAccountSet(block, cryptoSetting);
		return new LedgerDataset(adminDataset, userAccountSet, dataAccountSet, contractAccountSet, true);
	}

	public synchronized void resetNextBlockEditor() {
		this.nextBlockEditor = null;
	}

	@Override
	public synchronized LedgerEditor createNextBlock() {
		if (closed) {
			throw new RuntimeException("Ledger repository has been closed!");
		}
		if (this.nextBlockEditor != null) {
			throw new RuntimeException(
					"A new block is in process, cann't create another one until it finish by committing or canceling.");
		}
		LedgerBlock previousBlock = getLatestBlock();
		LedgerTransactionalEditor editor = LedgerTransactionalEditor.createEditor(previousBlock, getLatestSettings(),
				keyPrefix, exPolicyStorage, versioningStorage);
		NewBlockCommittingMonitor committingMonitor = new NewBlockCommittingMonitor(editor, this);
		this.nextBlockEditor = committingMonitor;
		return committingMonitor;
	}

	@Override
	public LedgerEditor getNextBlockEditor() {
		return nextBlockEditor;
	}

	@Override
	public synchronized void close() {
		if (closed) {
			return;
		}
		if (this.nextBlockEditor != null) {
			throw new RuntimeException("A new block is in process, cann't close the ledger repository!");
		}
		closed = true;
	}

	static Bytes encodeLedgerIndexKey(HashDigest ledgerHash) {
		return LEDGER_PREFIX.concat(ledgerHash);
	}

	static Bytes encodeBlockStorageKey(HashDigest blockHash) {
		return BLOCK_PREFIX.concat(blockHash);
	}

	static LedgerDataset newDataSet(LedgerInitSetting initSetting, String keyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage) {
		LedgerAdminDataset adminAccount = new LedgerAdminDataset(initSetting, keyPrefix, ledgerExStorage,
				ledgerVerStorage);

		String usersetKeyPrefix = keyPrefix + USER_SET_PREFIX;
		String datasetKeyPrefix = keyPrefix + DATA_SET_PREFIX;
		String contractsetKeyPrefix = keyPrefix + CONTRACT_SET_PREFIX;

		UserAccountSet userAccountSet = new UserAccountSet(adminAccount.getSettings().getCryptoSetting(),
				usersetKeyPrefix, ledgerExStorage, ledgerVerStorage, DEFAULT_ACCESS_POLICY);

		DataAccountSet dataAccountSet = new DataAccountSet(adminAccount.getSettings().getCryptoSetting(),
				datasetKeyPrefix, ledgerExStorage, ledgerVerStorage, DEFAULT_ACCESS_POLICY);

		ContractAccountSet contractAccountSet = new ContractAccountSet(adminAccount.getSettings().getCryptoSetting(),
				contractsetKeyPrefix, ledgerExStorage, ledgerVerStorage, DEFAULT_ACCESS_POLICY);

		LedgerDataset newDataSet = new LedgerDataset(adminAccount, userAccountSet, dataAccountSet,
				contractAccountSet, false);

		return newDataSet;
	}

	static TransactionSet newTransactionSet(LedgerSettings ledgerSetting, String keyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage) {

		String txsetKeyPrefix = keyPrefix + TRANSACTION_SET_PREFIX;

		TransactionSet transactionSet = new TransactionSet(ledgerSetting.getCryptoSetting(), txsetKeyPrefix,
				ledgerExStorage, ledgerVerStorage);
		return transactionSet;
	}

	static LedgerDataset loadDataSet(LedgerDataSnapshot dataSnapshot, CryptoSetting cryptoSetting, String keyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage, boolean readonly) {
		LedgerAdminDataset adminAccount = new LedgerAdminDataset(dataSnapshot.getAdminAccountHash(), keyPrefix,
				ledgerExStorage, ledgerVerStorage, readonly);

		UserAccountSet userAccountSet = loadUserAccountSet(dataSnapshot.getUserAccountSetHash(), cryptoSetting,
				keyPrefix, ledgerExStorage, ledgerVerStorage, readonly);

		DataAccountSet dataAccountSet = loadDataAccountSet(dataSnapshot.getDataAccountSetHash(), cryptoSetting,
				keyPrefix, ledgerExStorage, ledgerVerStorage, readonly);

		ContractAccountSet contractAccountSet = loadContractAccountSet(dataSnapshot.getContractAccountSetHash(),
				cryptoSetting, keyPrefix, ledgerExStorage, ledgerVerStorage, readonly);

		LedgerDataset dataset = new LedgerDataset(adminAccount, userAccountSet, dataAccountSet,
				contractAccountSet, readonly);

		return dataset;
	}

	static UserAccountSet loadUserAccountSet(HashDigest userAccountSetHash, CryptoSetting cryptoSetting,
			String keyPrefix, ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage,
			boolean readonly) {

		String usersetKeyPrefix = keyPrefix + USER_SET_PREFIX;
		return new UserAccountSet(userAccountSetHash, cryptoSetting, usersetKeyPrefix, ledgerExStorage,
				ledgerVerStorage, readonly, DEFAULT_ACCESS_POLICY);
	}

	static DataAccountSet loadDataAccountSet(HashDigest dataAccountSetHash, CryptoSetting cryptoSetting,
			String keyPrefix, ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage,
			boolean readonly) {

		String datasetKeyPrefix = keyPrefix + DATA_SET_PREFIX;
		return new DataAccountSet(dataAccountSetHash, cryptoSetting, datasetKeyPrefix, ledgerExStorage,
				ledgerVerStorage, readonly, DEFAULT_ACCESS_POLICY);
	}

	static ContractAccountSet loadContractAccountSet(HashDigest contractAccountSetHash, CryptoSetting cryptoSetting,
			String keyPrefix, ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage,
			boolean readonly) {

		String contractsetKeyPrefix = keyPrefix + CONTRACT_SET_PREFIX;
		return new ContractAccountSet(contractAccountSetHash, cryptoSetting, contractsetKeyPrefix, ledgerExStorage,
				ledgerVerStorage, readonly, DEFAULT_ACCESS_POLICY);
	}

	static TransactionSet loadTransactionSet(HashDigest txsetHash, CryptoSetting cryptoSetting, String keyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage, boolean readonly) {

		String txsetKeyPrefix = keyPrefix + TRANSACTION_SET_PREFIX;
		return new TransactionSet(txsetHash, cryptoSetting, txsetKeyPrefix, ledgerExStorage, ledgerVerStorage,
				readonly);

	}

	private static class NewBlockCommittingMonitor implements LedgerEditor {

		private LedgerTransactionalEditor editor;

		private LedgerRepositoryImpl ledgerRepo;

		public NewBlockCommittingMonitor(LedgerTransactionalEditor editor, LedgerRepositoryImpl ledgerRepo) {
			this.editor = editor;
			this.ledgerRepo = ledgerRepo;
		}

		@Override
		public HashDigest getLedgerHash() {
			return editor.getLedgerHash();
		}

		@Override
		public long getBlockHeight() {
			return editor.getBlockHeight();
		}

		@Override
		public LedgerDataset getLedgerDataset() {
			return editor.getLedgerDataset();
		}

		@Override
		public TransactionSet getTransactionSet() {
			return editor.getTransactionSet();
		}

		@Override
		public LedgerTransactionContext newTransaction(TransactionRequest txRequest) {
			return editor.newTransaction(txRequest);
		}

		@Override
		public LedgerBlock prepare() {
			return editor.prepare();
		}

		@Override
		public void commit() {
			try {
				editor.commit();
				LedgerBlock latestBlock = editor.getCurrentBlock();
				ledgerRepo.latestState = new LedgerState(latestBlock, editor.getLedgerDataset(),
						editor.getTransactionSet());
			} finally {
				ledgerRepo.nextBlockEditor = null;
			}
		}

		@Override
		public void cancel() {
			try {
				editor.cancel();
			} finally {
				ledgerRepo.nextBlockEditor = null;
			}
		}

	}

	/**
	 * 维护账本某个区块的数据状态的缓存结构；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class LedgerState {

		private final LedgerBlock block;

		private final TransactionQuery transactionSet;

		private final LedgerDataset ledgerDataset;

		public LedgerState(LedgerBlock block, LedgerDataset ledgerDataset, TransactionQuery transactionSet) {
			this.block = block;
			this.ledgerDataset = ledgerDataset;
			this.transactionSet = transactionSet;

		}

		public LedgerAdminDataset getAdminDataset() {
			return ledgerDataset.getAdminDataset();
		}

		public LedgerDataset getLedgerDataset() {
			return ledgerDataset;
		}

		public ContractAccountQuery getContractAccountSet() {
			return ledgerDataset.getContractAccountset();
		}

		public DataAccountQuery getDataAccountSet() {
			return ledgerDataset.getDataAccountSet();
		}

		public UserAccountQuery getUserAccountSet() {
			return ledgerDataset.getUserAccountSet();
		}

		public TransactionQuery getTransactionSet() {
			return transactionSet;
		}

	}
}
