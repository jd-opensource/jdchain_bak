package com.jd.blockchain.ledger.core;

import java.util.Set;

import com.jd.blockchain.ledger.LedgerPrivilege;
import com.jd.blockchain.ledger.RolePrivileges;

/**
 * 
 * {@link LedgerSecurityManager} implements the functions of security
 * management, including authentication, authorization, data confidentiality,
 * etc.
 * 
 * @author huanghaiquan
 *
 */
public class LedgerSecurityManager {
	
	public static final String ANONYMOUS_ROLE = "_ANONYMOUS";
	
	public static final String DEFAULT_ROLE = "_DEFAULT";
	
	
	public Set<String> getRoleNames(){
		throw new IllegalStateException("Not implemented!");
	}
	
	public RolePrivileges setRole(String role, LedgerPrivilege privilege) {
		throw new IllegalStateException("Not implemented!");
	}

	public RolePrivileges getRole(String role) {
		throw new IllegalStateException("Not implemented!");
	}
	
	
	
}
