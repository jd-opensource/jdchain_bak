//package com.jd.blockchain.storage.service.utils;
//
//
//import com.jd.blockchain.storage.service.DbConnection;
//import com.jd.blockchain.storage.service.DbConnectionFactory;
//import com.jd.blockchain.storage.service.KVStorageService;
//
//public class MemoryBasedDb implements DbConnectionFactory, DbConnection {
//
//	private MemoryKVStorage testStorage = new MemoryKVStorage();
//
//	@Override
//	public boolean support(String scheme) {
//		return true;
//	}
//
//	@Override
//	public DbConnection connect(String dbConnectionString) {
//		return new MemoryBasedDb();
//	}
//
//	@Override
//	public DbConnection connect(String dbConnectionString, String password) {
//		return new MemoryBasedDb();
//	}
//
//	@Override
//	public void close(){
//	}
//
//	@Override
//	public KVStorageService getStorageService() {
//		return testStorage;
//	}
//}