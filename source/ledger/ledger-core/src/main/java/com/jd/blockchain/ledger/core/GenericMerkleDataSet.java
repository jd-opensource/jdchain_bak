//package com.jd.blockchain.ledger.core;
//
//import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
//import com.jd.blockchain.crypto.hash.HashDigest;
//
//import my.utils.Transactional;
//import my.utils.io.ByteArray;
//import my.utils.io.ExistentialKVStorage;
//import my.utils.io.VersioningKVEntry;
//import my.utils.io.VersioningKVStorage;
//
///**
// * @author huanghaiquan
// *
// * @param <C>
// * @param <T>
// */
//public class GenericMerkleDataSet<C, T extends C> implements Transactional {
//
//	/**
//	 * 数据契约的接口类型；
//	 */
//	private Class<C> dataContractClazz;
//
//	/**
//	 * 数据契约的实现类型；
//	 */
//	private Class<T> dataClazz;
//
//	private MerkleDataSet merkleDataSet;
//
//	private Class<?>[] extImplClazzes;
//
//	/**
//	 * 创建一个新的 Merkle 数据集；
//	 * 
//	 * @param defaultMerkleHashAlgorithm
//	 * @param verifyMerkleHashOnLoad
//	 * @param merkleTreeStorage
//	 * @param dataStorage
//	 * @param dataContractClazz
//	 * @param dataClazz
//	 * @param extImplClazzes
//	 */
//	public GenericMerkleDataSet(CryptoSetting merkleTreeSetting, ExistentialKVStorage merkleTreeStorage,
//			VersioningKVStorage dataStorage, Class<C> dataContractClazz, Class<T> dataClazz,
//			Class<?>... extImplClazzes) {
//		if (!dataContractClazz.isAssignableFrom(dataClazz)) {
//			throw new IllegalArgumentException(String.format(
//					"The specified data class doesn't implement the specified data contract class! --[DataContractClass=%s][DataClass=%s]",
//					dataContractClazz.getName(), dataClazz.getName()));
//		}
//		this.merkleDataSet = new MerkleDataSet(merkleTreeSetting, merkleTreeStorage, dataStorage);
//		this.dataContractClazz = dataContractClazz;
//		this.dataClazz = dataClazz;
//		this.extImplClazzes = extImplClazzes;
//	}
//	
//	/**
//	 * 从指定的 Merkle 根创建 Merkle 数据集；
//	 * 
//	 * @param defaultMerkleHashAlgorithm
//	 * @param verifyMerkleHashOnLoad
//	 * @param merkleTreeStorage
//	 * @param dataStorage
//	 * @param dataContractClazz
//	 * @param dataClazz
//	 * @param extImplClazzes
//	 */
//	public GenericMerkleDataSet(HashDigest merkleRootHash, CryptoSetting merkleTreeSetting,
//			ExistentialKVStorage merkleTreeStorage, VersioningKVStorage dataStorage, boolean readonly, Class<C> dataContractClazz,
//			Class<T> dataClazz, Class<?>... extImplClazzes) {
//		if (!dataContractClazz.isAssignableFrom(dataClazz)) {
//			throw new IllegalArgumentException(String.format(
//					"The specified data class doesn't implement the specified data contract class! --[DataContractClass=%s][DataClass=%s]",
//					dataContractClazz.getName(), dataClazz.getName()));
//		}
//		this.merkleDataSet = new MerkleDataSet(merkleRootHash, merkleTreeSetting, merkleTreeStorage, dataStorage, readonly);
//		this.dataContractClazz = dataContractClazz;
//		this.dataClazz = dataClazz;
//		this.extImplClazzes = extImplClazzes;
//	}
//
//	/**
//	 * @return
//	 */
//	public HashDigest getRootHash() {
//		return merkleDataSet.getRootHash();
//	}
//
//	/**
//	 * Get the merkle proof of the specified key; <br>
//	 * 
//	 * The proof doesn't represent the latest changes until do
//	 * committing({@link #commit()}).
//	 * 
//	 * @param key
//	 * @return Return the {@link MerkleProof} instance, or null if the key doesn't
//	 *         exist.
//	 */
//	public MerkleProof getProof(String key) {
//		return merkleDataSet.getProof(key);
//	}
//
//	/**
//	 * Create or update the value associated the specified key if the version
//	 * checking is passed.<br>
//	 * 
//	 * The value of the key will be updated only if it's latest version equals the
//	 * specified version argument. <br>
//	 * If the key doesn't exist, the version checking will be ignored, and key will
//	 * be created with a new sequence number as id. <br>
//	 * It also could specify the version argument to -1 to ignore the version
//	 * checking.
//	 * <p>
//	 * If updating is performed, the version of the key increase by 1. <br>
//	 * If creating is performed, the version of the key initialize by 0. <br>
//	 * 
//	 * @param key
//	 *            The key of data;
//	 * @param value
//	 *            The value of data;
//	 * @param version
//	 *            The expected version of the key.
//	 */
//	public long setValue(String key, T value, long version) {
//		byte[] bytesValue = BinaryEncodingUtils.encode(value, dataContractClazz);
//		return merkleDataSet.setValue(key, bytesValue, version);
//	}
//
//	/**
//	 * 
//	 * @param key
//	 * @param version
//	 */
//	public T getValue(String key, long version) {
//		byte[] bytesValue = merkleDataSet.getValue(key, version);
//		return decode(bytesValue);
//	}
//
//	/**
//	 * 
//	 * @param version
//	 * @param key
//	 */
//	public T getValue(String key) {
//		byte[] bytesValue = merkleDataSet.getValue(key);
//		return decode(bytesValue);
//	}
//
//	public GenericDataEntry<T> getDataEntry(String key) {
//		VersioningKVEntry entry = merkleDataSet.getDataEntry(key);
//		T value = decode(entry.getValue());
//		return new GenericDataEntryWrapper<>(entry, value);
//	}
//
//	public GenericDataEntry<T> getDataEntry(String key, long version) {
//		VersioningKVEntry entry = merkleDataSet.getDataEntry(key, version);
//		T value = decode(entry.getValue());
//		return new GenericDataEntryWrapper<>(entry, value);
//	}
//
//	public GenericMerkleDataEntry<T> getMerkleEntry(String key, long version) {
//		GenericDataEntry<T> data = getDataEntry(key, version);
//		MerkleProof proof = merkleDataSet.getProof(key);
//		return new GenericMerkleDataEntryWrapperr<T>(data, proof);
//	}
//
//	public GenericMerkleDataEntry<T> getMerkleEntry(String key) {
//		GenericDataEntry<T> data = getDataEntry(key);
//		MerkleProof proof = merkleDataSet.getProof(key);
//		return new GenericMerkleDataEntryWrapperr<T>(data, proof);
//	}
//
//	@Override
//	public boolean isUpdated() {
//		return merkleDataSet.isUpdated();
//	}
//
//	@Override
//	public void commit() {
//		merkleDataSet.commit();
//	}
//
//	@Override
//	public void cancel() {
//		merkleDataSet.cancel();
//	}
//
//	private T decode(byte[] bytesValue) {
//		return BinaryEncodingUtils.decode(bytesValue, null, dataClazz);
//	}
//
//	/**
//	 * @author huanghaiquan
//	 *
//	 * @param <C>
//	 * @param <T>
//	 */
//	private static class GenericDataEntryWrapper<C, T extends C> implements GenericDataEntry<T> {
//
//		private String key;
//
//		private long version;
//
//		private T value;
//
//		public GenericDataEntryWrapper(VersioningKVEntry binaryEntry, T value) {
//			this.key = binaryEntry.getKey();
//			this.version = binaryEntry.getVersion();
//			this.value = value;
//		}
//
//		@Override
//		public String getKey() {
//			return key;
//		}
//
//		@Override
//		public long getVersion() {
//			return version;
//		}
//
//		@Override
//		public T getValue() {
//			return value;
//		}
//
//	}
//
//	/**
//	 * @author huanghaiquan
//	 *
//	 * @param <T>
//	 */
//	private static class GenericMerkleDataEntryWrapperr<T> implements GenericMerkleDataEntry<T> {
//
//		private GenericDataEntry<T> data;
//
//		private MerkleProof proof;
//
//		public GenericMerkleDataEntryWrapperr(GenericDataEntry<T> data, MerkleProof proof) {
//			this.data = data;
//			this.proof = proof;
//		}
//
//		@Override
//		public GenericDataEntry<T> getData() {
//			return data;
//		}
//
//		@Override
//		public MerkleProof getProof() {
//			return proof;
//		}
//
//	}
//
//}
