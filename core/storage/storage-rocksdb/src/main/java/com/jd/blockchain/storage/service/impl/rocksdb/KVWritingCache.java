package com.jd.blockchain.storage.service.impl.rocksdb;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.rocksdb.RocksDB;

import com.jd.blockchain.utils.io.BytesUtils;

public class KVWritingCache implements Closeable {

	private Map<String, byte[]> cache = new ConcurrentHashMap<>();

	private LinkedBlockingQueue<String> tasks = new LinkedBlockingQueue<>(1000000);
	
	private volatile boolean running;

	private RocksDB db;
	
	private Thread thrd;

	public KVWritingCache(RocksDB db) {
		this.db = db;
		thrd = new Thread(new Runnable() {
			@Override
			public void run() {
				running = true;
				doWritingTask();
			}
		}, "KVWritingCache-Thread");
		thrd.setContextClassLoader(Thread.currentThread().getContextClassLoader());
		thrd.start();
	}

	public byte[] get(String key) {
		return cache.get(key);
	}

	public void set(String key, byte[] value) {
		cache.put(key, value);
		tasks.add(key);
	}

	private void doWritingTask() {
		while (running) {
			dbWrite();
		}
	}

	private void dbWrite() {
		try {
			String key = tasks.take();
			byte[] value = cache.remove(key);
			byte[] keyBytes = BytesUtils.toBytes(key);
			db.put(keyBytes, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		running = false;
		thrd.interrupt();
	}
}
