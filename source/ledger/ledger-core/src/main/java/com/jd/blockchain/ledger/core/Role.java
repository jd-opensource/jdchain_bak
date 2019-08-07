package com.jd.blockchain.ledger.core;

public class Role {
	
	private String name;
	
	private long version;
	
	private LedgerPrivilege privilege;
	
	

	public String getName() {
		return name;
	}

	public long getVersion() {
		return version;
	}

	public LedgerPrivilege getPrivilege() {
		return privilege;
	}
	
	
	
}
