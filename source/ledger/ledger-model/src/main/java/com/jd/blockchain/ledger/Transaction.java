package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.ValueType;
import com.jd.blockchain.utils.io.ByteArray;

/**
 * Transaction 区块链交易，是被原子执行的操作集合；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.TX)
public interface Transaction extends NodeRequest, HashObject {

	/**
	 * 交易 Hash；
	 * 
	 * 这是包含交易内容、签名列表、交易结果的完整性 hash；
	 * 
	 * @return
	 */
	@DataField(order=1, primitiveType = ValueType.BYTES)
	@Override
	HashDigest getHash();

	/**
	 * 交易被包含的区块高度；
	 * 
	 * @return
	 */
	@DataField(order=2, primitiveType=ValueType.INT64)
	long getBlockHeight();

	/**
	 * 交易的执行结果；
	 * 
	 * 值为枚举值 {@link TransactionState#CODE} 之一；
	 * 
	 * @return
	 */
	@DataField(order=3, refEnum=true)
	TransactionState getExecutionState();

}
