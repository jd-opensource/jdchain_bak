package com.jd.blockchain.storage.service.utils;

import java.util.HashSet;
import java.util.Set;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.KVStorageService;
import com.jd.blockchain.storage.service.VersioningKVEntry;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesMap;

public class MemoryKVStorage implements ExPolicyKVStorage, VersioningKVStorage, KVStorageService, BytesMap<Bytes> {

//	private Set<String> keys = new HashSet<>();
	
//	private Set<String> keys = Collections.synchronizedSet(new HashSet<>());

//	private Map<String, Object> storageMap = new ConcurrentHashMap<>();
	private ExistancePolicyKVStorageMap exStorage = new ExistancePolicyKVStorageMap();
	private VersioningKVStorageMap verStorage = new VersioningKVStorageMap();

	@Override
	public long getVersion(Bytes key) {
		return verStorage.getVersion(key);
	}

	@Override
	public VersioningKVEntry getEntry(Bytes key, long version) {
		return verStorage.getEntry(key, version);
	}

	@Override
	public byte[] get(Bytes key, long version) {
		return verStorage.get(key, version);
	}
	
	@Override
	public long set(Bytes key, byte[] value, long version) {
		return verStorage.set(key, value, version);
//		if (v > -1) {
//			keys.add(key);
//		}
//		return v;
	}

	@Override
	public byte[] get(Bytes key) {
		return exStorage.get(key);
	}
	
	@Override
	public boolean exist(Bytes key) {
		return exStorage.exist(key);
	}

	@Override
	public boolean set(Bytes key, byte[] value, ExPolicy ex) {
		return exStorage.set(key, value, ex);
//		if (ok) {
//			keys.add(key);
//		}
//		return ok;
	}

	@Override
	public Set<Bytes> keySet() {
		return getStorageKeySet();
	}

	@Override
	public byte[] getValue(Bytes key) {
		byte[] v = exStorage.getValue(key);
		if (v == null) {
			v = verStorage.getValue(key);
		}
		return v;
	}

	public Set<Bytes> getStorageKeySet() {
		HashSet<Bytes> keySet = new HashSet<>(exStorage.keySet());
		keySet.addAll(verStorage.keySet());
		return keySet;
//		return storageMap.keySet();
	}

	public int getStorageCount() {
		return exStorage.getCount() + verStorage.getCount();
//		return storageMap.size();
	}

//	public void printStoragedKeys() {
//		String[] keys = StringUtils.toStringArray(getStorageKeySet());
//		StringUtils.sortStringArray(keys);
//		for (String k : keys) {
//			System.out.println(k);
//		}
//	}

	@Override
	public ExPolicyKVStorage getExPolicyKVStorage() {
		return this;
	}

	@Override
	public VersioningKVStorage getVersioningKVStorage() {
		return this;
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
