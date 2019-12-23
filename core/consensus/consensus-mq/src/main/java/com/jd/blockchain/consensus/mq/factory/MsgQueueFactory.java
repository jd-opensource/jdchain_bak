/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: MsgQueueFactory
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午10:13
 * Description:
 */
package com.jd.blockchain.consensus.mq.factory;


import com.jd.blockchain.consensus.mq.consumer.MsgQueueConsumer;
import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;

import static com.jd.blockchain.consensus.mq.factory.MsgQueueConfig.NATS_PREFIX;
import static com.jd.blockchain.consensus.mq.factory.MsgQueueConfig.RABBIT_PREFIX;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class MsgQueueFactory {

    public static MsgQueueProducer newProducer(String server, String topic) {
        try {
            if (server.startsWith(NATS_PREFIX)) {
                return NatsFactory.newProducer(server, topic);
            } else if (server.startsWith(RABBIT_PREFIX)) {
                return RabbitFactory.newProducer(server, topic);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static MsgQueueConsumer newConsumer(String server, String topic) {
        try {
            if (server.startsWith(NATS_PREFIX)) {
                return NatsFactory.newConsumer(server, topic);
            } else if (server.startsWith(RABBIT_PREFIX)) {
                return RabbitFactory.newConsumer(server, topic);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}