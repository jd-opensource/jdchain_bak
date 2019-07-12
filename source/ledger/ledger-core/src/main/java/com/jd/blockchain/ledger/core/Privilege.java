package com.jd.blockchain.ledger.core;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;

public class Privilege {

	private BitSet permissions;

	public Privilege(byte[] codeBytes) {
		permissions = BitSet.valueOf(codeBytes);
	}

	public boolean isEnable(LedgerPermission permission) {
		return permissions.get(getCodeIndex(permission));
	}

	public void enable(LedgerPermission permission) {
		permissions.set(getCodeIndex(permission));
	}

	public void disable(LedgerPermission permission) {
		permissions.clear(getCodeIndex(permission));
	}

	public static int getCodeIndex(LedgerPermission permission) {
		return permission.CODE & 0xFF;
	}

	public byte[] toCodeBytes() {
		return permissions.toByteArray();
	}

	public boolean[] getPermissionStates() {
		LedgerPermission[] PMs = LedgerPermission.values();

		LedgerPermission maxPermission = Arrays.stream(PMs).max(new Comparator<LedgerPermission>() {
			@Override
			public int compare(LedgerPermission o1, LedgerPermission o2) {
				return getCodeIndex(o1) - getCodeIndex(o2);
			}
		}).get();

		boolean[] states = new boolean[getCodeIndex(maxPermission) + 1];
		int idx = -1;
		for (LedgerPermission pm : PMs) {
			idx = getCodeIndex(pm);
			states[idx] = permissions.get(idx);
		}

		return states;
	}
}
