package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

/**
 * 交易内容；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= DataCodes.TX_CONTENT)
public interface TransactionContent extends TransactionContentBody, HashObject {
    @Override
    @DataField(order=1, primitiveType = PrimitiveType.BYTES)
    HashDigest getHash();

}
