package com.jd.blockchain.ledger;

import java.util.Collection;

import com.jd.blockchain.utils.Bytes;

public interface UserAuthorizationSettings {

	/**
	 * 单一用户可被授权的角色数量的最大值；
	 */
	public static final int MAX_ROLES_PER_USER = 20;

	/**
	 * 进行了授权的用户的数量；
	 * 
	 * @return
	 */
	long getUserCount();

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

	/**
	 * 返回全部的用户授权；
	 * 
	 * @return
	 */
	UserRoles[] getUserRoles();

	/**
	 * 是否只读；
	 * 
	 * @return
	 */
	boolean isReadonly();

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
	 * 加入新的用户角色授权； <br>
	 * 
	 * 如果该用户的授权已经存在，则引发 {@link LedgerException} 异常；
	 * 
	 * @param userAddress
	 * @param rolesPolicy
	 * @param roles
	 */
	void addUserRoles(Bytes userAddress, RolesPolicy rolesPolicy, Collection<String> roles);

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

}