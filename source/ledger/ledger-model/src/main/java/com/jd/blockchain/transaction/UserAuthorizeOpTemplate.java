package com.jd.blockchain.transaction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.SecurityUtils;
import com.jd.blockchain.ledger.UserAuthorizeOperation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.utils.ArrayUtils;
import com.jd.blockchain.utils.Bytes;

public class UserAuthorizeOpTemplate implements UserAuthorizer, UserAuthorizeOperation {

	static {
		DataContractRegistry.register(UserRegisterOperation.class);
	}

	private Map<Bytes, UserRolesAuthorization> userAuthMap = Collections
			.synchronizedMap(new LinkedHashMap<Bytes, UserRolesAuthorization>());

	public UserAuthorizeOpTemplate() {
	}

	public UserAuthorizeOpTemplate(BlockchainIdentity userID) {
	}

	@Override
	public UserRolesAuthorization[] getUserRolesAuthorizations() {
		return ArrayUtils.toArray(userAuthMap.values(), UserRolesAuthorization.class);
	}

	@Override
	public UserAuthorizeOperation getOperation() {
		return this;
	}

	@Override
	public UserRolesAuthorizer forUser(Bytes userAddress) {
		UserRolesAuthorization userRolesAuth = userAuthMap.get(userAddress);
		if (userRolesAuth == null) {
			userRolesAuth = new UserRolesAuthorization(userAddress);
			userAuthMap.put(userAddress, userRolesAuth);
		}
		return userRolesAuth;
	}

	@Override
	public UserRolesAuthorizer forUser(BlockchainIdentity userId) {
		return forUser(userId.getAddress());
	}

	private class UserRolesAuthorization implements UserRolesAuthorizer, UserRolesEntry {

		private Bytes userAddress;

		private RolesPolicy policy = RolesPolicy.UNION;

		private Set<String> authRoles = new LinkedHashSet<String>();
		private Set<String> unauthRoles = new LinkedHashSet<String>();

		private UserRolesAuthorization(Bytes userAddress) {
			this.userAddress = userAddress;
		}

		@Override
		public Bytes getUserAddress() {
			return userAddress;
		}

		@Override
		public RolesPolicy getPolicy() {
			return policy;
		}

		@Override
		public String[] getAuthorizedRoles() {
			return ArrayUtils.toArray(authRoles, String.class);
		}

		@Override
		public String[] getUnauthorizedRoles() {
			return ArrayUtils.toArray(unauthRoles, String.class);
		}

		@Override
		public UserRolesAuthorizer setPolicy(RolesPolicy policy) {
			this.policy = policy;
			return this;
		}

		@Override
		public UserRolesAuthorizer authorize(String... roles) {
			String roleName;
			for (String r : roles) {
				roleName = SecurityUtils.formatRoleName(r);
				authRoles.add(roleName);
				unauthRoles.remove(roleName);
			}

			return this;
		}

		@Override
		public UserRolesAuthorizer unauthorize(String... roles) {
			String roleName;
			for (String r : roles) {
				roleName = SecurityUtils.formatRoleName(r);
				unauthRoles.add(roleName);
				authRoles.remove(roleName);
			}

			return this;
		}

		@Override
		public UserRolesAuthorizer forUser(BlockchainIdentity userId) {
			return UserAuthorizeOpTemplate.this.forUser(userId);
		}

		@Override
		public UserRolesAuthorizer forUser(Bytes userAddress) {
			return UserAuthorizeOpTemplate.this.forUser(userAddress);
		}
	}
}
