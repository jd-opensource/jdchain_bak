package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.crypto.hash.HashDigest;
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

    //获得交易操作时间；
    @DataField(order=2, primitiveType = ValueType.INT64)
    Long getTxOpTime();

}
