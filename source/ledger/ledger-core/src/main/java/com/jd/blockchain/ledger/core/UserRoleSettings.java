package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.utils.Bytes;

public interface UserRoleSettings {

	long getRoleCount();

	/**
	 * 加入新的用户角色授权； <br>
	 * 
	 * 如果该用户的授权已经存在，则引发 {@link LedgerException} 异常；
	 * 
	 * @param userAddress
	 * @param rolesPolicy
	 * @param roles
	 */
	void addUserRoles(Bytes userAddress, RolesPolicy rolesPolicy, String... roles);

	/**
	 * 更新用户角色授权； <br>
	 * 如果指定用户的授权不存在，或者版本不匹配，则引发 {@link LedgerException} 异常；
	 * 
	 * @param userRoles
	 */
	void updateUserRoles(UserRoles userRoles);

	/**
	 * 设置用户的角色； <br>
	 * 如果用户的角色授权不存在，则创建新的授权；
	 * 
	 * @param userAddress 用户；
	 * @param policy      角色策略；
	 * @param roles       角色列表；
	 * @return
	 */
	long setRoles(Bytes userAddress, RolesPolicy policy, String... roles);

	/**
	 * 查询角色授权；
	 * 
	 * <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	UserRoles getUserRoles(Bytes userAddress);

	UserRoles[] getRoleAuthorizations();

}