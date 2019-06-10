package com.jd.blockchain.sdk;

import com.jd.blockchain.ledger.PermissionType;

/**
 * 权限设置；<br>
 * 
 * 
 * 
 * @author huanghaiquan
 *
 */
public interface PrivilegeSetting {
	
	String[] getSigners();
	
	long getMask(String address);
	
	boolean isEnable(String address, PermissionType privilege);
	
}
