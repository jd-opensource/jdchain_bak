package com.jd.blockchain.utils;

/**
 * Key-Value data set;
 * 
 * @author huanghaiquan
 *
 * @param <K>
 * @param <V>
 */
public interface Dataset<K, V> {

	/**
	 * Total count of data entries;
	 * 
	 * @return
	 */
	long getDataCount();

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
	long setValue(K key, V value, long version);

	/**
	 * Return the specified version's value;<br>
	 * 
	 * If the key with the specified version doesn't exist, then return null;<br>
	 * If the version is specified to -1, then return the latest version's value;
	 * 
	 * @param key
	 * @param version
	 */
	V getValue(K key, long version);

	/**
	 * Return the value of the latest version;
	 * 
	 * @param key
	 * @return return null if not exist;
	 */
	V getValue(K key);

	/**
	 * Return the latest version number of the specified key;
	 * 
	 * @param key
	 * @return The version number of the specified key; If the key doesn't exist,
	 *         then return -1;
	 */
	long getVersion(K key);

	/**
	 * Return the data entry with the specified key;
	 * 
	 * @param key
	 * @return Null if the key doesn't exist!
	 */
	DataEntry<K, V> getDataEntry(K key);

	/**
	 * Return the data entry with the specified key and version;
	 * 
	 * @param key
	 * @param version
	 * @return Null if the key doesn't exist!
	 */
	DataEntry<K, V> getDataEntry(K key, long version);

	/**
	 * Ascending iterator；
	 * 
	 * @return
	 */
	DataIterator<K, V> iterator();

	/**
	 * Descending iterator；
	 * 
	 * @return
	 */
	DataIterator<K, V> iteratorDesc();

}