package com.jd.blockchain.transaction;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.utils.Bytes;

public class DataAccountKVSetOpTemplate implements DataAccountKVSetOperation {
	static {
		DataContractRegistry.register(DataAccountKVSetOperation.class);
	}

	private Bytes accountAddress;

	private Map<String, KVWriteEntry> kvset = new LinkedHashMap<>();

	public DataAccountKVSetOpTemplate() {
	}

	public DataAccountKVSetOpTemplate(Bytes accountAddress) {
		this.accountAddress = accountAddress;
	}

	public DataAccountKVSetOpTemplate(Bytes accountAddress, Map<String, KVWriteEntry> kvset) {
		this.accountAddress = accountAddress;
		this.kvset = kvset;
	}

	@Override
	public Bytes getAccountAddress() {
		return accountAddress;
	}

	@Override
	public KVWriteEntry[] getWriteSet() {
		return kvset.values().toArray(new KVWriteEntry[kvset.size()]);
	}

	public void setWriteSet(Object[] kvEntries) {
		for (Object object : kvEntries) {
			KVWriteEntry kvEntry = (KVWriteEntry) object;
			set(kvEntry.getKey(), kvEntry.getValue(), kvEntry.getExpectedVersion());
		}
		return;
	}

	public void set(String key, BytesValue value, long expVersion) {
		if (kvset.containsKey(key)) {
			throw new IllegalArgumentException("Cann't set the same key repeatedly!");
		}
		KVData kvdata = new KVData(key, value, expVersion);
		kvset.put(key, kvdata);
	}

	public void set(KVData kvData) {
		if (kvset.containsKey(kvData.getKey())) {
			throw new IllegalArgumentException("Cann't set the same key repeatedly!");
		}
		kvset.put(kvData.getKey(), kvData);
	}

}
