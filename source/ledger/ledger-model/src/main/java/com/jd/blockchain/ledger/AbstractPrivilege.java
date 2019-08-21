package com.jd.blockchain.ledger;

import java.util.BitSet;

import com.jd.blockchain.utils.io.BytesSerializable;

/**
 * LedgerPrivilege 账本特权是授权给特定角色的权限代码序列；
 * 
 * @author huanghaiquan
 *
 */
public abstract class AbstractPrivilege<E extends Enum<?>> implements Privilege<E>, BytesSerializable {

	private BitSet permissionBits;
	
	public AbstractPrivilege() {
		permissionBits = new BitSet();
	}

	public AbstractPrivilege(byte[] codeBytes) {
		permissionBits =  BitSet.valueOf(codeBytes);
	}

	public boolean isEnable(E permission) {
		return permissionBits.get(getCodeIndex(permission));
	}

	public void enable(E permission) {
		permissionBits.set(getCodeIndex(permission));
	}

	public void disable(E permission) {
		permissionBits.clear(getCodeIndex(permission));
	}
	
	@SuppressWarnings("unchecked")
	public void enable(E... permissions) {
		for (E p : permissions) {
			permissionBits.set(getCodeIndex(p));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void disable(E... permissions) {
		for (E p : permissions) {
			permissionBits.clear(getCodeIndex(p));
		}
	}

	protected abstract int getCodeIndex(E permission);

	@Override
	public byte[] toBytes() {
		return permissionBits.toByteArray();
	}

}
