package com.jd.blockchain.contract.param;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code = DataCodes.CONTRACT_BYTES)
public interface WRAP_BYTES {

    @DataField(order = 1, primitiveType = PrimitiveType.BYTES)
    byte[] getValue();
}
