package com.jd.blockchain.ledger.core.impl;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.BlockBody;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerDataSnapshot;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.core.AccountAccessPolicy;
import com.jd.blockchain.ledger.core.ContractAccountSet;
import com.jd.blockchain.ledger.core.DataAccountSet;
import com.jd.blockchain.ledger.core.LedgerAdminAccount;
import com.jd.blockchain.ledger.core.LedgerAdministration;
import com.jd.blockchain.ledger.core.LedgerConsts;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerException;
import com.jd.blockchain.ledger.core.LedgerRepository;
import com.jd.blockchain.ledger.core.LedgerSetting;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.TransactionSet;
import com.jd.blockchain.ledger.core.UserAccountSet;
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
public class LedgerRepositoryImpl implements LedgerRepository {

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
			throw new LedgerException("Ledger doesn't exist!");
		}
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
		LedgerState state = getLatestState();
		return state.block;
	}

	private LedgerState getLatestState() {
		LedgerState state = latestState;
		if (state == null) {
			LedgerBlock latestBlock = innerGetBlock(innerGetLatestBlockHeight());
			state = new LedgerState(latestBlock);
			latestState = state;
		}
		return state;
	}

	@Override
	public LedgerBlock retrieveLatestBlock() {
		LedgerBlock latestBlock = innerGetBlock(innerGetLatestBlockHeight());
		latestState = new LedgerState(latestBlock);
		return latestBlock;
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
		return CryptoUtils.hashCrypto().resolveHashDigest(hashBytes);
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
		return innerGetBlock(getBlockHash(height));
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
			throw new LedgerException("Block hash not equals to it's storage key!");
		}

		// verify hash;
		// boolean requiredVerifyHash =
		// adminAccount.getMetadata().getSetting().getCryptoSetting().getAutoVerifyHash();
		// TODO: 未实现从配置中加载是否校验 Hash 的设置；
		boolean requiredVerifyHash = false;
		if (requiredVerifyHash) {
			byte[] blockBodyBytes = null;
			if (block.getHeight() == 0) {
				// 计算创世区块的 hash 时，不包括 ledgerHash 字段；
				block.setLedgerHash(null);
				blockBodyBytes = BinaryEncodingUtils.encode(block, BlockBody.class);
				// 恢复；
				block.setLedgerHash(block.getHash());
			} else {
				blockBodyBytes = BinaryEncodingUtils.encode(block, BlockBody.class);
			}
			boolean pass = CryptoUtils.hashCrypto().verify(blockHash, blockBodyBytes);
			if (!pass) {
				throw new LedgerException("Block hash verification fail!");
			}
		}

		// verify height;
		HashDigest indexedHash = getBlockHash(block.getHeight());
		if (indexedHash == null || !indexedHash.equals(blockHash)) {
			throw new LedgerException(
					"Illegal ledger state in storage that ledger height index doesn't match it's block data in height["
							+ block.getHeight() + "] and block hash[" + Base58Utils.encode(blockHash.toBytes())
							+ "] !");
		}

		return block;
	}

	@Override
	public LedgerAdministration getAdminInfo() {
		return getAdminAccount(getLatestBlock());
	}

	private LedgerBlock deserialize(byte[] blockBytes) {
		return BinaryEncodingUtils.decode(blockBytes);
	}

	@Override
	public TransactionSet getTransactionSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		TransactionSet transactionSet = null;
		if (height == block.getHeight()) {
			// 缓存读；
			LedgerState state = getLatestState();
			transactionSet = state.transactionSet;
			if (transactionSet == null) {
				LedgerAdminAccount adminAccount = getAdminAccount(block);
				transactionSet = loadTransactionSet(block.getTransactionSetHash(),
						adminAccount.getMetadata().getSetting().getCryptoSetting(), keyPrefix, exPolicyStorage,
						versioningStorage, true);
				state.transactionSet = transactionSet;
			}
			return transactionSet;
		}
		LedgerAdminAccount adminAccount = getAdminAccount(block);
		// All of existing block is readonly;
		return loadTransactionSet(block.getTransactionSetHash(),
				adminAccount.getMetadata().getSetting().getCryptoSetting(), keyPrefix, exPolicyStorage,
				versioningStorage, true);
	}

	@Override
	public LedgerAdminAccount getAdminAccount(LedgerBlock block) {
		long height = getLatestBlockHeight();
		LedgerAdminAccount adminAccount = null;
		if (height == block.getHeight()) {
			// 缓存读；
			LedgerState state = getLatestState();
			adminAccount = state.adminAccount;
			if (adminAccount == null) {
				adminAccount = new LedgerAdminAccount(block.getAdminAccountHash(), keyPrefix, exPolicyStorage,
						versioningStorage, true);
				state.adminAccount = adminAccount;
			}
			return adminAccount;
		}

		return new LedgerAdminAccount(block.getAdminAccountHash(), keyPrefix, exPolicyStorage, versioningStorage, true);
	}

	@Override
	public UserAccountSet getUserAccountSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		UserAccountSet userAccountSet = null;
		if (height == block.getHeight()) {
			// 缓存读；
			LedgerState state = getLatestState();
			userAccountSet = state.userAccountSet;
			if (userAccountSet == null) {
				LedgerAdminAccount adminAccount = getAdminAccount(block);
				userAccountSet = loadUserAccountSet(block.getUserAccountSetHash(),
						adminAccount.getPreviousSetting().getCryptoSetting(), keyPrefix, exPolicyStorage,
						versioningStorage, true);
				state.userAccountSet = userAccountSet;
			}
			return userAccountSet;
		}
		LedgerAdminAccount adminAccount = getAdminAccount(block);
		return loadUserAccountSet(block.getUserAccountSetHash(), adminAccount.getPreviousSetting().getCryptoSetting(),
				keyPrefix, exPolicyStorage, versioningStorage, true);
	}

	@Override
	public DataAccountSet getDataAccountSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		DataAccountSet dataAccountSet = null;
		if (height == block.getHeight()) {
			// 缓存读；
			LedgerState state = getLatestState();
			dataAccountSet = state.dataAccountSet;
			if (dataAccountSet == null) {
				LedgerAdminAccount adminAccount = getAdminAccount(block);
				dataAccountSet = loadDataAccountSet(block.getDataAccountSetHash(),
						adminAccount.getPreviousSetting().getCryptoSetting(), keyPrefix, exPolicyStorage,
						versioningStorage, true);
				state.dataAccountSet = dataAccountSet;
			}
			return dataAccountSet;
		}

		LedgerAdminAccount adminAccount = getAdminAccount(block);
		return loadDataAccountSet(block.getDataAccountSetHash(), adminAccount.getPreviousSetting().getCryptoSetting(),
				keyPrefix, exPolicyStorage, versioningStorage, true);
	}

	@Override
	public ContractAccountSet getContractAccountSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		ContractAccountSet contractAccountSet = null;
		if (height == block.getHeight()) {
			// 缓存读；
			LedgerState state = getLatestState();
			contractAccountSet = state.contractAccountSet;
			if (contractAccountSet == null) {
				LedgerAdminAccount adminAccount = getAdminAccount(block);
				contractAccountSet = loadContractAccountSet(block.getContractAccountSetHash(),
						adminAccount.getPreviousSetting().getCryptoSetting(), keyPrefix, exPolicyStorage,
						versioningStorage, true);
				state.contractAccountSet = contractAccountSet;
			}
			return contractAccountSet;
		}

		LedgerAdminAccount adminAccount = getAdminAccount(block);
		return loadContractAccountSet(block.getContractAccountSetHash(),
				adminAccount.getPreviousSetting().getCryptoSetting(), keyPrefix, exPolicyStorage, versioningStorage,
				true);
	}

	@Override
	public LedgerDataSet getDataSet(LedgerBlock block) {
		long height = getLatestBlockHeight();
		LedgerDataSet ledgerDataSet = null;
		if (height == block.getHeight()) {
			// 缓存读；
			LedgerState state = getLatestState();
			ledgerDataSet = state.ledgerDataSet;
			if (ledgerDataSet == null) {
				ledgerDataSet = innerDataSet(block);
				state.ledgerDataSet = ledgerDataSet;
			}
			return ledgerDataSet;
		}

		// All of existing block is readonly;
		return innerDataSet(block);
	}

	private LedgerDataSet innerDataSet(LedgerBlock block) {
		LedgerAdminAccount adminAccount = getAdminAccount(block);
		UserAccountSet userAccountSet = getUserAccountSet(block);
		DataAccountSet dataAccountSet = getDataAccountSet(block);
		ContractAccountSet contractAccountSet = getContractAccountSet(block);
		return new LedgerDataSetImpl(adminAccount, userAccountSet, dataAccountSet, contractAccountSet, true);
	}

	@Override
	public synchronized LedgerEditor createNextBlock() {
		if (closed) {
			throw new LedgerException(
					"Ledger repository has been closed!");
		}
		if (this.nextBlockEditor != null) {
			throw new LedgerException(
					"A new block is in process, cann't create another one until it finish by committing or canceling.");
		}
		LedgerBlock previousBlock = getLatestBlock();
		LedgerTransactionalEditor editor = LedgerTransactionalEditor.createEditor(
				getAdminInfo().getMetadata().getSetting(), previousBlock, keyPrefix, exPolicyStorage,
				versioningStorage);
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
			throw new LedgerException(
					"A new block is in process, cann't close the ledger repository!");
		}
		closed = true;
	}

	static Bytes encodeLedgerIndexKey(HashDigest ledgerHash) {
		// return LEDGER_PREFIX + Base58Utils.encode(ledgerHash.toBytes());
		// return new Bytes(ledgerHash.toBytes()).concatTo(LEDGER_PREFIX);
		return LEDGER_PREFIX.concat(ledgerHash);
	}

	static Bytes encodeBlockStorageKey(HashDigest blockHash) {
		// String key = ByteArray.toBase58(blockHash.toBytes());
		// return BLOCK_PREFIX + key;

		return BLOCK_PREFIX.concat(blockHash);
	}

	static LedgerDataSetImpl newDataSet(LedgerInitSetting initSetting, String keyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage) {
		LedgerAdminAccount adminAccount = new LedgerAdminAccount(initSetting, keyPrefix, ledgerExStorage,
				ledgerVerStorage);

		String usersetKeyPrefix = keyPrefix + USER_SET_PREFIX;
		String datasetKeyPrefix = keyPrefix + DATA_SET_PREFIX;
		String contractsetKeyPrefix = keyPrefix + CONTRACT_SET_PREFIX;
		// String txsetKeyPrefix = keyPrefix + TRANSACTION_SET_PREFIX;

		// UserAccountSet userAccountSet = new
		// UserAccountSet(adminAccount.getSetting().getCryptoSetting(),
		// PrefixAppender.prefix(USER_SET_PREFIX, ledgerExStorage),
		// PrefixAppender.prefix(USER_SET_PREFIX, ledgerVerStorage),
		// DEFAULT_ACCESS_POLICY);
		UserAccountSet userAccountSet = new UserAccountSet(adminAccount.getSetting().getCryptoSetting(),
				usersetKeyPrefix, ledgerExStorage, ledgerVerStorage, DEFAULT_ACCESS_POLICY);

		// DataAccountSet dataAccountSet = new
		// DataAccountSet(adminAccount.getSetting().getCryptoSetting(),
		// PrefixAppender.prefix(DATA_SET_PREFIX, ledgerExStorage),
		// PrefixAppender.prefix(DATA_SET_PREFIX, ledgerVerStorage),
		// DEFAULT_ACCESS_POLICY);
		DataAccountSet dataAccountSet = new DataAccountSet(adminAccount.getSetting().getCryptoSetting(),
				datasetKeyPrefix, ledgerExStorage, ledgerVerStorage, DEFAULT_ACCESS_POLICY);

		// ContractAccountSet contractAccountSet = new
		// ContractAccountSet(adminAccount.getSetting().getCryptoSetting(),
		// PrefixAppender.prefix(CONTRACT_SET_PREFIX, ledgerExStorage),
		// PrefixAppender.prefix(CONTRACT_SET_PREFIX, ledgerVerStorage),
		// DEFAULT_ACCESS_POLICY);
		ContractAccountSet contractAccountSet = new ContractAccountSet(adminAccount.getSetting().getCryptoSetting(),
				contractsetKeyPrefix, ledgerExStorage, ledgerVerStorage, DEFAULT_ACCESS_POLICY);

		LedgerDataSetImpl newDataSet = new LedgerDataSetImpl(adminAccount, userAccountSet, dataAccountSet,
				contractAccountSet, false);

		return newDataSet;
	}

	static TransactionSet newTransactionSet(LedgerSetting ledgerSetting, String keyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage) {
		// TransactionSet transactionSet = new
		// TransactionSet(ledgerSetting.getCryptoSetting(),
		// PrefixAppender.prefix(TRANSACTION_SET_PREFIX, ledgerExStorage),
		// PrefixAppender.prefix(TRANSACTION_SET_PREFIX, ledgerVerStorage));

		String txsetKeyPrefix = keyPrefix + TRANSACTION_SET_PREFIX;

		TransactionSet transactionSet = new TransactionSet(ledgerSetting.getCryptoSetting(), txsetKeyPrefix,
				ledgerExStorage, ledgerVerStorage);
		return transactionSet;
	}

	static LedgerDataSetImpl loadDataSet(LedgerDataSnapshot dataSnapshot, String keyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage, boolean readonly) {
		LedgerAdminAccount adminAccount = new LedgerAdminAccount(dataSnapshot.getAdminAccountHash(), keyPrefix,
				ledgerExStorage, ledgerVerStorage, readonly);

		CryptoSetting cryptoSetting = adminAccount.getPreviousSetting().getCryptoSetting();

		UserAccountSet userAccountSet = loadUserAccountSet(dataSnapshot.getUserAccountSetHash(), cryptoSetting,
				keyPrefix, ledgerExStorage, ledgerVerStorage, readonly);

		DataAccountSet dataAccountSet = loadDataAccountSet(dataSnapshot.getDataAccountSetHash(), cryptoSetting,
				keyPrefix, ledgerExStorage, ledgerVerStorage, readonly);

		ContractAccountSet contractAccountSet = loadContractAccountSet(dataSnapshot.getContractAccountSetHash(),
				cryptoSetting, keyPrefix, ledgerExStorage, ledgerVerStorage, readonly);

		LedgerDataSetImpl dataset = new LedgerDataSetImpl(adminAccount, userAccountSet, dataAccountSet,
				contractAccountSet, readonly);

		return dataset;
	}

	static UserAccountSet loadUserAccountSet(HashDigest userAccountSetHash, CryptoSetting cryptoSetting,
			String keyPrefix, ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage,
			boolean readonly) {
		// return new UserAccountSet(userAccountSetHash, cryptoSetting,
		// PrefixAppender.prefix(USER_SET_PREFIX, ledgerExStorage),
		// PrefixAppender.prefix(USER_SET_PREFIX, ledgerVerStorage), readonly,
		// DEFAULT_ACCESS_POLICY);

		String usersetKeyPrefix = keyPrefix + USER_SET_PREFIX;
		return new UserAccountSet(userAccountSetHash, cryptoSetting, usersetKeyPrefix, ledgerExStorage,
				ledgerVerStorage, readonly, DEFAULT_ACCESS_POLICY);
	}

	static DataAccountSet loadDataAccountSet(HashDigest dataAccountSetHash, CryptoSetting cryptoSetting,
			String keyPrefix, ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage,
			boolean readonly) {
		// return new DataAccountSet(dataAccountSetHash, cryptoSetting,
		// PrefixAppender.prefix(DATA_SET_PREFIX, ledgerExStorage,
		// PrefixAppender.prefix(DATA_SET_PREFIX, ledgerVerStorage), readonly,
		// DEFAULT_ACCESS_POLICY);

		String datasetKeyPrefix = keyPrefix + DATA_SET_PREFIX;
		return new DataAccountSet(dataAccountSetHash, cryptoSetting, datasetKeyPrefix, ledgerExStorage,
				ledgerVerStorage, readonly, DEFAULT_ACCESS_POLICY);
	}

	static ContractAccountSet loadContractAccountSet(HashDigest contractAccountSetHash, CryptoSetting cryptoSetting,
			String keyPrefix, ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage,
			boolean readonly) {
		// return new ContractAccountSet(contractAccountSetHash, cryptoSetting,
		// PrefixAppender.prefix(CONTRACT_SET_PREFIX, ledgerExStorage,
		// PrefixAppender.prefix(CONTRACT_SET_PREFIX, ledgerVerStorage), readonly,
		// DEFAULT_ACCESS_POLICY);

		String contractsetKeyPrefix = keyPrefix + CONTRACT_SET_PREFIX;
		return new ContractAccountSet(contractAccountSetHash, cryptoSetting, contractsetKeyPrefix, ledgerExStorage,
				ledgerVerStorage, readonly, DEFAULT_ACCESS_POLICY);
	}

	static TransactionSet loadTransactionSet(HashDigest txsetHash, CryptoSetting cryptoSetting, String keyPrefix,
			ExPolicyKVStorage ledgerExStorage, VersioningKVStorage ledgerVerStorage, boolean readonly) {
		// return new TransactionSet(txsetHash, cryptoSetting,
		// PrefixAppender.prefix(TRANSACTION_SET_PREFIX, ledgerExStorage),
		// PrefixAppender.prefix(TRANSACTION_SET_PREFIX, ledgerVerStorage), readonly);

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
				LedgerBlock latestBlock = editor.getNewlyBlock();
				ledgerRepo.latestState = new LedgerState(latestBlock);
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

		private volatile LedgerAdminAccount adminAccount;

		private volatile UserAccountSet userAccountSet;

		private volatile DataAccountSet dataAccountSet;

		private volatile ContractAccountSet contractAccountSet;

		private volatile TransactionSet transactionSet;

		private volatile LedgerDataSet ledgerDataSet;

		public LedgerState(LedgerBlock block) {
			this.block = block;
		}

	}
}
