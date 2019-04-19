package com.jd.blockchain.statetransfer.message;

import com.jd.blockchain.stp.communication.message.LoadMessage;

/**
 *
 *
 */
public class DataSequenceLoadMessage implements LoadMessage {
    byte[] bytes;

    public DataSequenceLoadMessage(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] toBytes() {
        return bytes;
    }
}
