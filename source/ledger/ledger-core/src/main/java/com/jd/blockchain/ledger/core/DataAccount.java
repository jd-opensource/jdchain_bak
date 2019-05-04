package com.jd.blockchain.ledger.core;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.KVDataEntry;
import com.jd.blockchain.ledger.KVDataObject;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

public class DataAccount implements AccountHeader, MerkleProvable {

	private BaseAccount baseAccount;

	public DataAccount(BaseAccount accBase) {
		this.baseAccount = accBase;
	}

	@Override
	public Bytes getAddress() {
		return baseAccount.getAddress();
	}

	@Override
	public PubKey getPubKey() {
		return baseAccount.getPubKey();
	}

	@Override
	public HashDigest getRootHash() {
		return baseAccount.getRootHash();
	}

	/**
	 * 返回指定数据的存在性证明；
	 */
	@Override
	public MerkleProof getProof(Bytes key) {
		return baseAccount.getProof(key);
	}

	public long setBytes(Bytes key, byte[] value, long version) {
		return baseAccount.setBytes(key, value, version);
	}
	
	/**
	 * Return the latest version entry associated the specified key; If the key
	 * doesn't exist, then return -1;
	 * 
	 * @param key
	 * @return
	 */
	public long getDataVersion(String key) {
		return baseAccount.getKeyVersion(Bytes.fromString(key));
	}

	/**
	 * Return the latest version entry associated the specified key; If the key
	 * doesn't exist, then return -1;
	 * 
	 * @param key
	 * @return
	 */
	public long getDataVersion(Bytes key) {
		return baseAccount.getKeyVersion(key);
	}

	/**
	 * return the latest version's value;
	 * 
	 * @param key
	 * @return return null if not exist;
	 */
	public byte[] getBytes(String key) {
		return baseAccount.getBytes(Bytes.fromString(key));
	}

	/**
	 * return the latest version's value;
	 * 
	 * @param key
	 * @return return null if not exist;
	 */
	public byte[] getBytes(Bytes key) {
		return baseAccount.getBytes(key);
	}

	/**
	 * return the specified version's value;
	 * 
	 * @param key
	 * @param version
	 * @return return null if not exist;
	 */
	public byte[] getBytes(String key, long version) {
		return baseAccount.getBytes(Bytes.fromString(key), version);
	}

	/**
	 * return the specified version's value;
	 * 
	 * @param key
	 * @param version
	 * @return return null if not exist;
	 */
	public byte[] getBytes(Bytes key, long version) {
		return baseAccount.getBytes(key, version);
	}

	/**
	 * return the specified index's KVDataEntry;
	 *
	 * @param fromIndex
	 * @param count
	 * @return return null if not exist;
	 */

	public KVDataEntry[] getDataEntries(int fromIndex, int count) {

		if (getDataEntriesTotalCount() == 0 || count == 0) {
			return null;
		}

		if (count == -1 || count > getDataEntriesTotalCount()) {
			fromIndex = 0;
			count = (int)getDataEntriesTotalCount();
		}

		if (fromIndex < 0 || fromIndex > getDataEntriesTotalCount() - 1) {
			fromIndex = 0;
		}

		KVDataEntry[] kvDataEntries = new KVDataEntry[count];
		byte[] value;
		String key;
		long ver;
		for (int i = 0; i < count; i++) {
			value = baseAccount.dataset.getValuesAtIndex(fromIndex);
			key = baseAccount.dataset.getKeyAtIndex(fromIndex);
			ver = baseAccount.dataset.getVersion(key);
			BytesValue decodeData = BinaryEncodingUtils.decode(value);
			kvDataEntries[i] = new KVDataObject(key, ver, PrimitiveType.valueOf(decodeData.getType().CODE), decodeData.getValue().toBytes());
			fromIndex++;
		}

		return kvDataEntries;
	}

	/**
	 * return the dataAccount's kv total count;
	 *
	 * @param
	 * @param
	 * @return return total count;
	 */
	public long getDataEntriesTotalCount() {
		return baseAccount.dataset.getDataCount();
	}

}