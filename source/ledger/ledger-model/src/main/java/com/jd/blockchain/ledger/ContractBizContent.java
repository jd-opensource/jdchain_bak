package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * build complex param Object, provide more String attributes;
 */
@DataContract(code = DataCodes.CONTRACT_BIZ_CONTENT)
public interface ContractBizContent {
	/**
	 * param lists;
	 * @return
	 */
	@DataField(order = 1, list = true, primitiveType = PrimitiveType.TEXT, genericContract = true)
	String[] getAttrs();

}
