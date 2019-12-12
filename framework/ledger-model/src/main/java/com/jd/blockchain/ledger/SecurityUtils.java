package com.jd.blockchain.ledger;

public class SecurityUtils {

	public static final int MAX_ROLE_NAMES = 20;

	/**
	 * 校验角色名称的有效性，并格式化角色名称：去掉两端空白字符，统一为大写字符；
	 * 
	 * @param roleName
	 * @return
	 */
	public static String formatRoleName(String roleName) {
		if (roleName == null) {
			throw new IllegalArgumentException("Role name is empty!");
		}
		roleName = roleName.trim();
		if (roleName.length() > MAX_ROLE_NAMES) {
			throw new IllegalArgumentException("Role name exceeds max length!");
		}
		if (roleName.length() == 0) {
			throw new IllegalArgumentException("Role name is empty!");
		}

		return roleName.toUpperCase();
	}

}
