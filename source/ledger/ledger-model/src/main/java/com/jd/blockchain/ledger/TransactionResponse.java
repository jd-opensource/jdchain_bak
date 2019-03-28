package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.utils.ValueType;

/**
 * 交易请求 {@link TransactionRequest} 的回复；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.TX_RESPONSE)
public interface TransactionResponse {

	/**
	 * 交易原始内容的哈希；
	 * 
	 * @return
	 */
	@DataField(order=1, primitiveType = ValueType.BYTES)
	HashDigest getContentHash();

	/**
	 * 执行状态；
	 * 
	 * @return
	 */
	@DataField(order=2, refEnum=true)
	TransactionState getExecutionState();

	/**
	 * 交易被纳入的区块哈希；
	 * 
	 * @return
	 */
	@DataField(order=3, primitiveType = ValueType.BYTES)
	HashDigest getBlockHash();

	/**
	 * 交易被纳入的区块高度；
	 * 
	 * <p>
	 * 如果未生成区块，则返回 -1;
	 * 
	 * @return
	 */
	@DataField(order=4, primitiveType=ValueType.INT64)
	long getBlockHeight();
	
	@DataField(order=5, primitiveType=ValueType.BOOLEAN)
	boolean isSuccess();

}