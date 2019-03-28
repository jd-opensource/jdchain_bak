package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.utils.ValueType;

/**
 * 交易请求；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.REQUEST)
public interface TransactionRequest extends NodeRequest, HashObject {

	/**
	 * 交易请求的 hash ；<br>
	 *
	 * @return
	 */
	@Override
	@DataField(order=1, primitiveType = ValueType.BYTES)
	HashDigest getHash();
}