package com.jd.blockchain.ledger.core;

public class DataAccount extends AccountDecorator {

	public DataAccount(CompositeAccount mklAccount) {
		super(mklAccount);
	}

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
//	 * @param key     The key of data;
//	 * @param value   The value of data;
//	 * @param version The expected version of the key.
//	 * @return The new version of the key. <br>
//	 *         If the key is new created success, then return 0; <br>
//	 *         If the key is updated success, then return the new version;<br>
//	 *         If this operation fail by version checking or other reason, then
//	 *         return -1;
//	 */
//	public long setBytes(Bytes key, BytesValue value, long version) {
//		return super.getDataset().setValue(key, value, version);
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
//	 * @param key     The key of data;
//	 * @param value   The value of data;
//	 * @param version The expected version of the key.
//	 * @return The new version of the key. <br>
//	 *         If the key is new created success, then return 0; <br>
//	 *         If the key is updated success, then return the new version;<br>
//	 *         If this operation fail by version checking or other reason, then
//	 *         return -1;
//	 */
//	public long setBytes(Bytes key, String value, long version) {
//		BytesValue bytesValue = TypedValue.fromText(value);
//		return baseAccount.setValue(key, bytesValue, version);
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
//	 * @param key     The key of data;
//	 * @param value   The value of data;
//	 * @param version The expected version of the key.
//	 * @return The new version of the key. <br>
//	 *         If the key is new created success, then return 0; <br>
//	 *         If the key is updated success, then return the new version;<br>
//	 *         If this operation fail by version checking or other reason, then
//	 *         return -1;
//	 */
//	public long setBytes(Bytes key, byte[] value, long version) {
//		BytesValue bytesValue = TypedValue.fromBytes(value);
//		return baseAccount.setValue(key, bytesValue, version);
//	}
//
//	/**
//	 * Return the latest version entry associated the specified key; If the key
//	 * doesn't exist, then return -1;
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public long getDataVersion(String key) {
//		return baseAccount.getVersion(Bytes.fromString(key));
//	}
//
//	/**
//	 * Return the latest version entry associated the specified key; If the key
//	 * doesn't exist, then return -1;
//	 * 
//	 * @param key
//	 * @return
//	 */
//	public long getDataVersion(Bytes key) {
//		return baseAccount.getVersion(key);
//	}
//
//	/**
//	 * return the latest version's value;
//	 * 
//	 * @param key
//	 * @return return null if not exist;
//	 */
//	public BytesValue getBytes(String key) {
//		return baseAccount.getValue(Bytes.fromString(key));
//	}
//
//	/**
//	 * return the latest version's value;
//	 * 
//	 * @param key
//	 * @return return null if not exist;
//	 */
//	public BytesValue getBytes(Bytes key) {
//		return baseAccount.getValue(key);
//	}
//
//	/**
//	 * return the specified version's value;
//	 * 
//	 * @param key
//	 * @param version
//	 * @return return null if not exist;
//	 */
//	public BytesValue getBytes(String key, long version) {
//		return baseAccount.getValue(Bytes.fromString(key), version);
//	}
//
//	/**
//	 * return the specified version's value;
//	 * 
//	 * @param key
//	 * @param version
//	 * @return return null if not exist;
//	 */
//	public BytesValue getBytes(Bytes key, long version) {
//		return baseAccount.getValue(key, version);
//	}
	
	
	
	
//	/**
//	 * @param key
//	 * @param version
//	 * @return
//	 */
//	public KVDataEntry getDataEntry(String key, long version) {
//		return getDataEntry(Bytes.fromString(key), version);
//	}
//	
//	/**
//	 * @param key
//	 * @param version
//	 * @return
//	 */
//	public KVDataEntry getDataEntry(Bytes key, long version) {
//		BytesValue value = baseAccount.getValue(key, version);
//		if (value == null) {
//			return new KVDataObject(key.toUTF8String(), -1,  null);
//		}else {
//			return new KVDataObject(key.toUTF8String(), version,  value);
//		}
//	}
//
//	/**
//	 * return the specified index's KVDataEntry;
//	 *
//	 * @param fromIndex
//	 * @param count
//	 * @return return null if not exist;
//	 */
//
//	public KVDataEntry[] getDataEntries(int fromIndex, int count) {
//		if (count == 0 || getDataEntriesTotalCount() == 0) {
//			return null;
//		}
//
//		if (count == -1 || count > getDataEntriesTotalCount()) {
//			fromIndex = 0;
//			count = (int)getDataEntriesTotalCount();
//		}
//
//		if (fromIndex < 0 || fromIndex > getDataEntriesTotalCount() - 1) {
//			fromIndex = 0;
//		}
//
//		KVDataEntry[] kvDataEntries = new KVDataEntry[count];
//		byte[] value;
//		String key;
//		long ver;
//		for (int i = 0; i < count; i++) {
//			value = baseAccount.dataset.getValuesAtIndex(fromIndex);
//			key = baseAccount.dataset.getKeyAtIndex(fromIndex);
//			ver = baseAccount.dataset.getVersion(key);
//			BytesValue decodeData = BinaryProtocol.decode(value);
//			kvDataEntries[i] = new KVDataObject(key, ver,  decodeData);
//			fromIndex++;
//		}
//
//		return kvDataEntries;
//	}
//
//	/**
//	 * return the dataAccount's kv total count;
//	 *
//	 * @param
//	 * @param
//	 * @return return total count;
//	 */
//	public long getDataEntriesTotalCount() {
//		if(baseAccount == null){
//			return 0;
//		}
//		return baseAccount.dataset.getDataCount();
//	}
}