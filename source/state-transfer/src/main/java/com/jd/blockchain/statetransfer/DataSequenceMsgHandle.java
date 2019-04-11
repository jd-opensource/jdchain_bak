package com.jd.blockchain.statetransfer;

import com.jd.blockchain.stp.communication.MessageHandler;
import com.jd.blockchain.stp.communication.RemoteSession;

/**
 *
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public class DataSequenceMsgHandle implements MessageHandler {

    DataSequenceReader dsReader;
    DataSequenceWriter dsWriter;

    public DataSequenceMsgHandle(DataSequenceReader dsReader, DataSequenceWriter dsWriter) {
        this.dsReader = dsReader;
        this.dsWriter = dsWriter;
    }

    @Override
    public void receive(byte[] key, byte[] data, RemoteSession session) {

    }

    /**
     *
     *
     */

}
