package com.jd.blockchain.ledger.core;

import java.util.BitSet;

import com.jd.blockchain.utils.io.BytesSerializable;

/**
 * LedgerPrivilege 账本特权是授权给特定角色的权限代码序列；
 * 
 * @author huanghaiquan
 *
 */
public abstract class AbstractPrivilege<E extends Enum<?>> implements Privilege<E>, BytesSerializable {

	private BitSet permissions;

	public AbstractPrivilege(byte[] codeBytes) {
		permissions = BitSet.valueOf(codeBytes);
	}

	public boolean isEnable(E permission) {
		return permissions.get(getCodeIndex(permission));
	}

	public void enable(E permission) {
		permissions.set(getCodeIndex(permission));
	}

	public void disable(E permission) {
		permissions.clear(getCodeIndex(permission));
	}

//	private int getCodeIndex(E permission) {
//		return permission.CODE & 0xFF;
//	}

	protected abstract int getCodeIndex(E permission);

	@Override
	public byte[] toBytes() {
		return permissions.toByteArray();
	}

//	public boolean[] getPermissionStates() {
//		LedgerPermission[] PMs = LedgerPermission.values();
//
//		LedgerPermission maxPermission = Arrays.stream(PMs).max(new Comparator<LedgerPermission>() {
//			@Override
//			public int compare(LedgerPermission o1, LedgerPermission o2) {
//				return getCodeIndex(o1) - getCodeIndex(o2);
//			}
//		}).get();
//
//		boolean[] states = new boolean[getCodeIndex(maxPermission) + 1];
//		int idx = -1;
//		for (LedgerPermission pm : PMs) {
//			idx = getCodeIndex(pm);
//			states[idx] = permissions.get(idx);
//		}
//
//		return states;
//	}
}
