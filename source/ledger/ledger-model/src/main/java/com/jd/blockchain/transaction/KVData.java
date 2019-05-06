package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountKVSetOperation.KVWriteEntry;

public class KVData implements KVWriteEntry {

	private String key;

	private BytesValue value;

	private long expectedVersion;

	public KVData(String key, BytesValue value, long expectedVersion) {
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