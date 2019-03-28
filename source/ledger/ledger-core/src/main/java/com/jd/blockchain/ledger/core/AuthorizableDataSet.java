//package com.jd.blockchain.ledger.core;
//
//import com.jd.blockchain.crypto.hash.HashDigest;
//
//import my.utils.Scratchable;
//import my.utils.io.ByteArray;
//import my.utils.io.BytesUtils;
//import my.utils.io.ExistancePolicyKVStorage;
//import my.utils.io.VersioningKVStorage;
//
///**
// * 可进行授权控制的数据集合；
// * 
// * @author huanghaiquan
// *
// */
//public class AuthorizableDataSet implements Scratchable {
//
//	public static final String DATA_PREFIX = "DATA" + LedgerConsts.KEY_SEPERATOR;
////	public static final String PRIVILEGE_PREFIX = "PRVL" + LedgerConsts.KEY_SEPERATOR;
//
//	private static final String DEFAULT_PRIVILEGE_KEY = "%";
//	
//	private DataAccessable accessable;
//
//	protected MerkleDataSet data;
//	
////	private PrivilegeDataSet privileges;
//
//	/**
//	 * Create a new Account instance;
//	 * 
//	 * @param address
//	 * @param pubKey
//	 */
//	protected AuthorizableDataSet(CryptoSetting merkleTreeSetting, ExistancePolicyKVStorage simpleStorage,
//			VersioningKVStorage versioningStorage) {
//		this(null, merkleTreeSetting, null, simpleStorage, versioningStorage);
//	}
//
//	protected AuthorizableDataSet(byte[] dataRootHash, CryptoSetting merkleTreeSetting, byte[] privilegeRootHash,
//			ExistancePolicyKVStorage simpleStorage, VersioningKVStorage versioningStorage) {
//		this(dataRootHash, merkleTreeSetting, privilegeRootHash, simpleStorage, versioningStorage, false);
//	}
//
//	protected AuthorizableDataSet(byte[] dataRootHash, CryptoSetting merkleTreeSetting, byte[] privilegeRootHash,
//			ExistancePolicyKVStorage simpleStorage, VersioningKVStorage versioningStorage, boolean readonly) {
//		this.data = new MerkleDataSet(dataRootHash, merkleTreeSetting,
//				PrefixAppender.prefix(DATA_PREFIX, simpleStorage),
//				PrefixAppender.prefix(DATA_PREFIX, versioningStorage), readonly);
//
////		this.privileges = new PrivilegeDataSet(privilegeRootHash, merkleTreeSetting,
////				PrefixAppender.prefix(PRIVILEGE_PREFIX, simpleStorage),
////				PrefixAppender.prefix(PRIVILEGE_PREFIX, versioningStorage), readonly);
//	}
//
//	public ByteArray getDataRootHash() {
//		return data.getRootHash();
//	}
//
////	public ByteArray getPrivilegeRootHash() {
////		return privileges.getRootHash();
////	}
//
//	/**
//	 * 
//	 * @param userAddress
//	 * @param op
//	 * @param enable
//	 */
//	public void setPrivilege(String userAddress, byte op, boolean enable) {
//
//	}
//
//	/**
//	 * 
//	 * @param op
//	 * @param enable
//	 */
//	public void setDefaultPrivilege(byte op, boolean enable) {
//	}
//
//	public boolean checkCurrentUserPrivilege() {
//		return false;
//	}
//
//	/**
//	 * Return the latest version entry associated the specified key; If the key
//	 * doesn't exist, then return -1;
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public long getVersion(String key) {
//		return data.getVersion(key);
//	}
//
//	protected long setString(String key, String value, long version) {
//		checkWritting();
//		byte[] bytes = BytesUtils.toBytes(value, LedgerConsts.CHARSET);
//		return data.setValue(key, bytes, version);
//	}
//
//	protected String getString(String key) {
//		checkReading();
//		byte[] value = data.getValue(key);
//		return BytesUtils.toString(value, LedgerConsts.CHARSET);
//	}
//
//	protected String getString(String key, long version) {
//		checkReading();
//		byte[] value = data.getValue(key, version);
//		return BytesUtils.toString(value, LedgerConsts.CHARSET);
//	}
//
//	protected long setValue(String key, byte[] value, long version) {
//		checkWritting();
//		return data.setValue(key, value, version);
//	}
//
//	protected byte[] getValue(String key) {
//		checkReading();
//		return data.getValue(key);
//	}
//
//	protected byte[] getValue(String key, long version) {
//		checkReading();
//		return data.getValue(key, version);
//	}
//
//	private void checkWritting() {
//		// Check writting enable;
//	}
//
//	private void checkReading() {
//		// TODO Check privilege of reading;
//	}
//
//	// /**
//	// * 数据“读”的操作码；
//	// *
//	// * @return
//	// */
//	// protected abstract AccountPrivilege getPrivilege();
//
//	@Override
//	public boolean isUpdated() {
//		return data.isUpdated();
////		return data.isUpdated()|| privileges.isUpdated();
//	}
//
//	@Override
//	public void commit() {
//		if (data.isUpdated()) {
//			data.commit();
//		}
////		if (privileges.isUpdated()) {
////			privileges.commit();
////		}
//	}
//
//	@Override
//	public void cancel() {
//		data.cancel();
////		privileges.cancel();
//	}
//
//}