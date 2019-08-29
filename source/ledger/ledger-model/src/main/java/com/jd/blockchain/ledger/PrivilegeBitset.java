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
	// 加入前缀位，可避免序列化时输出空的字节数组；
	private static final boolean[] PREFIX = { false, false, false, true, false, false, false, true };
	private static final int OFFSET = PREFIX.length;
	private static final int MAX_SIZE = 256 - PREFIX.length;

	private BitSet permissionBits;

	private CodeIndexer<E> codeIndexer;

	public PrivilegeBitset(CodeIndexer<E> codeIndexer) {
		this.permissionBits = new BitSet();
		this.codeIndexer = codeIndexer;
		// 设置前缀；
		for (int i = 0; i < PREFIX.length; i++) {
			permissionBits.set(i, PREFIX[i]);
		}
	}

	public PrivilegeBitset(byte[] codeBytes, CodeIndexer<E> codeIndexer) {
		if (codeBytes.length > MAX_SIZE) {
			throw new IllegalArgumentException(
					"The size of code bytes specified to PrivilegeBitset exceed the max size[" + MAX_SIZE + "]!");
		}
		this.permissionBits = BitSet.valueOf(codeBytes);
		this.codeIndexer = codeIndexer;
		// 校验前缀；
		for (int i = 0; i < PREFIX.length; i++) {
			if (permissionBits.get(i) != PREFIX[i]) {
				throw new IllegalArgumentException("The code bytes is not match the privilege prefix code!");
			}
		}
	}

	private PrivilegeBitset(BitSet bits, CodeIndexer<E> codeIndexer) {
		this.permissionBits = bits;
		this.codeIndexer = codeIndexer;
	}

	public boolean isEnable(E permission) {
		return permissionBits.get(index(permission));
	}

	public void enable(E permission) {
		permissionBits.set(index(permission));
	}

	public void disable(E permission) {
		permissionBits.clear(index(permission));
	}

	@SuppressWarnings("unchecked")
	public void enable(E... permissions) {
		for (E p : permissions) {
			permissionBits.set(index(p));
		}
	}

	@SuppressWarnings("unchecked")
	public void disable(E... permissions) {
		for (E p : permissions) {
			permissionBits.clear(index(p));
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
	@SuppressWarnings("unchecked")
	public Privilege<E> union(PrivilegeBitset<E>... privileges) {
		return union(privileges, 0, privileges.length);
	}

	/**
	 * 把指定的权限合并到当前的权限中； <br>
	 * 
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
	@SuppressWarnings("unchecked")
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

	private int index(E permission) {
		return OFFSET + codeIndexer.getCodeIndex(permission);
	}

	static interface CodeIndexer<E extends Enum<?>> {
		int getCodeIndex(E permission);
	}
}
