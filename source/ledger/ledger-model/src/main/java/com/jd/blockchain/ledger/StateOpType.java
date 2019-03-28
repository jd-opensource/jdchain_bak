package com.jd.blockchain.ledger;

/**
 * 状态操作类型；
 * 
 * @author huanghaiquan
 *
 */
public enum StateOpType {

	/**
	 * 设置状态值；
	 */
	SET((byte) 1),

	/**
	 * 移除状态值；
	 */
	REMOVE((byte) 0);

	public final byte CODE;

	private StateOpType(byte code) {
		this.CODE = code;
	}

	public static StateOpType valueOf(byte code) {
		for (StateOpType opType : StateOpType.values()) {
			if (opType.CODE == code) {
				return opType;
			}
		}
		throw new IllegalArgumentException("Unsupported code[" + code + "] of StateOpType!");

	}

}
