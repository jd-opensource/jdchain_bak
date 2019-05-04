package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.consts.DataCodes;


@DataContract(code = DataCodes.CONSENSUS_BFTSMART_BLOCK_SETTINGS)
public interface BftsmartCommitBlockSettings {

    @DataField(order = 0, primitiveType = DataType.INT32)
    int getTxSizePerBlock();

    @DataField(order = 1, primitiveType = DataType.INT64)
    long getMaxDelayMilliSecondsPerBlock();
}
