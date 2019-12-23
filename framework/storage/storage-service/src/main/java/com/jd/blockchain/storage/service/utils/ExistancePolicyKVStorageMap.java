package com.jd.blockchain.storage.service.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesMap;

/**
 * 基于内存的 {@link ExPolicyKVStorage} 实现；
 * 
 * @author huanghaiquan
 *
 */
public class ExistancePolicyKVStorageMap implements ExPolicyKVStorage, BytesMap<Bytes> {
	// 要维持键的写入顺序，并且最终以相同的顺序输出；
	private Map<Bytes, Object> storage;
	
	private Object mutex = new Object();

	public ExistancePolicyKVStorageMap() {
		storage = new ConcurrentHashMap<Bytes, Object>();
	}

	public ExistancePolicyKVStorageMap(Map<Bytes, Object> external) {
		storage = external;
	}

	@Override
	public byte[] get(Bytes key) {
		return (byte[]) storage.get(key);
	}

	@Override
	public boolean exist(Bytes key) {
		return storage.containsKey(key);
	}

	@Override
	public synchronized boolean set(Bytes key, byte[] value, ExPolicy ex) {
		switch (ex) {
		case EXISTING:
			return setEx(key, value);
		case NOT_EXISTING:
			return setNx(key, value);
		default:
			throw new IllegalArgumentException("Unsupported ExistancePolicy[" + ex + "]!");
		}
	}

	private boolean setNx(Bytes key, byte[] value) {
		if (storage.containsKey(key)) {
			return false;
		}
		synchronized (mutex) {
			if (storage.containsKey(key)) {
				return false;
			}
			storage.put(key, value);
			return true;
		}
	}

	private boolean setEx(Bytes key, byte[] value) {
		if (!storage.containsKey(key)) {
			return false;
		}
		storage.put(key, value);
		return true;
	}

	@Override
	public Set<Bytes> keySet() {
		return storage.keySet();
	}

	public int getCount() {
		return storage.size();
	}

	@Override
	public byte[] getValue(Bytes key) {
		return get(key);
	}

	@Override
	public void batchBegin() {
		// un support!!!
	}

	@Override
	public void batchCommit() {
		// un support!!!
	}
}