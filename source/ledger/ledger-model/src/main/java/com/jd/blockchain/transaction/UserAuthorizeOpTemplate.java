package com.jd.blockchain.transaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
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
		DataContractRegistry.register(UserAuthorizeOperation.class);
		DataContractRegistry.register(UserRolesEntry.class);
	}

	private Set<AuthorizationDataEntry> userAuthMap = Collections
			.synchronizedSet(new LinkedHashSet<AuthorizationDataEntry>());

	public UserAuthorizeOpTemplate() {
	}

	public UserAuthorizeOpTemplate(BlockchainIdentity userID) {
	}

	@Override
	public AuthorizationDataEntry[] getUserRolesAuthorizations() {
		return ArrayUtils.toArray(userAuthMap, AuthorizationDataEntry.class);
	}

	@Override
	public UserAuthorizeOperation getOperation() {
		return this;
	}

	@Override
	public UserRolesAuthorizer forUser(Bytes... userAddresses) {
		AuthorizationDataEntry userRolesAuth = new AuthorizationDataEntry(userAddresses);
		userAuthMap.add(userRolesAuth);
		return userRolesAuth;
	}

	@Override
	public UserRolesAuthorizer forUser(BlockchainIdentity... userIds) {
		Bytes[] addresses = Arrays.stream(userIds).map(p -> p.getAddress()).toArray(Bytes[]::new);
		return forUser(addresses);
	}

	private class AuthorizationDataEntry implements UserRolesAuthorizer, UserRolesEntry {

		private Bytes[] userAddress;

		private RolesPolicy policy = RolesPolicy.UNION;

		private Set<String> authRoles = new LinkedHashSet<String>();
		private Set<String> unauthRoles = new LinkedHashSet<String>();

		private AuthorizationDataEntry(Bytes[] userAddress) {
			this.userAddress = userAddress;
		}

		@Override
		public Bytes[] getUserAddresses() {
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
		public UserRolesAuthorizer forUser(BlockchainIdentity... userIds) {
			return UserAuthorizeOpTemplate.this.forUser(userIds);
		}

		@Override
		public UserRolesAuthorizer forUser(Bytes... userAddresses) {
			return UserAuthorizeOpTemplate.this.forUser(userAddresses);
		}
	}
}
