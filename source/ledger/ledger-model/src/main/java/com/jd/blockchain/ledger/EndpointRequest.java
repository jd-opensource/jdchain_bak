package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

@DataContract(code= DataCodes.REQUEST_ENDPOINT)
public interface EndpointRequest {

	@DataField(order=1, primitiveType = PrimitiveType.BYTES)
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
