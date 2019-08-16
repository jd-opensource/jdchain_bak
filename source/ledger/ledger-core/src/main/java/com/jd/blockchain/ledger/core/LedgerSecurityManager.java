package com.jd.blockchain.ledger.core;

import java.util.Set;

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
	
	public RolePrivilegeAuthorization setRole(String role, LedgerPrivilege privilege) {
		throw new IllegalStateException("Not implemented!");
	}

	public RolePrivilegeAuthorization getRole(String role) {
		throw new IllegalStateException("Not implemented!");
	}
	
	
	
}
