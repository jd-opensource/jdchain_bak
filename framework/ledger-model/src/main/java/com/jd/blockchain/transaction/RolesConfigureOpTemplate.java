package com.jd.blockchain.transaction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.RolesConfigureOperation;
import com.jd.blockchain.ledger.SecurityUtils;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.utils.ArrayUtils;

public class RolesConfigureOpTemplate implements RolesConfigurer, RolesConfigureOperation {

	static {
		DataContractRegistry.register(UserRegisterOperation.class);
		DataContractRegistry.register(RolesConfigureOperation.class);
		DataContractRegistry.register(RolePrivilegeEntry.class);
	}

	private Map<String, RolePrivilegeConfig> rolesMap = Collections
			.synchronizedMap(new LinkedHashMap<String, RolePrivilegeConfig>());

	public RolesConfigureOpTemplate() {
	}

	boolean isEmpty() {
		return rolesMap.isEmpty();
	}

	@Override
	public RolePrivilegeEntry[] getRoles() {
		return rolesMap.values().toArray(new RolePrivilegeEntry[rolesMap.size()]);
	}

	@Override
	public RolesConfigureOperation getOperation() {
		return this;
	}

	@Override
	public RolePrivilegeConfigurer configure(String roleName) {
		roleName = SecurityUtils.formatRoleName(roleName);

		RolePrivilegeConfig roleConfig = rolesMap.get(roleName);
		if (roleConfig == null) {
			roleConfig = new RolePrivilegeConfig(roleName);
			rolesMap.put(roleName, roleConfig);
		}
		return roleConfig;
	}

	private class RolePrivilegeConfig implements RolePrivilegeConfigurer, RolePrivilegeEntry {

		private String roleName;

		private Set<LedgerPermission> enableLedgerPermissions = new LinkedHashSet<LedgerPermission>();
		private Set<LedgerPermission> disableLedgerPermissions = new LinkedHashSet<LedgerPermission>();

		private Set<TransactionPermission> enableTxPermissions = new LinkedHashSet<TransactionPermission>();
		private Set<TransactionPermission> disableTxPermissions = new LinkedHashSet<TransactionPermission>();

		private RolePrivilegeConfig(String roleName) {
			this.roleName = roleName;
		}

		@Override
		public String getRoleName() {
			return roleName;
		}

		@Override
		public LedgerPermission[] getEnableLedgerPermissions() {
			return ArrayUtils.toArray(enableLedgerPermissions, LedgerPermission.class);
		}

		@Override
		public LedgerPermission[] getDisableLedgerPermissions() {
			return ArrayUtils.toArray(disableLedgerPermissions, LedgerPermission.class);
		}

		@Override
		public TransactionPermission[] getEnableTransactionPermissions() {
			return ArrayUtils.toArray(enableTxPermissions, TransactionPermission.class);
		}

		@Override
		public TransactionPermission[] getDisableTransactionPermissions() {
			return ArrayUtils.toArray(disableTxPermissions, TransactionPermission.class);
		}

		@Override
		public RolePrivilegeConfigurer enable(LedgerPermission... permissions) {
			List<LedgerPermission> permissionList = ArrayUtils.asList(permissions);
			enableLedgerPermissions.addAll(permissionList);
			disableLedgerPermissions.removeAll(permissionList);

			return this;
		}

		@Override
		public RolePrivilegeConfigurer disable(LedgerPermission... permissions) {
			List<LedgerPermission> permissionList = ArrayUtils.asList(permissions);
			disableLedgerPermissions.addAll(permissionList);
			enableLedgerPermissions.removeAll(permissionList);

			return this;
		}

		@Override
		public RolePrivilegeConfigurer enable(TransactionPermission... permissions) {
			List<TransactionPermission> permissionList = ArrayUtils.asList(permissions);
			enableTxPermissions.addAll(permissionList);
			disableTxPermissions.removeAll(permissionList);

			return this;
		}

		@Override
		public RolePrivilegeConfigurer disable(TransactionPermission... permissions) {
			List<TransactionPermission> permissionList = ArrayUtils.asList(permissions);
			disableTxPermissions.addAll(permissionList);
			enableTxPermissions.removeAll(permissionList);

			return this;
		}

		@Override
		public RolePrivilegeConfigurer configure(String roleName) {
			return RolesConfigureOpTemplate.this.configure(roleName);
		}

	}
}
