//package com.jd.blockchain.consensus.bftsmart;
//
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataField;
//import com.jd.blockchain.binaryproto.PrimitiveType;
//import com.jd.blockchain.consts.DataCodes;
//
//
//@DataContract(code = DataCodes.CONSENSUS_BFTSMART_BLOCK_SETTINGS)
//public interface BftsmartCommitBlockSettings {
//
//    @DataField(order = 0, primitiveType = PrimitiveType.INT32)
//    int getTxSizePerBlock();
//
//    @DataField(order = 1, primitiveType = PrimitiveType.INT64)
//    long getMaxDelayMilliSecondsPerBlock();
//}
