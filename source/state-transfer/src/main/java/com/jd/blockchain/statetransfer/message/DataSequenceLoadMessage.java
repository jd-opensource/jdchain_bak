package com.jd.blockchain.statetransfer.message;

import com.jd.blockchain.stp.communication.message.LoadMessage;

/**
 * 数据序列复制的负载消息
 * @author zhangshuang
 * @create 2019/4/18
 * @since 1.0.0
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
