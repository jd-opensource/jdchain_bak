package com.jd.blockchain.ledger.core;

public interface AccountPrivilege {

	/**
	 * 数据“读”的操作码；
	 * 
	 * @return
	 */
	byte getReadingOpCode();

	/**
	 * “写”的操作码；
	 * 
	 * @return
	 */
	byte getWrittingOpCode();

	/**
	 * 其它的扩展操作码；
	 * 
	 * @return
	 */
	byte[] getExtOpCodes();

}
