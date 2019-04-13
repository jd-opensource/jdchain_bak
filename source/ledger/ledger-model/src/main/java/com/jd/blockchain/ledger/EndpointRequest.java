package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.ValueType;

@DataContract(code= TypeCodes.REQUEST_ENDPOINT)
public interface EndpointRequest {

	@DataField(order=1, primitiveType = ValueType.BYTES)
	HashDigest getHash();
	/**
	 * 交易内容；
	 * 
	 * @return
	 */
	@DataField(order=2, refContract=true)
	TransactionContent getTransactionContent();

	/**
	 * 终端用户的签名列表；
	 * 
	 * @return
	 */
	@DataField(order=3, list=true, refContract=true)
	DigitalSignature[] getEndpointSignatures();
	
}
