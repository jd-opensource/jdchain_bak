package com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.DConstructor;
import com.jd.blockchain.binaryproto.FieldSetter;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountKVSetOperation.KVWriteEntry;

public class KVData implements KVWriteEntry {
		
		private String key;
		
		private BytesValue value;
		
		private long expectedVersion;


		@DConstructor(name="KVData")
		public KVData(@FieldSetter(name="getKey", type="String") String key, @FieldSetter(name="getValue", type="BytesValue") BytesValue value, @FieldSetter(name="getExpectedVersion", type="long")long expectedVersion) {
			this.key = key;
			this.value = value;
			this.expectedVersion = expectedVersion;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public BytesValue getValue() {
			return value;
		}

		@Override
		public long getExpectedVersion() {
			return expectedVersion;
		}
		
	}