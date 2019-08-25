package com.jd.blockchain.ledger;

import java.util.BitSet;

import com.jd.blockchain.utils.io.BytesSerializable;

/**
 * PrivilegeBitset 定义了用位表示的权限码；
 * 
 * @author huanghaiquan
 *
 */
public class PrivilegeBitset<E extends Enum<?>> implements Privilege<E>, BytesSerializable {

	private BitSet permissionBits;

	private CodeIndexer<E> codeIndexer;

	public PrivilegeBitset(CodeIndexer<E> codeIndexer) {
		this(new BitSet(), codeIndexer);
	}

	public PrivilegeBitset(byte[] codeBytes, CodeIndexer<E> codeIndexer) {
		this(BitSet.valueOf(codeBytes), codeIndexer);
	}

	private PrivilegeBitset(BitSet bits, CodeIndexer<E> codeIndexer) {
		this.permissionBits = bits;
		this.codeIndexer = codeIndexer;
	}

	public boolean isEnable(E permission) {
		return permissionBits.get(codeIndexer.getCodeIndex(permission));
	}

	public void enable(E permission) {
		permissionBits.set(codeIndexer.getCodeIndex(permission));
	}

	public void disable(E permission) {
		permissionBits.clear(codeIndexer.getCodeIndex(permission));
	}

	@SuppressWarnings("unchecked")
	public void enable(E... permissions) {
		for (E p : permissions) {
			permissionBits.set(codeIndexer.getCodeIndex(p));
		}
	}

	@SuppressWarnings("unchecked")
	public void disable(E... permissions) {
		for (E p : permissions) {
			permissionBits.clear(codeIndexer.getCodeIndex(p));
		}
	}

	@Override
	public byte[] toBytes() {
		return permissionBits.toByteArray();
	}

	/**
	 * 把指定的权限合并到当前的权限中； <br>
	 *
	 * @param privileges
	 * @return
	 */
	public Privilege<E> union(PrivilegeBitset<E>... privileges) {
		return union(privileges, 0, privileges.length);
	}

	/**
	 * 把指定的权限合并到当前的权限中； <br>
	 * @param privileges
	 * @param offset
	 * @param count
	 * @return
	 */
	public Privilege<E> union(PrivilegeBitset<E>[] privileges, int offset, int count) {
		BitSet bits = this.permissionBits;
		for (int i = 0; i < count; i++) {
			bits.or(privileges[i + offset].permissionBits);
		}
		return this;
	}

	/**
	 * 保留当前的权限与指定权限的共同生效的部分，同时清除其它的权限位； <br>
	 * 
	 * @param privileges
	 * @return
	 */
	public Privilege<E> intersect(PrivilegeBitset<E>... privileges) {
		return intersect(privileges, 0, privileges.length);
	}

	/**
	 * 保留当前的权限与指定权限的共同生效的部分，同时清除其它的权限位； <br>
	 * 
	 * @param privileges
	 * @param offset
	 * @param count
	 * @return
	 */
	public Privilege<E> intersect(PrivilegeBitset<E>[] privileges, int offset, int count) {
		BitSet bits = this.permissionBits;
		for (int i = 0; i < count; i++) {
			bits.and(privileges[i + offset].permissionBits);
		}
		return this;
	}

	public PrivilegeBitset<E> clone() {
		return new PrivilegeBitset<E>((BitSet) permissionBits.clone(), codeIndexer);
	}

	static interface CodeIndexer<E extends Enum<?>> {
		int getCodeIndex(E permission);
	}
}
