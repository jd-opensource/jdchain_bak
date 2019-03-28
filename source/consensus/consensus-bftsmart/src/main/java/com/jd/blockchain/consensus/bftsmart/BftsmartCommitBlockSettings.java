package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.utils.ValueType;


@DataContract(code = TypeCodes.CONSENSUS_BFTSMART_BLOCK_SETTINGS)
public interface BftsmartCommitBlockSettings {

    @DataField(order = 0, primitiveType = ValueType.INT32)
    int getTxSizePerBlock();

    @DataField(order = 1, primitiveType = ValueType.INT64)
    long getMaxDelayMilliSecondsPerBlock();
}
