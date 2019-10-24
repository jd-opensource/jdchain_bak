package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.MerkleDataNode;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.ExPolicyKVStorage.ExPolicy;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.utils.BufferedKVStorage;
import com.jd.blockchain.storage.service.utils.VersioningKVData;
import com.jd.blockchain.utils.ArrayUtils;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;
import com.jd.blockchain.utils.DataIterator;
import com.jd.blockchain.utils.Dataset;
import com.jd.blockchain.utils.Transactional;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * 对新的数据项按顺序递增进行编号的 Merkle 数据集； <br>
 * 
 * 注：此实现不是线程安全的；
 * 
 * @author huanghaiquan
 *
 */
public class MerkleDataSet implements Transactional, MerkleProvable, Dataset<Bytes, byte[]> {

	/**
	 * 4 MB MaxSize of value;
	 */
	public static final int MAX_SIZE_OF_VALUE = 4 * 1024 * 1024;

	public static final Bytes SN_PREFIX = Bytes.fromString("SN" + LedgerConsts.KEY_SEPERATOR);
	public static final Bytes DATA_PREFIX = Bytes.fromString("KV" + LedgerConsts.KEY_SEPERATOR);
	public static final Bytes MERKLE_TREE_PREFIX = Bytes.fromString("MKL" + LedgerConsts.KEY_SEPERATOR);

	private final Bytes snKeyPrefix;
	private final Bytes dataKeyPrefix;
	private final Bytes merkleKeyPrefix;

	@SuppressWarnings("unchecked")
	private static final DataEntry<Bytes, byte[]>[] EMPTY_ENTRIES = new DataEntry[0];

	private BufferedKVStorage bufferedStorage;

	private VersioningKVStorage valueStorage;

	private ExPolicyKVStorage snStorage;

	private MerkleTree merkleTree;

	private SNGenerator snGenerator;

	private boolean readonly;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.MerkleProvable#getRootHash()
	 */
	@Override
	public HashDigest getRootHash() {
		return merkleTree.getRootHash();
	}

	/**
	 * 创建一个新的 MerkleDataSet；
	 * 
	 * @param setting           密码设置；
	 * @param exPolicyStorage   默克尔树的存储；
	 * @param versioningStorage 数据的存储；
	 */
	public MerkleDataSet(CryptoSetting setting, String keyPrefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage versioningStorage) {
		this(setting, Bytes.fromString(keyPrefix), exPolicyStorage, versioningStorage);
	}

	/**
	 * 创建一个新的 MerkleDataSet；
	 * 
	 * @param setting           密码设置；
	 * @param exPolicyStorage   默克尔树的存储；
	 * @param versioningStorage 数据的存储；
	 */
	public MerkleDataSet(CryptoSetting setting, Bytes keyPrefix, ExPolicyKVStorage exPolicyStorage,
			VersioningKVStorage versioningStorage) {
		// 缓冲对KV的写入；
		this.bufferedStorage = new BufferedKVStorage(exPolicyStorage, versioningStorage, false);

		// 把存储数据值、SN、Merkle节点的 key 分别加入独立的前缀，避免针对 key 的注入攻击；
		// this.valueStorage = PrefixAppender.prefix(DATA_PREFIX, (VersioningKVStorage)
		// bufferedStorage);
		// this.snStorage = PrefixAppender.prefix(SN_PREFIX, (ExPolicyKVStorage)
		// bufferedStorage);
		snKeyPrefix = keyPrefix.concat(SN_PREFIX);
		dataKeyPrefix = keyPrefix.concat(DATA_PREFIX);
		this.valueStorage = bufferedStorage;
		this.snStorage = bufferedStorage;

		// MerkleTree 本身是可缓冲的；
		// ExPolicyKVStorage merkleTreeStorage =
		// PrefixAppender.prefix(MERKLE_TREE_PREFIX, exPolicyStorage);
		merkleKeyPrefix = keyPrefix.concat(MERKLE_TREE_PREFIX);
		ExPolicyKVStorage merkleTreeStorage = exPolicyStorage;
		this.merkleTree = new MerkleTree(setting, merkleKeyPrefix, merkleTreeStorage);
		this.snGenerator = new MerkleSequenceSNGenerator(merkleTree);
	}

	/**
	 * 从指定的 Merkle 根构建的 MerkleDataSet；
	 * 
	 * @param dataStorage
	 * @param defaultMerkleHashAlgorithm
	 * @param verifyMerkleHashOnLoad
	 * @param merkleTreeStorage
	 * @param snGenerator
	 */
	public MerkleDataSet(HashDigest merkleRootHash, CryptoSetting setting, String keyPrefix,
			ExPolicyKVStorage exPolicyStorage, VersioningKVStorage versioningStorage, boolean readonly) {
		this(merkleRootHash, setting, Bytes.fromString(keyPrefix), exPolicyStorage, versioningStorage, readonly);
	}

	/**
	 * 从指定的 Merkle 根构建的 MerkleDataSet；
	 * 
	 * @param dataStorage
	 * @param defaultMerkleHashAlgorithm
	 * @param verifyMerkleHashOnLoad
	 * @param merkleTreeStorage
	 * @param snGenerator
	 */
	public MerkleDataSet(HashDigest merkleRootHash, CryptoSetting setting, Bytes keyPrefix,
			ExPolicyKVStorage exPolicyStorage, VersioningKVStorage versioningStorage, boolean readonly) {
		// 缓冲对KV的写入；
		this.bufferedStorage = new BufferedKVStorage(exPolicyStorage, versioningStorage, false);

		// 把存储数据值、SN、Merkle节点的 key 分别加入独立的前缀，避免针对 key 的注入攻击；
//		snKeyPrefix = Bytes.fromString(keyPrefix + SN_PREFIX);
//		dataKeyPrefix = Bytes.fromString(keyPrefix + DATA_PREFIX);
		snKeyPrefix = keyPrefix.concat(SN_PREFIX);
		dataKeyPrefix = keyPrefix.concat(DATA_PREFIX);
		this.valueStorage = bufferedStorage;
		this.snStorage = bufferedStorage;

		// MerkleTree 本身是可缓冲的；
		merkleKeyPrefix = keyPrefix.concat(MERKLE_TREE_PREFIX);
		ExPolicyKVStorage merkleTreeStorage = exPolicyStorage;
		this.merkleTree = new MerkleTree(merkleRootHash, setting, merkleKeyPrefix, merkleTreeStorage, readonly);

		this.snGenerator = new MerkleSequenceSNGenerator(merkleTree);
		this.readonly = readonly;
	}

	public boolean isReadonly() {
		return readonly;
	}

	void setReadonly() {
		this.readonly = true;
	}

	@Override
	public long getDataCount() {
		return merkleTree.getDataCount();
	}

	/**
	 * 返回理论上允许的最大数据索引；
	 * 
	 * @return
	 */
	public long getMaxIndex() {
		return merkleTree.getMaxSn();
	}

	public byte[][] getLatestValues(long fromIndex, int count) {
		if (count > LedgerConsts.MAX_LIST_COUNT) {
			throw new IllegalArgumentException("Count exceed the upper limit[" + LedgerConsts.MAX_LIST_COUNT + "]!");
		}
		if (fromIndex < 0 || (fromIndex + count) > merkleTree.getDataCount()) {
			throw new IllegalArgumentException("Index out of bound!");
		}
		byte[][] values = new byte[count][];
		for (int i = 0; i < count; i++) {
			MerkleDataNode dataNode = merkleTree.getData(fromIndex + i);
			Bytes dataKey = encodeDataKey(dataNode.getKey());
			values[i] = valueStorage.get(dataKey, dataNode.getVersion());
		}
		return values;
	}

	public DataEntry<Bytes, byte[]>[] getLatestDataEntries(long fromIndex, int count) {
		if (count > LedgerConsts.MAX_LIST_COUNT) {
			throw new IllegalArgumentException("Count exceed the upper limit[" + LedgerConsts.MAX_LIST_COUNT + "]!");
		}
		if (fromIndex < 0 || (fromIndex + count) > merkleTree.getDataCount()) {
			throw new IllegalArgumentException("Index out of bound!");
		}
		if (count == 0) {
			return EMPTY_ENTRIES;
		}
		@SuppressWarnings("unchecked")
		DataEntry<Bytes, byte[]>[] values = new DataEntry[count];
		byte[] bytesValue;
		for (int i = 0; i < count; i++) {
			MerkleDataNode dataNode = merkleTree.getData(fromIndex + i);
			Bytes dataKey = encodeDataKey(dataNode.getKey());
			bytesValue = valueStorage.get(dataKey, dataNode.getVersion());
			values[i] = new VersioningKVData<Bytes, byte[]>(dataNode.getKey(), dataNode.getVersion(), bytesValue);
		}
		return values;
	}

	public DataEntry<Bytes, byte[]> getLatestDataEntry(long index) {
		if (index < 0 || index + 1 > merkleTree.getDataCount()) {
			throw new IllegalArgumentException("Index out of bound!");
		}
		byte[] bytesValue;
		MerkleDataNode dataNode = merkleTree.getData(index);
		Bytes dataKey = encodeDataKey(dataNode.getKey());
		bytesValue = valueStorage.get(dataKey, dataNode.getVersion());
		DataEntry<Bytes, byte[]> entry = new VersioningKVData<Bytes, byte[]>(dataNode.getKey(), dataNode.getVersion(),
				bytesValue);
		return entry;
	}

	/**
	 * get the data at the specific index;
	 * 
	 * @param fromIndex
	 * @return
	 */
	public byte[] getValuesAtIndex(int fromIndex) {
		MerkleDataNode dataNode = merkleTree.getData(fromIndex);
		Bytes dataKey = encodeDataKey(dataNode.getKey());
		return valueStorage.get(dataKey, dataNode.getVersion());
	}

	/**
	 * get the key at the specific index;
	 * 
	 * @param fromIndex
	 * @return
	 */
	public String getKeyAtIndex(int fromIndex) {
		MerkleDataNode dataNode = merkleTree.getData(fromIndex);
		// TODO: 未去掉前缀；
		return dataNode.getKey().toUTF8String();
	}

//	/**
//	 * Create or update the value associated the specified key if the version
//	 * checking is passed.<br>
//	 * 
//	 * The value of the key will be updated only if it's latest version equals the
//	 * specified version argument. <br>
//	 * If the key doesn't exist, it will be created when the version arg was -1.
//	 * <p>
//	 * If updating is performed, the version of the key increase by 1. <br>
//	 * If creating is performed, the version of the key initialize by 0. <br>
//	 * 
//	 * @param key     The key of data;
//	 * @param value   The value of data;
//	 * @param version The expected latest version of the key.
//	 * @return The new version of the key. <br>
//	 *         If the key is new created success, then return 0; <br>
//	 *         If the key is updated success, then return the new version;<br>
//	 *         If this operation fail by version checking or other reason, then
//	 *         return -1;
//	 */
//	@Override
//	public long setValue(String key, byte[] value, long version) {
//		return setValue(Bytes.fromString(key), value, version);
//	}

	/**
	 * Create or update the value associated the specified key if the version
	 * checking is passed.<br>
	 * 
	 * The value of the key will be updated only if it's latest version equals the
	 * specified version argument. <br>
	 * If the key doesn't exist, it will be created when the version arg was -1.
	 * <p>
	 * If updating is performed, the version of the key increase by 1. <br>
	 * If creating is performed, the version of the key initialize by 0. <br>
	 * 
	 * @param key     The key of data;
	 * @param value   The value of data;
	 * @param version The expected latest version of the key.
	 * @return The new version of the key. <br>
	 *         If the key is new created success, then return 0; <br>
	 *         If the key is updated success, then return the new version;<br>
	 *         If this operation fail by version checking or other reason, then
	 *         return -1;
	 */
	@Override
	public long setValue(Bytes key, byte[] value, long version) {
		if (readonly) {
			throw new IllegalArgumentException("This merkle dataset is readonly!");
		}
		if (value.length > MAX_SIZE_OF_VALUE) {
			throw new IllegalArgumentException(
					"The size of value is great than the max size[" + MAX_SIZE_OF_VALUE + "]!");
		}
		Bytes dataKey = encodeDataKey(key);
		long latestVersion = valueStorage.getVersion(dataKey);
		if (version != latestVersion) {
			return -1;
		}

		// set into versioning kv storage before adding to merkle tree, in order to
		// check version confliction first;
		long sn;
		long newVersion;
		if (version < 0) {
			// creating ;
			sn = snGenerator.generate(key);
			newVersion = valueStorage.set(dataKey, value, -1);
			if (newVersion < 0) {
				return -1;
			}
			byte[] snBytes = BytesUtils.toBytes(sn);
			Bytes snKey = encodeSNKey(key);
			boolean nx = snStorage.set(snKey, snBytes, ExPolicy.NOT_EXISTING);
			if (!nx) {
				throw new LedgerException("SN already exist! --[KEY=" + key + "]");
			}
		} else {
			// updating;

			// TODO: 未在当前实例的层面，实现对输入键-值的缓冲，而直接写入了存储，而 MerkleTree 在未调用 commit
			// 之前是缓冲的，这使得在存储层面的数据会不一致，而未来需要优化；
			newVersion = valueStorage.set(dataKey, value, version);
			if (newVersion < 0) {
				return -1;
			}

			sn = getSN(key);
		}

		// update merkle tree;
		merkleTree.setData(sn, key, newVersion, value);
		// TODO: 未在当前实例的层面，实现对输入键-值的缓冲，而直接写入了存储，而 MerkleTree 在未调用 commit
		// 之前是缓冲的，这使得在存储层面的数据会不一致，而未来需要优化；

		return newVersion;
	}

	private Bytes encodeSNKey(Bytes key) {
		return new Bytes(snKeyPrefix, key);
	}

	private Bytes encodeDataKey(Bytes key) {
		return new Bytes(dataKeyPrefix, key);
	}

	/**
	 * 返回指定 key 对应的序号，如果不存在，则返回 -1；
	 * 
	 * @param key
	 * @return
	 */
	private long getSN(Bytes key) {
		// SN-KEY index entry has never changed;
		Bytes snKey = encodeSNKey(key);
		byte[] snBytes = snStorage.get(snKey);
		if (snBytes == null) {
			// throw new IllegalStateException("Cann't found SN of key[" + key + "] from
			// data storage!");
			return -1;
		}
		return BytesUtils.toLong(snBytes);
	}

	/**
	 * 返回默克尔树中记录的指定键的版本，在由默克尔树表示的数据集的快照中，这是指定键的最新版本，<br>
	 * 但该版本有可能小于实际存储的最新版本（由于后续追加的新修改被之后生成的快照维护）；
	 * 
	 * @param key
	 * @return 返回指定的键的版本；如果不存在，则返回 -1；
	 */
	private long getMerkleVersion(Bytes key) {
		long sn = getSN(key);
		if (sn < 0) {
			return -1;
		}
		MerkleDataNode mdn = merkleTree.getData(sn);
		if (mdn == null) {
			return -1;
		}
		return mdn.getVersion();
	}

//	/**
//	 * Return the specified version's value;<br>
//	 * 
//	 * If the key with the specified version doesn't exist, then return null;<br>
//	 * If the version is specified to -1, then return the latest version's value;
//	 * 
//	 * @param key
//	 * @param version
//	 */
//	@Override
//	public byte[] getValue(String key, long version) {
//		return getValue(Bytes.fromString(key), version);
//	}

	/**
	 * Return the specified version's value;<br>
	 * 
	 * If the key with the specified version doesn't exist, then return null;<br>
	 * If the version is specified to -1, then return the latest version's value;
	 * 
	 * @param key
	 * @param version
	 */
	@Override
	public byte[] getValue(Bytes key, long version) {
		long latestVersion = getMerkleVersion(key);
		if (latestVersion < 0 || version > latestVersion) {
			// key not exist, or the specified version is out of the latest version indexed
			// by the current merkletree;
			return null;
		}
		version = version < 0 ? latestVersion : version;
		Bytes dataKey = encodeDataKey(key);
		return valueStorage.get(dataKey, version);
	}

//	/**
//	 * Return the latest version's value;
//	 * 
//	 * @param key
//	 * @return return null if not exist;
//	 */
//	@Override
//	public byte[] getValue(String key) {
//		return getValue(Bytes.fromString(key));
//	}

	/**
	 * Return the latest version's value;
	 * 
	 * @param key
	 * @return return null if not exist;
	 */
	@Override
	public byte[] getValue(Bytes key) {
		long latestVersion = getMerkleVersion(key);
		if (latestVersion < 0) {
			return null;
		}
		Bytes dataKey = encodeDataKey(key);
		return valueStorage.get(dataKey, latestVersion);
	}

//	/**
//	 * Return the latest version entry associated the specified key; If the key
//	 * doesn't exist, then return -1;
//	 * 
//	 * @param key
//	 * @return
//	 */
//	@Override
//	public long getVersion(String key) {
//		return getMerkleVersion(Bytes.fromString(key));
//	}

	/**
	 * Return the latest version entry associated the specified key; If the key
	 * doesn't exist, then return -1;
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public long getVersion(Bytes key) {
		return getMerkleVersion(key);
	}

//	@Override
//	public VersioningKVEntry<String, byte[]> getDataEntry(String key) {
//		return getDataEntry(key, -1);
//	}

	/**
	 * 
	 * @param key
	 * @return Null if the key doesn't exist!
	 */
	@Override
	public DataEntry<Bytes, byte[]> getDataEntry(Bytes key) {
		return getDataEntry(key, -1);
	}

//	@Override
//	public VersioningKVEntry<String, byte[]> getDataEntry(String key, long version) {
//		Bytes keyBytes = Bytes.fromString(key);
//		long latestVersion = getMerkleVersion(keyBytes);
//		if (latestVersion < 0 || version > latestVersion) {
//			// key not exist, or the specified version is out of the latest version indexed
//			// by the current merkletree;
//			return null;
//		}
//		version = version < 0 ? latestVersion : version;
//		Bytes dataKey = encodeDataKey(keyBytes);
//		byte[] value = valueStorage.get(dataKey, version);
//		if (value == null) {
//			return null;
//		}
//		return new VersioningKVData<String, byte[]>(key, version, value);
//	}

	@Override
	public DataEntry<Bytes, byte[]> getDataEntry(Bytes key, long version) {
		long latestVersion = getMerkleVersion(key);
		if (latestVersion < 0 || version > latestVersion) {
			// key not exist, or the specified version is out of the latest version indexed
			// by the current merkletree;
			return null;
		}
		version = version < 0 ? latestVersion : version;
		Bytes dataKey = encodeDataKey(key);
		byte[] value = valueStorage.get(dataKey, version);
		if (value == null) {
			return null;
		}
		return new VersioningKVData<Bytes, byte[]>(key, version, value);
	}

	@Override
	public DataIterator<Bytes, byte[]> iterator() {
		return new AscDataInterator(getDataCount());
	}

	@Override
	public DataIterator<Bytes, byte[]> iteratorDesc() {
		return new DescDataInterator(getDataCount());
	}

	public MerkleDataEntry getMerkleEntry(Bytes key, long version) {
		DataEntry<Bytes, byte[]> dataEntry = getDataEntry(key, version);
		if (dataEntry == null) {
			return null;
		}
		MerkleProof proof = getProof(key);
		return new MerkleDataEntryWrapper(dataEntry, proof);
	}

	public MerkleDataEntry getMerkleEntry(Bytes key) {
		DataEntry<Bytes, byte[]> dataEntry = getDataEntry(key);
		if (dataEntry == null) {
			return null;
		}
		MerkleProof proof = getProof(key);
		return new MerkleDataEntryWrapper(dataEntry, proof);
	}

	public MerkleProof getProof(String key) {
		return getProof(Bytes.fromString(key));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.MerkleProvable#getProof(java.lang.String)
	 */
	@Override
	public MerkleProof getProof(Bytes key) {
		long sn = getSN(key);
		if (sn < 0) {
			return null;
		}
		return merkleTree.getProof(sn);
	}

	/**
	 * A wrapper for {@link DataEntry} and {@link MerkleProof};
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class MerkleDataEntryWrapper implements MerkleDataEntry {

		private DataEntry<Bytes, byte[]> data;
		private MerkleProof proof;

		public MerkleDataEntryWrapper(DataEntry<Bytes, byte[]> data, MerkleProof proof) {
			this.data = data;
			this.proof = proof;
		}

		@Override
		public DataEntry<Bytes, byte[]> getData() {
			return data;
		}

		@Override
		public MerkleProof getProof() {
			return proof;
		}

	}

	@Override
	public boolean isUpdated() {
		return bufferedStorage.isUpdated() || merkleTree.isUpdated();
	}

	@Override
	public void commit() {
		bufferedStorage.commit();
		merkleTree.commit();
	}

	@Override
	public void cancel() {
		bufferedStorage.cancel();
		merkleTree.cancel();
		snGenerator = new MerkleSequenceSNGenerator(merkleTree);
	}

	// ----------------------------------------------------------

	private class AscDataInterator implements DataIterator<Bytes, byte[]> {

		private final long total;

		private long cursor = 0;

		public AscDataInterator(long total) {
			this.total = total;
		}

		@Override
		public void skip(long count) {
			cursor = nextCursor(count);
		}

		private long nextCursor(long skippingCount) {
			long c = cursor + skippingCount;
			return c > total ? total : c;
		}

		@Override
		public DataEntry<Bytes, byte[]> next() {
			if (hasNext()) {
				DataEntry<Bytes, byte[]> entry = getLatestDataEntry(cursor);
				cursor = nextCursor(1);
				return entry;
			}
			return null;
		}

		@Override
		public DataEntry<Bytes, byte[]>[] next(int count) {
			if (hasNext()) {
				long from = cursor;
				long nextCursor = nextCursor(count);
				long c = nextCursor - cursor;
				if (c > LedgerConsts.MAX_LIST_COUNT) {
					throw new IllegalArgumentException(
							"Count exceed the upper limit[" + LedgerConsts.MAX_LIST_COUNT + "]!");
				}
				DataEntry<Bytes, byte[]>[] entries = getLatestDataEntries(from, (int) c);
				cursor = nextCursor;
				return entries;
			}
			return EMPTY_ENTRIES;
		}

		@Override
		public boolean hasNext() {
			return cursor < total;
		}

	}

	private class DescDataInterator implements DataIterator<Bytes, byte[]> {

		private final long total;

		private long cursor;

		public DescDataInterator(long total) {
			this.total = total;
			this.cursor = total - 1;
		}

		@Override
		public void skip(long count) {
			cursor = nextCursor(count);
		}

		private long nextCursor(long skippingCount) {
			long c = cursor - skippingCount;
			return c < 0 ? -1 : c;
		}

		@Override
		public DataEntry<Bytes, byte[]> next() {
			if (hasNext()) {
				DataEntry<Bytes, byte[]> entry = getLatestDataEntry(cursor);
				cursor = nextCursor(1);
				return entry;
			}
			return null;
		}

		@Override
		public DataEntry<Bytes, byte[]>[] next(int count) {
			if (hasNext()) {
				long nextCursor = nextCursor(count);
				long from = nextCursor + 1;
				long c = cursor - nextCursor;
				if (c > LedgerConsts.MAX_LIST_COUNT) {
					throw new IllegalArgumentException(
							"Count exceed the upper limit[" + LedgerConsts.MAX_LIST_COUNT + "]!");
				}
				DataEntry<Bytes, byte[]>[] entries = getLatestDataEntries(from, (int) c);
				// reverse;
				ArrayUtils.reverse(entries);

				cursor = nextCursor;
				return entries;
			}
			return EMPTY_ENTRIES;
		}

		@Override
		public boolean hasNext() {
			return cursor < total;
		}

	}

}