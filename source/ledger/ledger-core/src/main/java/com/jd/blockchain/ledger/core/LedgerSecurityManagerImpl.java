package com.jd.blockchain.ledger.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerSecurityException;
import com.jd.blockchain.ledger.ParticipantDataQuery;
import com.jd.blockchain.ledger.ParticipantDoesNotExistException;
import com.jd.blockchain.ledger.RolePrivilegeSettings;
import com.jd.blockchain.ledger.RolePrivileges;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.UserDoesNotExistException;
import com.jd.blockchain.ledger.UserRoles;
import com.jd.blockchain.ledger.UserAuthorizationSettings;
import com.jd.blockchain.utils.Bytes;

/**
 * 账本安全管理器；
 * 
 * @author huanghaiquan
 *
 */
public class LedgerSecurityManagerImpl implements LedgerSecurityManager {

	private RolePrivilegeSettings rolePrivilegeSettings;

	private UserAuthorizationSettings userRolesSettings;

	// 用户的权限配置
	private Map<Bytes, UserRolesPrivileges> userPrivilegesCache = new ConcurrentHashMap<>();

	private Map<Bytes, UserRoles> userRolesCache = new ConcurrentHashMap<>();
	private Map<String, RolePrivileges> rolesPrivilegeCache = new ConcurrentHashMap<>();

	private ParticipantDataQuery participantsQuery;
	private UserAccountQuery userAccountsQuery;

	public LedgerSecurityManagerImpl(RolePrivilegeSettings rolePrivilegeSettings, UserAuthorizationSettings userRolesSettings,
			ParticipantDataQuery participantsQuery, UserAccountQuery userAccountsQuery) {
		this.rolePrivilegeSettings = rolePrivilegeSettings;
		this.userRolesSettings = userRolesSettings;
		this.participantsQuery = participantsQuery;
		this.userAccountsQuery = userAccountsQuery;
	}

	@Override
	public SecurityPolicy createSecurityPolicy(Set<Bytes> endpoints, Set<Bytes> nodes) {
		Map<Bytes, UserRolesPrivileges> endpointPrivilegeMap = new HashMap<>();
		Map<Bytes, UserRolesPrivileges> nodePrivilegeMap = new HashMap<>();

		for (Bytes userAddress : endpoints) {
			UserRolesPrivileges userPrivileges = getUserRolesPrivilegs(userAddress);
			endpointPrivilegeMap.put(userAddress, userPrivileges);
		}

		for (Bytes userAddress : nodes) {
			UserRolesPrivileges userPrivileges = getUserRolesPrivilegs(userAddress);
			nodePrivilegeMap.put(userAddress, userPrivileges);
		}

		return new UserRolesSecurityPolicy(endpointPrivilegeMap, nodePrivilegeMap, participantsQuery, userAccountsQuery);
	}

	private UserRolesPrivileges getUserRolesPrivilegs(Bytes userAddress) {
		UserRolesPrivileges userPrivileges = userPrivilegesCache.get(userAddress);
		if (userPrivileges != null) {
			return userPrivileges;
		}

		UserRoles userRoles = null;

		List<RolePrivileges> privilegesList = new ArrayList<>();

		// 加载用户的角色列表；
		userRoles = userRolesCache.get(userAddress);
		if (userRoles == null) {
			userRoles = userRolesSettings.getUserRoles(userAddress);
			if (userRoles != null) {
				userRolesCache.put(userAddress, userRoles);
			}
		}

		// 计算用户的综合权限；
		if (userRoles != null) {
			String[] roles = userRoles.getRoles();
			RolePrivileges privilege = null;
			for (String role : roles) {
				// 先从缓存读取，如果没有再从原始数据源进行加载；
				privilege = rolesPrivilegeCache.get(role);
				if (privilege == null) {
					privilege = rolePrivilegeSettings.getRolePrivilege(role);
					if (privilege == null) {
						// 略过不存在的无效角色；
						continue;
					}
					rolesPrivilegeCache.put(role, privilege);
				}
				privilegesList.add(privilege);
			}
		}
		// 如果用户未被授权任何角色，则采用默认角色的权限；
		if (privilegesList.size() == 0) {
			RolePrivileges privilege = getDefaultRolePrivilege();
			privilegesList.add(privilege);
		}

		if (userRoles == null) {
			userPrivileges = new UserRolesPrivileges(userAddress, RolesPolicy.UNION, privilegesList);
		} else {
			userPrivileges = new UserRolesPrivileges(userAddress, userRoles.getPolicy(), privilegesList);
		}

		userPrivilegesCache.put(userAddress, userPrivileges);
		return userPrivileges;
	}

	private RolePrivileges getDefaultRolePrivilege() {
		RolePrivileges privileges = rolesPrivilegeCache.get(DEFAULT_ROLE);
		if (privileges == null) {
			privileges = rolePrivilegeSettings.getRolePrivilege(DEFAULT_ROLE);
			if (privileges == null) {
				throw new LedgerSecurityException(
						"This ledger is missing the default role-privilege settings for the users who don't have a role!");
			}
		}
		return privileges;
	}

	private class UserRolesSecurityPolicy implements SecurityPolicy {

		/**
		 * 终端用户的权限表；
		 */
		private Map<Bytes, UserRolesPrivileges> endpointPrivilegeMap = new HashMap<>();

		/**
		 * 节点参与方的权限表；
		 */
		private Map<Bytes, UserRolesPrivileges> nodePrivilegeMap = new HashMap<>();

		private ParticipantDataQuery participantsQuery;

		private UserAccountQuery userAccountsQuery;

		public UserRolesSecurityPolicy(Map<Bytes, UserRolesPrivileges> endpointPrivilegeMap,
				Map<Bytes, UserRolesPrivileges> nodePrivilegeMap, ParticipantDataQuery participantsQuery,
				UserAccountQuery userAccountsQuery) {
			this.endpointPrivilegeMap = endpointPrivilegeMap;
			this.nodePrivilegeMap = nodePrivilegeMap;
			this.participantsQuery = participantsQuery;
			this.userAccountsQuery = userAccountsQuery;
		}

		@Override
		public boolean isEndpointEnable(LedgerPermission permission, MultiIDsPolicy midPolicy) {
			if (MultiIDsPolicy.AT_LEAST_ONE == midPolicy) {
				// 至少一个；
				for (UserRolesPrivileges p : endpointPrivilegeMap.values()) {
					if (p.getLedgerPrivileges().isEnable(permission)) {
						return true;
					}
				}
				return false;
			} else if (MultiIDsPolicy.ALL == midPolicy) {
				// 全部；
				for (UserRolesPrivileges p : endpointPrivilegeMap.values()) {
					if (!p.getLedgerPrivileges().isEnable(permission)) {
						return false;
					}
				}
				return true;
			} else {
				throw new IllegalArgumentException("Unsupported MultiIdsPolicy[" + midPolicy + "]!");
			}
		}

		@Override
		public boolean isEndpointEnable(TransactionPermission permission, MultiIDsPolicy midPolicy) {
			if (MultiIDsPolicy.AT_LEAST_ONE == midPolicy) {
				// 至少一个；
				for (UserRolesPrivileges p : endpointPrivilegeMap.values()) {
					if (p.getTransactionPrivileges().isEnable(permission)) {
						return true;
					}
				}
				return false;
			} else if (MultiIDsPolicy.ALL == midPolicy) {
				// 全部；
				for (UserRolesPrivileges p : endpointPrivilegeMap.values()) {
					if (!p.getTransactionPrivileges().isEnable(permission)) {
						return false;
					}
				}
				return true;
			} else {
				throw new IllegalArgumentException("Unsupported MultiIdsPolicy[" + midPolicy + "]!");
			}
		}

		@Override
		public boolean isNodeEnable(LedgerPermission permission, MultiIDsPolicy midPolicy) {
			if (MultiIDsPolicy.AT_LEAST_ONE == midPolicy) {
				// 至少一个；
				for (UserRolesPrivileges p : nodePrivilegeMap.values()) {
					if (p.getLedgerPrivileges().isEnable(permission)) {
						return true;
					}
				}
				return false;
			} else if (MultiIDsPolicy.ALL == midPolicy) {
				// 全部；
				for (UserRolesPrivileges p : nodePrivilegeMap.values()) {
					if (!p.getLedgerPrivileges().isEnable(permission)) {
						return false;
					}
				}
				return true;
			} else {
				throw new IllegalArgumentException("Unsupported MultiIdsPolicy[" + midPolicy + "]!");
			}
		}

		@Override
		public boolean isNodeEnable(TransactionPermission permission, MultiIDsPolicy midPolicy) {
			if (MultiIDsPolicy.AT_LEAST_ONE == midPolicy) {
				// 至少一个；
				for (UserRolesPrivileges p : nodePrivilegeMap.values()) {
					if (p.getTransactionPrivileges().isEnable(permission)) {
						return true;
					}
				}
				return false;
			} else if (MultiIDsPolicy.ALL == midPolicy) {
				// 全部；
				for (UserRolesPrivileges p : nodePrivilegeMap.values()) {
					if (!p.getTransactionPrivileges().isEnable(permission)) {
						return false;
					}
				}
				return true;
			} else {
				throw new IllegalArgumentException("Unsupported MultiIdsPolicy[" + midPolicy + "]!");
			}
		}

		@Override
		public void checkEndpointPermission(LedgerPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
			if (!isEndpointEnable(permission, midPolicy)) {
				throw new LedgerSecurityException(String.format(
						"The security policy [Permission=%s, Policy=%s] for endpoints rejected the current operation!",
						permission, midPolicy));
			}
		}

		@Override
		public void checkEndpointPermission(TransactionPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
			if (!isEndpointEnable(permission, midPolicy)) {
				throw new LedgerSecurityException(String.format(
						"The security policy [Permission=%s, Policy=%s] for endpoints rejected the current operation!",
						permission, midPolicy));
			}
		}

		@Override
		public void checkNodePermission(LedgerPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
			if (!isNodeEnable(permission, midPolicy)) {
				throw new LedgerSecurityException(String.format(
						"The security policy [Permission=%s, Policy=%s] for nodes rejected the current operation!",
						permission, midPolicy));
			}
		}

		@Override
		public void checkNodePermission(TransactionPermission permission, MultiIDsPolicy midPolicy)
				throws LedgerSecurityException {
			if (!isNodeEnable(permission, midPolicy)) {
				throw new LedgerSecurityException(String.format(
						"The security policy [Permission=%s, Policy=%s] for nodes rejected the current operation!",
						permission, midPolicy));
			}
		}

		@Override
		public Set<Bytes> getEndpoints() {
			return endpointPrivilegeMap.keySet();
		}

		@Override
		public Set<Bytes> getNodes() {
			return nodePrivilegeMap.keySet();
		}

		@Override
		public boolean isEndpointValid(MultiIDsPolicy midPolicy) {
			if (MultiIDsPolicy.AT_LEAST_ONE == midPolicy) {
				// 至少一个；
				for (Bytes address : getEndpoints()) {
					if (userAccountsQuery.contains(address)) {
						return true;
					}
				}
				return false;
			} else if (MultiIDsPolicy.ALL == midPolicy) {
				// 全部；
				for (Bytes address : getEndpoints()) {
					if (!userAccountsQuery.contains(address)) {
						return false;
					}
				}
				return true;
			} else {
				throw new IllegalArgumentException("Unsupported MultiIdsPolicy[" + midPolicy + "]!");
			}
		}

		@Override
		public boolean isNodeValid(MultiIDsPolicy midPolicy) {
			if (MultiIDsPolicy.AT_LEAST_ONE == midPolicy) {
				// 至少一个；
				for (Bytes address : getNodes()) {
					if (participantsQuery.contains(address)) {
						return true;
					}
				}
				return false;
			} else if (MultiIDsPolicy.ALL == midPolicy) {
				// 全部；
				for (Bytes address : getNodes()) {
					if (!participantsQuery.contains(address)) {
						return false;
					}
				}
				return true;
			} else {
				throw new IllegalArgumentException("Unsupported MultiIdsPolicy[" + midPolicy + "]!");
			}
		}

		@Override
		public void checkEndpointValidity(MultiIDsPolicy midPolicy) throws LedgerSecurityException {
			if (MultiIDsPolicy.AT_LEAST_ONE == midPolicy) {
				// 至少一个；
				for (Bytes address : getEndpoints()) {
					if (userAccountsQuery.contains(address)) {
						return;
					}
				}
				throw new UserDoesNotExistException("All endpoint signers were not registered!");
			} else if (MultiIDsPolicy.ALL == midPolicy) {
				// 全部；
				for (Bytes address : getEndpoints()) {
					if (!userAccountsQuery.contains(address)) {
						throw new UserDoesNotExistException("The endpoint signer[" + address + "] was not registered!");
					}
				}
				return;
			} else {
				throw new IllegalArgumentException("Unsupported MultiIdsPolicy[" + midPolicy + "]!");
			}
		}

		@Override
		public void checkNodeValidity(MultiIDsPolicy midPolicy) throws LedgerSecurityException {
			if (MultiIDsPolicy.AT_LEAST_ONE == midPolicy) {
				// 至少一个；
				for (Bytes address : getNodes()) {
					if (participantsQuery.contains(address)) {
						return;
					}
				}
				throw new ParticipantDoesNotExistException("All node signers were not registered as participant!");
			} else if (MultiIDsPolicy.ALL == midPolicy) {
				// 全部；
				for (Bytes address : getNodes()) {
					if (!participantsQuery.contains(address)) {
						throw new ParticipantDoesNotExistException(
								"The node signer[" + address + "] was not registered as participant!");
					}
				}
			} else {
				throw new IllegalArgumentException("Unsupported MultiIdsPolicy[" + midPolicy + "]!");
			}
		}

	}

}
