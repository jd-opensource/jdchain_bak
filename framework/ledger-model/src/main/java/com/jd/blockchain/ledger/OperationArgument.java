package com.jd.blockchain.ledger;

import com.jd.blockchain.utils.io.ByteArray;

/**
 * 操作参数；
 * 
 * @author huanghaiquan
 *
 */
public interface OperationArgument {

	/**
	 * 参数类型； <br>
	 * 
	 * @return
	 */
	byte getKey();

	/**
	 * 参数值；
	 * 
	 * @return
	 */
	ByteArray getValue();

}
