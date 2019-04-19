package com.jd.blockchain.statetransfer.message;

import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;

public class DSMsgResolverFactory {

    public static DataSequenceMsgEncoder getEncoder(DataSequenceWriter dsWriter, DataSequenceReader dsReader) {
        return new DataSequenceMsgEncoder(dsWriter, dsReader);
    }

    public static DataSequenceMsgDecoder getDecoder(DataSequenceWriter dsWriter, DataSequenceReader dsReader) {
        return new DataSequenceMsgDecoder(dsWriter, dsReader);
    }
}
