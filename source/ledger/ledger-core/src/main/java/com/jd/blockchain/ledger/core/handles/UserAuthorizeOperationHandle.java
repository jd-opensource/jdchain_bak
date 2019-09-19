package com.jd.blockchain.ledger.core.handles;

import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.RolePrivilegeSettings;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.UserAuthorizeOperation;
import com.jd.blockchain.ledger.UserAuthorizeOperation.UserRolesEntry;
import com.jd.blockchain.ledger.UserRoles;
import com.jd.blockchain.ledger.UserAuthorizationSettings;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.MultiIDsPolicy;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.SecurityContext;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionRequestExtension;
import com.jd.blockchain.utils.Bytes;

public class UserAuthorizeOperationHandle extends AbstractLedgerOperationHandle<UserAuthorizeOperation> {

	public UserAuthorizeOperationHandle() {
		super(UserAuthorizeOperation.class);
	}

	@Override
	protected void doProcess(UserAuthorizeOperation operation, LedgerDataset newBlockDataset,
			TransactionRequestExtension request, LedgerQuery ledger,
			OperationHandleContext handleContext) {
		// 权限校验；
		SecurityPolicy securityPolicy = SecurityContext.getContextUsersPolicy();
		securityPolicy.checkEndpointPermission(LedgerPermission.CONFIGURE_ROLES, MultiIDsPolicy.AT_LEAST_ONE);

		// 操作账本；

		UserRolesEntry[] urcfgs = operation.getUserRolesAuthorizations();
		UserAuthorizationSettings urSettings = newBlockDataset.getAdminDataset().getAuthorizations();
		RolePrivilegeSettings rolesSettings = newBlockDataset.getAdminDataset().getRolePrivileges();
		if (urcfgs != null) {
			for (UserRolesEntry urcfg : urcfgs) {
				//
				String[] authRoles = urcfg.getAuthorizedRoles();
				List<String> validRoles = new ArrayList<String>();
				if (authRoles != null) {
					for (String r : authRoles) {
						if (rolesSettings.contains(r)) {
							validRoles.add(r);
						}
					}
				}
				for (Bytes address : urcfg.getUserAddresses()) {
					UserRoles ur = urSettings.getUserRoles(address);
					if (ur == null) {
						// 这是新的授权；
						RolesPolicy policy = urcfg.getPolicy();
						if (policy == null) {
							policy = RolesPolicy.UNION;
						}
						urSettings.addUserRoles(address, policy, validRoles);
					} else {
						// 更改之前的授权；
						ur.addRoles(validRoles);
						ur.removeRoles(urcfg.getUnauthorizedRoles());

						// 如果请求中设置了策略，才进行更新；
						RolesPolicy policy = urcfg.getPolicy();
						if (policy != null) {
							ur.setPolicy(policy);
						}
					}
				}
			}
		}
	}

}
