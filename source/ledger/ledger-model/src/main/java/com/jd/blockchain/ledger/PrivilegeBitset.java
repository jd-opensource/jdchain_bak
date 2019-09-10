package com.jd.blockchain.ledger;

import java.util.BitSet;

import com.jd.blockchain.utils.Int8Code;
import com.jd.blockchain.utils.io.BytesSerializable;

/**
 * PrivilegeBitset 定义了用位表示的权限码；
 * 
 * @author huanghaiquan
 *
 */
public abstract class PrivilegeBitset<E extends Enum<?> & Int8Code> implements Privilege<E>, BytesSerializable, Cloneable {
	// 加入1个字节的前缀位 0xF1，可避免序列化时输出空的字节数组；
	private static final byte PREFIX = (byte) 0xF1;
	private static final byte[] PREFIX_BYTES = { PREFIX };
	private static final int OFFSET = 8;
	private static final int MAX_SIZE = 32;

	// 前缀中置为 1 的位数，值 0xF1 有 5 个比特位为 1；
	private static final int PREFIX_CARDINALITY = 5;

	private BitSet permissionBits;

	public PrivilegeBitset() {
		// 设置前缀；
		this.permissionBits = BitSet.valueOf(PREFIX_BYTES);
	}

	/**
	 * @param codeBytes   权限的字节位；
	 * @param codeIndexer
	 */
	public PrivilegeBitset(byte[] codeBytes) {
		// 检查长度；
		if (codeBytes.length == 0) {
			throw new IllegalArgumentException("Empty code bytes!");
		}
		if (codeBytes.length > MAX_SIZE) {
			throw new IllegalArgumentException(
					"The size of code bytes specified to PrivilegeBitset exceed the max size[" + MAX_SIZE + "]!");
		}
		// 校验前缀；
		if (codeBytes[0] != PREFIX) {
			throw new IllegalArgumentException("The code bytes is not match the privilege prefix code!");
		}

		this.permissionBits = BitSet.valueOf(codeBytes);
	}

	protected PrivilegeBitset(BitSet bits) {
		this.permissionBits = bits;
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

	@Override
	public Privilege<E> clone() {
		try {
			BitSet bitSet = (BitSet) permissionBits.clone();
			@SuppressWarnings("unchecked")
			PrivilegeBitset<E> privilege = (PrivilegeBitset<E>) super.clone();
			privilege.permissionBits = bitSet;
			return privilege;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	protected BitSet cloneBitSet() {
		return (BitSet) permissionBits.clone();
	}

	private int index(E permission) {
		return OFFSET + permission.getCode();
	}

	public int getPermissionCount() {
		return permissionBits.cardinality() - PREFIX_CARDINALITY;
	}

}
