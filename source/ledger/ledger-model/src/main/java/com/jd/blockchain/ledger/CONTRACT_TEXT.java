package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 * contract args for String;
 * @author zhaogw
 * date 2019-05-17 15:32
 */
@DataContract(code = DataCodes.CONTRACT_TEXT)
public interface CONTRACT_TEXT {

    @DataField(order=2, primitiveType= PrimitiveType.TEXT)
    String getValue();
}
