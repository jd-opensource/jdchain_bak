/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.client.MessageTransmitter
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 上午11:21
 * Description:
 */
package com.jd.blockchain.consensus.mq.client;


import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public interface MessageTransmitter {

    void connect() throws Exception;

    void publishMessage(MsgQueueProducer producer, byte[] message) throws Exception;

//    void processMsg(byte[] message);

    void close();
}