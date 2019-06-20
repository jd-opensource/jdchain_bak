package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code = DataCodes.BYTES_VALUE_LIST)
public interface BytesValueList {

	@DataField(order = 0, refContract = true, list = true)
	BytesValue[] getValues();

}
