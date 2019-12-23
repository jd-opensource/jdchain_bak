package com.jd.blockchain.storage.service;

import java.io.Closeable;

public interface DbConnection extends Closeable {
	
	KVStorageService getStorageService();
	
}
