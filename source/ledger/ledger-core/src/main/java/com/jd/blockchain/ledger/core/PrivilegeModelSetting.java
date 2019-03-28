package com.jd.blockchain.ledger.core;

public interface PrivilegeModelSetting {
	
	long getLatestVersion();
	
	Privilege getPrivilege(long version);
	
}
