package com.jd.blockchain.storage.service.utils;


import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.storage.service.KVStorageService;

public class MemoryDBConn implements DbConnection {

		private MemoryKVStorage testStorage = new MemoryKVStorage();

		@Override
		public void close(){
		}

		@Override
		public KVStorageService getStorageService() {
			return testStorage;
		}

	}