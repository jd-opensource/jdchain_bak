package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

import java.math.BigDecimal;

/**
 * contract args for BIG_INT;
 * @author zhaogw
 * date 2019-05-17 15:32
 */
@DataContract(code = DataCodes.CONTRACT_BINARY)
public interface CONTRACT_BIG_INT {

    @DataField(order=2, primitiveType= PrimitiveType.BIG_INT)
    BigDecimal getValue();
}
