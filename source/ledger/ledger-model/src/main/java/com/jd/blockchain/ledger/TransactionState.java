package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;
import com.jd.blockchain.utils.ValueType;

/**
 * 交易（事务）执行状态；
 * 
 * @author huanghaiquan
 *
 */
@EnumContract(code= TypeCodes.ENUM_TYPE_TRANSACTION_STATE)
public enum TransactionState {

	/**
	 * 成功；
	 */
	SUCCESS((byte) 0),

	/**
	 * 共识错误；
	 */
	CONSENSUS_ERROR((byte) 1),
	
	/**
	 * 账本错误；
	 */
	LEDGER_ERROR((byte) 2),

	/**
	 * 系统错误；
	 */
	SYSTEM_ERROR((byte) 0x80),

	/**
	 * 超时；
	 */
	TIMEOUT((byte) 0x81);

	@EnumField(type= ValueType.INT8)
	public final byte CODE;

	private TransactionState(byte code) {
		this.CODE = code;
	}

	public static TransactionState valueOf(byte code) {
		for (TransactionState tr : values()) {
			if (tr.CODE == code) {
				return tr;
			}
		}
		throw new IllegalArgumentException("Unsupported transaction result code!");
	}

}
