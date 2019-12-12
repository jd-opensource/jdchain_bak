/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.peer.consensus.MessageEvent
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/23 上午11:45
 * Description:
 */
package com.jd.blockchain.consensus.mq.event;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/23
 * @since 1.0.0
 */

public class MessageEvent {

    String messageKey;

    byte[] message;

    public MessageEvent(String messageKey, byte[] message) {
        this.messageKey = messageKey;
        this.message = message;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}