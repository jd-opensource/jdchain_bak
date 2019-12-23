package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.PermissionType;

/**
 * 账户权限设置操作；
 * 
 * <br>
 * 
 * 注：默认情况下，在账户被注册时，账户自身会包含在权限设置表中，具有全部的权限； <br>
 * 
 * 但这不是必须的，使用者可以根据业务需要，去掉账户自身的权限，并将权限赋予其它的账户，以此实现将区块链账户分别用于表示“角色”和“数据”这两种目的；
 * 
 * @author huanghaiquan
 *
 */
public interface PrivilegeSettingOperationBuilder {
	
	PrivilegeSettingOperationBuilder setThreshhold(PermissionType privilege, long threshhold);

	PrivilegeSettingOperationBuilder enable(PermissionType privilege, String address, int weight);

	PrivilegeSettingOperationBuilder disable(PermissionType privilege, String address);

}
