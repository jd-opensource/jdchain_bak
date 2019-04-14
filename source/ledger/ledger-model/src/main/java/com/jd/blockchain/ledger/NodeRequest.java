package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;

@DataContract(code = TypeCodes.REQUEST_NODE)
public interface NodeRequest extends EndpointRequest {


	/**
	 * 接入交易的节点的签名；<br>
	 * 
	 * 注：能够提交交易的节点可以是共识节点或网关节点；
	 * 
	 * @return
	 */

	@DataField(order=1, list=true, refContract=true)
	DigitalSignature[] getNodeSignatures();

}
