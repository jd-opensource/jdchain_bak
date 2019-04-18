package com.jd.blockchain.statetransfer;

import com.jd.blockchain.stp.communication.MessageExecutor;
import com.jd.blockchain.stp.communication.RemoteSession;

/**
 *
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public class DataSequenceMsgHandle implements MessageExecutor {

    DataSequenceReader dsReader;
    DataSequenceWriter dsWriter;

    public DataSequenceMsgHandle(DataSequenceReader dsReader, DataSequenceWriter dsWriter) {
        this.dsReader = dsReader;
        this.dsWriter = dsWriter;
    }

    @Override
    public byte[] receive(String key, byte[] data, RemoteSession session) {
        return new byte[0];
    }

    @Override
    public REPLY replyType() {
        return REPLY.AUTO;
    }

    /**
     *
     *
     */

}
