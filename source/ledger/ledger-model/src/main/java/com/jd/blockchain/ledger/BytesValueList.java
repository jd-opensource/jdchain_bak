package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code = DataCodes.BYTES_VALUE_LIST)
public interface BytesValueList {

	BytesValue[] getValues();

}
