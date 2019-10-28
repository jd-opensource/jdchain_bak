package com.jd.blockchain.storage.service.utils;

import com.jd.blockchain.utils.DataEntry;

public class VersioningKVData<K, V> implements DataEntry<K, V> {

		private K key;

		private long version;

		private V value;

		public VersioningKVData(K key, long version, V value) {
			this.key = key;
			this.version = version;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public long getVersion() {
			return version;
		}

		@Override
		public V getValue() {
			return value;
		}

	}