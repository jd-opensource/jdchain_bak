package com.jd.blockchain.storage.service.utils;

import com.jd.blockchain.storage.service.VersioningKVEntry;
import com.jd.blockchain.utils.Bytes;

public class VersioningKVData implements VersioningKVEntry {

		private Bytes key;

		private long version;

		private byte[] value;

		public VersioningKVData(Bytes key, long version, byte[] value) {
			this.key = key;
			this.version = version;
			this.value = value;
		}

		@Override
		public Bytes getKey() {
			return key;
		}

		@Override
		public long getVersion() {
			return version;
		}

		@Override
		public byte[] getValue() {
			return value;
		}

	}