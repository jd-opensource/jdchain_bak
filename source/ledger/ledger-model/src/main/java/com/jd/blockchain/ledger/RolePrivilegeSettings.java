package com.jd.blockchain.ledger;

public interface RolePrivilegeSettings {

	/**
	 * 角色名称的最大 Unicode 字符数；
	 */
	public static final int MAX_ROLE_NAME_LENGTH = 20;

	/**
	 * 角色的数量；
	 * 
	 * @return
	 */
	long getRoleCount();

	/**
	 * 查询角色权限；
	 * 
	 * <br>
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	RolePrivileges getRolePrivilege(String roleName);

	/**
	 * 返回指定位置的角色权限；
	 * 
	 * @param index
	 * @param count
	 * @return
	 */
	RolePrivileges[] getRolePrivileges(int index, int count);

	/**
	 * 返回所有的角色权限；
	 * 
	 * @return
	 */
	RolePrivileges[] getRolePrivileges();

	/**
	 * 是否只读；
	 * 
	 * @return
	 */
	boolean isReadonly();

	/**
	 * 加入新的角色权限； <br>
	 * 
	 * 如果指定的角色已经存在，则引发 {@link LedgerException} 异常；
	 * 
	 * @param roleName        角色名称；不能超过 {@link #MAX_ROLE_NAME_LENGTH} 个 Unicode 字符；
	 * @param ledgerPrivilege
	 * @param txPrivilege
	 */
	long addRolePrivilege(String roleName, Privileges privileges);

	/**
	 * 加入新的角色权限； <br>
	 * 
	 * 如果指定的角色已经存在，则引发 {@link LedgerException} 异常；
	 * 
	 * @param roleName        角色名称；不能超过 {@link #MAX_ROLE_NAME_LENGTH} 个 Unicode 字符；
	 * @param ledgerPrivilege
	 * @param txPrivilege
	 */
	long addRolePrivilege(String roleName, LedgerPrivilege ledgerPrivilege, TransactionPrivilege txPrivilege);

	/**
	 * 加入新的角色权限； <br>
	 * 
	 * 如果指定的角色已经存在，则引发 {@link LedgerException} 异常；
	 * 
	 * @param roleName          角色名称；不能超过 {@link #MAX_ROLE_NAME_LENGTH} 个 Unicode
	 *                          字符；
	 * @param ledgerPermissions 给角色授予的账本权限列表；
	 * @param txPermissions     给角色授予的交易权限列表；
	 * @return
	 */
	long addRolePrivilege(String roleName, LedgerPermission[] ledgerPermissions, TransactionPermission[] txPermissions);

	/**
	 * 更新角色权限； <br>
	 * 如果指定的角色不存在，或者版本不匹配，则引发 {@link LedgerException} 异常；
	 * 
	 * @param participant
	 */
	void updateRolePrivilege(RolePrivileges roleAuth);

	/**
	 * 授权角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName    角色；
	 * @param permissions 权限列表；
	 * @return
	 */
	long enablePermissions(String roleName, LedgerPermission... permissions);

	/**
	 * 授权角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName    角色；
	 * @param permissions 权限列表；
	 * @return
	 */
	long enablePermissions(String roleName, TransactionPermission... permissions);

	/**
	 * 禁止角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName    角色；
	 * @param permissions 权限列表；
	 * @return
	 */
	long disablePermissions(String roleName, LedgerPermission... permissions);

	/**
	 * 禁止角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName    角色；
	 * @param permissions 权限列表；
	 * @return
	 */
	long disablePermissions(String roleName, TransactionPermission... permissions);

	/**
	 * 授权角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName
	 * @param ledgerPermissions
	 * @param txPermissions
	 * @return
	 */
	long enablePermissions(String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] txPermissions);

	/**
	 * 禁用角色指定的权限； <br>
	 * 如果角色不存在，则返回 -1；
	 * 
	 * @param roleName
	 * @param ledgerPermissions
	 * @param txPermissions
	 * @return
	 */
	long disablePermissions(String roleName, LedgerPermission[] ledgerPermissions,
			TransactionPermission[] txPermissions);

	boolean contains(String r);

}