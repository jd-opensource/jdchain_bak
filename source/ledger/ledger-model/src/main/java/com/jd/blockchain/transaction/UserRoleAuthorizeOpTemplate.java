package com.jd.blockchain.transaction;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.ledger.UserRoleAuthorizeOperation;
import com.jd.blockchain.utils.ArrayUtils;
import com.jd.blockchain.utils.Bytes;

public class UserRoleAuthorizeOpTemplate implements UserRoleAuthorizeOperation {

	static {
		DataContractRegistry.register(UserRegisterOperation.class);
	}

	private Map<Bytes, UserRoleAuthConfig> rolesMap = new LinkedHashMap<Bytes, UserRoleAuthConfig>();

	public UserRoleAuthorizeOpTemplate() {
	}

	public UserRoleAuthorizeOpTemplate(BlockchainIdentity userID) {
	}

	@Override
	public UserRoleAuthConfig[] getUserRoleAuthorizations() {
		return ArrayUtils.toArray(rolesMap.values(), UserRoleAuthConfig.class);
	}

	public static class UserRoleAuthConfig implements UserRoleAuthEntry {

		private Bytes userAddress;

		private long expectedVersion;

		private RolesPolicy rolePolicy;

		private Set<String> authRoles = new LinkedHashSet<String>();
		private Set<String> unauthRoles = new LinkedHashSet<String>();

		private UserRoleAuthConfig(Bytes userAddress, long expectedVersion) {
			this.userAddress = userAddress;
			
		}

		@Override
		public Bytes getUserAddress() {
			return userAddress;
		}

		@Override
		public long getExplectedVersion() {
			return expectedVersion;
		}

		@Override
		public RolesPolicy getRolesPolicy() {
			return rolePolicy;
		}

		@Override
		public String[] getAuthRoles() {
			return ArrayUtils.toArray(authRoles, String.class);
		}

		@Override
		public String[] getUnauthRoles() {
			return ArrayUtils.toArray(unauthRoles, String.class);
		}

		public UserRoleAuthConfig authorize(String... roles) {
			Collection<String> roleList = ArrayUtils.asList(roles);
			authRoles.addAll(roleList);
			unauthRoles.removeAll(roleList);

			return this;
		}

		public UserRoleAuthConfig unauthorize(String... roles) {
			Collection<String> roleList = ArrayUtils.asList(roles);
			unauthRoles.addAll(roleList);
			authRoles.removeAll(roleList);

			return this;
		}
	}
}
