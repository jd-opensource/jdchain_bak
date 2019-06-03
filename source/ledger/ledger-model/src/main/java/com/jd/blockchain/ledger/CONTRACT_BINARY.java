package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.utils.Bytes;

/**
 * contract args for Binary;
 * @author zhaogw
 * date 2019-05-17 15:32
 */
@DataContract(code = DataCodes.CONTRACT_BINARY)
public interface CONTRACT_BINARY {

    @DataField(order=2, primitiveType= PrimitiveType.BYTES)
    Bytes getValue();
}
