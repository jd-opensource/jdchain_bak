package com.jd.blockchain.ledger.core;

public interface PrivilegeModelSetting {
	
	long getLatestVersion();
	
	PermissionService getPrivilege(long version);
	
}
