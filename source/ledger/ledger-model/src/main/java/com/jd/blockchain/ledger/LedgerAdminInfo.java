package com.jd.blockchain.ledger;

public interface LedgerAdminInfo {

	LedgerMetadata_V2 getMetadata();

	LedgerSettings getSettings();

	long getParticipantCount();

	ParticipantNode[] getParticipants();

	UserRolesSettings getUserRoles();

	RolePrivilegeSettings getRolePrivileges();

}