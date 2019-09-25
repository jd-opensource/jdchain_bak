package com.jd.blockchain.utils;

public abstract class RegionMap<K, V> implements VersioningMap<K, V> {

	private K region;

	private VersioningMap<K, V> dataMap;

	public RegionMap(K region, VersioningMap<K, V> dataMap) {
		this.region = region;
		this.dataMap = dataMap;
	}

	@Override
	public long setValue(K key, V value, long version) {
		K dataKey = concatKey(region, key);
		return dataMap.setValue(dataKey, value, version);
	}

	@Override
	public V getValue(K key, long version) {
		K dataKey = concatKey(region, key);
		return dataMap.getValue(dataKey, version);
	}

	@Override
	public V getValue(K key) {
		K dataKey = concatKey(region, key);
		return dataMap.getValue(dataKey);
	}

	@Override
	public long getVersion(K key) {
		K dataKey = concatKey(region, key);
		return dataMap.getVersion(dataKey);
	}

	@Override
	public VersioningKVEntry<K, V> getDataEntry(K key) {
		K dataKey = concatKey(region, key);
		VersioningKVEntry<K, V> entry = dataMap.getDataEntry(dataKey);
		return new KVEntryWrapper<K, V>(key, entry);
	}

	@Override
	public VersioningKVEntry<K, V> getDataEntry(K key, long version) {
		K dataKey = concatKey(region, key);
		VersioningKVEntry<K, V> entry = dataMap.getDataEntry(dataKey, version);
		return new KVEntryWrapper<K, V>(key, entry);
	}

	/**
	 * 以指定的前缀组成新的key；
	 * 
	 * @param prefix
	 * @param key
	 * @return
	 */
	protected abstract K concatKey(K prefix, K key);

	public static <V> VersioningMap<Bytes, V> newRegion(Bytes region, VersioningMap<Bytes, V> dataMap) {
		return new BytesKeyRegionMap<V>(region, dataMap);
	}
	
	public static <V> VersioningMap<String, V> newRegion(String region, VersioningMap<String, V> dataMap) {
		return new StringKeyRegionMap<V>(region, dataMap);
	}

	private static class BytesKeyRegionMap<V> extends RegionMap<Bytes, V> {

		public BytesKeyRegionMap(Bytes region, VersioningMap<Bytes, V> dataMap) {
			super(region, dataMap);
		}

		@Override
		protected Bytes concatKey(Bytes prefix, Bytes key) {
			return prefix.concat(key);
		}

	}

	private static class StringKeyRegionMap<V> extends RegionMap<String, V> {

		public StringKeyRegionMap(String region, VersioningMap<String, V> dataMap) {
			super(region, dataMap);
		}

		@Override
		protected String concatKey(String prefix, String key) {
			return prefix + key;
		}

	}

	private static class KVEntryWrapper<K, V> implements VersioningKVEntry<K, V> {

		private K key;

		private VersioningKVEntry<K, V> entry;

		public KVEntryWrapper(K key, VersioningKVEntry<K, V> entry) {
			this.key = key;
			this.entry = entry;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public long getVersion() {
			return entry.getVersion();
		}

		@Override
		public V getValue() {
			return entry.getValue();
		}

	}
}
