package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;
import com.jd.blockchain.consts.DataCodes;

/**
 * 交易（事务）执行状态；
 * 
 * @author huanghaiquan
 *
 */
@EnumContract(code = DataCodes.ENUM_TYPE_TRANSACTION_STATE)
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
	 * 由于在错误的账本上执行交易而被丢弃；
	 */
	DISCARD_BY_WRONG_LEDGER((byte) 3),

	/**
	 * 由于交易内容的验签失败而丢弃；
	 */
	DISCARD_BY_WRONG_CONTENT_SIGNATURE((byte) 4),

	/**
	 * 数据账户不存在；
	 */
	DATA_ACCOUNT_DOES_NOT_EXIST((byte) 5),
	
	/**
	 * 用户不存在；
	 */
	USER_DOES_NOT_EXIST((byte) 6),
	
	/**
	 * 合约不存在；
	 */
	CONTRACT_DOES_NOT_EXIST((byte) 6),

	/**
	 * 系统错误；
	 */
	SYSTEM_ERROR((byte) 0x80),

	/**
	 * 超时；
	 */
	TIMEOUT((byte) 0x81);

	@EnumField(type = PrimitiveType.INT8)
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
