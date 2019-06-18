package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code = DataCodes.TX_OP_RESULT)
public interface OperationResult {

	@DataField(order = 1, primitiveType = PrimitiveType.INT32)
	int getIndex();

	@DataField(order = 2, refContract = true)
	BytesValue getResult();
}
