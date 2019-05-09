package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

/**
 * 交易请求；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= DataCodes.REQUEST)
public interface TransactionRequest extends NodeRequest, HashObject {

	/**
	 * 交易请求的 hash ；<br>
	 *
	 * @return
	 */
	@Override
	@DataField(order=1, primitiveType = PrimitiveType.BYTES)
	HashDigest getHash();
}