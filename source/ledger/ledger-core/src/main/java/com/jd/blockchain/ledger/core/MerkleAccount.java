package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.HashProof;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.ledger.MerkleSnapshot;
import com.jd.blockchain.ledger.TypedBytesValue;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.Transactional;
import com.jd.blockchain.utils.VersioningKVEntry;
import com.jd.blockchain.utils.VersioningMap;

/**
 * 事务性的基础账户；
 * 
 * @author huanghaiquan
 *
 */
public class MerkleAccount implements LedgerAccount, HashProvable, MerkleSnapshot, Transactional {

	private static final Bytes HEADER_PREFIX = Bytes.fromString("HD/");
	private static final Bytes DATA_PREFIX = Bytes.fromString("DT/");

	private static final Bytes KEY_PUBKEY = Bytes.fromString("PUBKEY");

	private static final Bytes KEY_HEADER_ROOT = Bytes.fromString("HEADER");

	private static final Bytes KEY_DATA_ROOT = Bytes.fromString("DATA");

	private BlockchainIdentity accountID;

	private MerkleDataSet rootDS;

	private MerkleDatasetAdapter headerDS;

	private MerkleDatasetAdapter dataDS;
	
	protected long version;

	/**
	 * Create a new Account with the specified address and pubkey; <br>
	 *
	 * At the same time, a empty merkle dataset is also created for this account,
	 * which is used for storing data of this account.<br>
	 *
	 * Note that, the blockchain identity of the account is not stored in the
	 * account's merkle dataset, but is stored by the outer invoker;
	 *
	 * @param accountID     身份；
	 * @param version       版本；
	 * @param cryptoSetting 密码参数；
	 * @param keyPrefix     数据前缀；
	 * @param exStorage
	 * @param verStorage
	 */
	public MerkleAccount(BlockchainIdentity accountID, long version, CryptoSetting cryptoSetting, Bytes keyPrefix,
			ExPolicyKVStorage exStorage, VersioningKVStorage verStorage) {
		this(accountID.getAddress(), accountID.getPubKey(), version, null, cryptoSetting, keyPrefix, exStorage,
				verStorage, false);

		savePubKey(accountID.getPubKey());
	}

	/**
	 * Create a account instance with the specified address and pubkey and load it's
	 * merkle dataset from the specified root hash. This merkle dateset is used for
	 * storing data of this account.<br>
	 *
	 * @param accountID     identity of this account;
	 * @param version
	 * @param dataRootHash  merkle root hash of account's data; if set to a null
	 *                      value, an empty merkle dataset is created;
	 * @param cryptoSetting
	 * @param keyPrefix
	 * @param exStorage
	 * @param verStorage
	 * @param readonly
	 */
	public MerkleAccount(Bytes address, long version, HashDigest dataRootHash, CryptoSetting cryptoSetting,
			Bytes keyPrefix, ExPolicyKVStorage exStorage, VersioningKVStorage verStorage, boolean readonly) {
		this(address, null, version, dataRootHash, cryptoSetting, keyPrefix, exStorage, verStorage, readonly);
	}

	private MerkleAccount(Bytes address, PubKey pubKey, long version, HashDigest dataRootHash,
			CryptoSetting cryptoSetting, Bytes keyPrefix, ExPolicyKVStorage exStorage, VersioningKVStorage verStorage,
			boolean readonly) {
		this.accountID = new BlockchainIdentityProxy(address, pubKey);
		this.version = version;
		this.rootDS = new MerkleDataSet(dataRootHash, cryptoSetting, keyPrefix, exStorage, verStorage, readonly);

		// 初始化数据；
		DataChangedListener dataChangedListener = new DataChangedListener() {
			@Override
			public void onChanged(Bytes key, BytesValue value, long newVersion) {
				onUpdated(keyPrefix, value, newVersion);
			}
		};

		HashDigest headerRoot = loadHeaderRoot();
		Bytes headerPrefix = keyPrefix.concat(HEADER_PREFIX);
		MerkleDataSet headerDataset = new MerkleDataSet(headerRoot, cryptoSetting, headerPrefix, exStorage, verStorage,
				readonly);
		this.headerDS = new MerkleDatasetAdapter(headerDataset, dataChangedListener);

		HashDigest dataRoot = loadDataRoot();
		Bytes dataPrefix = keyPrefix.concat(DATA_PREFIX);
		MerkleDataSet dataDataset = new MerkleDataSet(dataRoot, cryptoSetting, dataPrefix, exStorage, verStorage,
				readonly);
		this.dataDS = new MerkleDatasetAdapter(dataDataset, dataChangedListener);
	}

	private HashDigest loadHeaderRoot() {
		byte[] hashBytes = rootDS.getValue(KEY_HEADER_ROOT);
		if (hashBytes == null) {
			return null;
		}
		return new HashDigest(hashBytes);
	}

	private HashDigest loadDataRoot() {
		byte[] hashBytes = rootDS.getValue(KEY_DATA_ROOT);
		if (hashBytes == null) {
			return null;
		}
		return new HashDigest(hashBytes);
	}

	public Bytes getAddress() {
		return accountID.getAddress();
	}

	public PubKey getPubKey() {
		return accountID.getPubKey();
	}

	@Override
	public BlockchainIdentity getID() {
		return accountID;
	}

	@Override
	public VersioningMap<Bytes, BytesValue> getDataset() {
		return dataDS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.AccountDataSet#getRootHash()
	 */
	@Override
	public HashDigest getRootHash() {
		return rootDS.getRootHash();
	}

	@Override
	public HashProof getProof(Bytes key) {
		MerkleProof dataProof = dataDS.getDataset().getProof(key);
		if (dataProof == null) {
			return null;
		}
		MerkleProof rootProof = rootDS.getProof(KEY_DATA_ROOT);
		if (rootProof == null) {
			return null;
		}
		HashDegistList proof = new HashDegistList(rootProof);
		proof.concat(dataProof);
		return proof;
	}

	/**
	 * 是否只读；
	 * 
	 * @return
	 */
	public boolean isReadonly() {
		return dataDS.getDataset().isReadonly();
	}

	public long getDataCount() {
		return dataDS.getDataset().getDataCount();
	}

	/**
	 * 保存公钥；
	 * 
	 * @param pubKey
	 */
	private void savePubKey(PubKey pubKey) {
		long v = headerDS.setValue(KEY_PUBKEY, TypedBytesValue.fromPubKey(pubKey), -1);
		if (v < 0) {
			throw new LedgerException("PubKey storage conflict!");
		}
	}

	/**
	 * 加载公钥；
	 * 
	 * @return
	 */
	private PubKey loadPubKey() {
		BytesValue bytesValue = headerDS.getValue(KEY_PUBKEY);
		return TypedBytesValue.wrap(bytesValue).pubKeyValue();
	}

	/**
	 * 当写入新值时触发此方法；
	 * 
	 * @param key
	 * @param value
	 * @param newVersion
	 */
	protected void onUpdated(Bytes key, BytesValue value, long newVersion) {
	}

	@Override
	public boolean isUpdated() {
		return dataDS.getDataset().isUpdated() || headerDS.getDataset().isUpdated() || rootDS.isUpdated();
	}

	@Override
	public void commit() {
		dataDS.getDataset().commit();
		headerDS.getDataset().commit();
		
		rootDS.setValue(key, value, version)
		
		baseDS.commit();
	}

	@Override
	public void cancel() {
		baseDS.cancel();
	}

	private class BlockchainIdentityProxy implements BlockchainIdentity {

		private Bytes address;

		private PubKey pubKey;

		public BlockchainIdentityProxy(Bytes address, PubKey pubKey) {
			this.address = address;
			this.pubKey = pubKey;
		}

		@Override
		public Bytes getAddress() {
			return address;
		}

		@Override
		public PubKey getPubKey() {
			if (pubKey == null) {
				pubKey = loadPubKey();
			}
			return pubKey;
		}

	}

	// ----------------------

	private static class MerkleDatasetAdapter implements VersioningMap<Bytes, BytesValue> {

		private static DataChangedListener NULL_LISTENER = new DataChangedListener() {
			@Override
			public void onChanged(Bytes key, BytesValue value, long newVersion) {
			}
		};

		private DataChangedListener changedListener;

		private MerkleDataSet dataset;

		public MerkleDataSet getDataset() {
			return dataset;
		}

		public MerkleDatasetAdapter(MerkleDataSet dataset) {
			this(dataset, NULL_LISTENER);
		}

		public MerkleDatasetAdapter(MerkleDataSet dataset, DataChangedListener listener) {
			this.dataset = dataset;
			this.changedListener = listener == null ? NULL_LISTENER : listener;
		}

		@Override
		public VersioningKVEntry<Bytes, BytesValue> getDataEntry(Bytes key) {
			return new VersioningKVEntryWraper(dataset.getDataEntry(key));
		}

		@Override
		public VersioningKVEntry<Bytes, BytesValue> getDataEntry(Bytes key, long version) {
			return new VersioningKVEntryWraper(dataset.getDataEntry(key, version));
		}

		/**
		 * Create or update the value associated the specified key if the version
		 * checking is passed.<br>
		 * 
		 * The value of the key will be updated only if it's latest version equals the
		 * specified version argument. <br>
		 * If the key doesn't exist, the version checking will be ignored, and key will
		 * be created with a new sequence number as id. <br>
		 * It also could specify the version argument to -1 to ignore the version
		 * checking.
		 * <p>
		 * If updating is performed, the version of the key increase by 1. <br>
		 * If creating is performed, the version of the key initialize by 0. <br>
		 * 
		 * @param key     The key of data;
		 * @param value   The value of data;
		 * @param version The expected version of the key.
		 * @return The new version of the key. <br>
		 *         If the key is new created success, then return 0; <br>
		 *         If the key is updated success, then return the new version;<br>
		 *         If this operation fail by version checking or other reason, then
		 *         return -1;
		 */
		@Override
		public long setValue(Bytes key, BytesValue value, long version) {
			byte[] bytesValue = BinaryProtocol.encode(value, BytesValue.class);
			long v = dataset.setValue(key, bytesValue, version);
			if (v > -1) {
				changedListener.onChanged(key, value, v);
			}
			return v;
		}

		/**
		 * Return the latest version entry associated the specified key; If the key
		 * doesn't exist, then return -1;
		 * 
		 * @param key
		 * @return
		 */
		@Override
		public long getVersion(Bytes key) {
			return dataset.getVersion(key);
		}

		/**
		 * return the latest version's value;
		 * 
		 * @param key
		 * @return return null if not exist;
		 */
		@Override
		public BytesValue getValue(Bytes key) {
			byte[] bytesValue = dataset.getValue(key);
			if (bytesValue == null) {
				return null;
			}
			return BinaryProtocol.decodeAs(bytesValue, BytesValue.class);
		}

		/**
		 * Return the specified version's value;
		 * 
		 * @param key
		 * @param version
		 * @return return null if not exist;
		 */
		@Override
		public BytesValue getValue(Bytes key, long version) {
			byte[] bytesValue = dataset.getValue(key, version);
			if (bytesValue == null) {
				return null;
			}
			return BinaryProtocol.decodeAs(bytesValue, BytesValue.class);
		}
	}

	private static interface DataChangedListener {

		void onChanged(Bytes key, BytesValue value, long newVersion);

	}

	private static class VersioningKVEntryWraper implements VersioningKVEntry<Bytes, BytesValue> {

		private VersioningKVEntry<Bytes, byte[]> kv;

		public VersioningKVEntryWraper(VersioningKVEntry<Bytes, byte[]> kv) {
			this.kv = kv;
		}

		@Override
		public Bytes getKey() {
			return kv.getKey();
		}

		@Override
		public long getVersion() {
			return kv.getVersion();
		}

		@Override
		public BytesValue getValue() {
			return BinaryProtocol.decodeAs(kv.getValue(), BytesValue.class);
		}

	}

}