package com.jd.blockchain.storage.service;

import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;

/**
 * Versioning Key-Value Storage
 * <p>
 * 
 * One key in this storage has a version attribute, which starts from zero and
 * increased by 1 on it's value being updated.
 * 
 * <br>
 * The writing of a key must be specified a explict integer of it's latest
 * version at the executing moment.
 * 
 * 
 * @author huanghaiquan
 *
 */
public interface VersioningKVStorage extends BatchStorageService {

	/**
	 * Return the latest version entry associated the specified key;
	 * 
	 * If the key doesn't exist, then return -1;
	 * 
	 * @param key
	 * @return
	 */
	long getVersion(Bytes key);
	
	/**
	 * Return the specified verson's entry;<br>
	 * 
	 * It will return the latest one if the version is -1; <br>
	 * 
	 * It will return null if the key or version not exist.
	 * 
	 * @param key
	 * @param version
	 * @return
	 */
	DataEntry<Bytes, byte[]> getEntry(Bytes key, long version);
	
	/**
	 * Return the specified verson's value; <br>
	 * 
	 * If the specified version of key doesn't exist, then return null;<br>
	 * 
	 * If the version is specified to -1, then return the latest version's
	 * value;<br>
	 * 
	 * @param key
	 * @param version
	 * @return
	 */
	byte[] get(Bytes key, long version);
	
	/**
	 * Update the value of the key;<br>
	 * 
	 * If key exist, and the specified version equals to it's latest version, then the value will be
	 * updated and version will be increased by 1;<br>
	 * If key not exist, and the specified version is -1, then the value will be
	 * created and initialized it's version by 0; <br>
	 * 
	 * @param key
	 *            the key;
	 * @param value
	 *            the new value to update if expected version match the actual
	 *            version;
	 * @param version
	 *            the latest version expected;
	 * @return The latest version entry after setting. <br>
	 *         If the version checking fail, or concurrent confliction occur, then
	 *         return -1 as indication. <br>
	 */
	long set(Bytes key, byte[] value, long version);

}