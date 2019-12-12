package com.jd.blockchain.storage.service.impl.redis;

public class JedisProperties {
	
	private String host;
	
	private int port = 6379;
	
	private int db = 0;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getDb() {
		return db;
	}

	public void setDb(int db) {
		this.db = db;
	}
	
}
