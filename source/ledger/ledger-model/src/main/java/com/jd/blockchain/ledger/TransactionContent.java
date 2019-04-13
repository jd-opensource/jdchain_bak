package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.ValueType;

/**
 * 交易内容；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.TX_CONTENT)
public interface TransactionContent extends TransactionContentBody, HashObject {
    @Override
    @DataField(order=1, primitiveType = ValueType.BYTES)
    HashDigest getHash();

}
