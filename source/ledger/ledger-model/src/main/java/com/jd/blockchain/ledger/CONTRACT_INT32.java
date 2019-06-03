package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * contract args for int32;
 * @author zhaogw
 * date 2019-05-17 15:32
 */
@DataContract(code = DataCodes.CONTRACT_INT32)
public interface CONTRACT_INT32 {

    @DataField(order=2, primitiveType= PrimitiveType.INT32)
    int getValue();
}
