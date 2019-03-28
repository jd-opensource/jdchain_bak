package com.jd.blockchain.storage.service;

public interface KVStorageService {
	
	ExPolicyKVStorage getExPolicyKVStorage();
	
	VersioningKVStorage getVersioningKVStorage();
	
}
