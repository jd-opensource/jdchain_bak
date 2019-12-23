/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.consumer.AbstractConsumer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/29 下午12:31
 * Description:
 */
package com.jd.blockchain.consensus.mq.consumer;

import com.jd.blockchain.consensus.event.EventEntity;
import com.jd.blockchain.consensus.event.EventProducer;
import com.jd.blockchain.consensus.mq.exchange.BytesEventFactory;
import com.jd.blockchain.consensus.mq.exchange.BytesEventProducer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/29
 * @since 1.0.0
 */

public abstract class AbstractConsumer implements MsgQueueConsumer {

    protected EventProducer eventProducer;

    protected void initEventHandler(EventHandler eventHandler) {
        Disruptor<EventEntity<byte[]>> disruptor =
                new Disruptor<>(new BytesEventFactory(),
                        BytesEventFactory.BUFFER_SIZE, r -> {
                    return new Thread(r);
                }, ProducerType.SINGLE, new BlockingWaitStrategy());

        disruptor.handleEventsWith(eventHandler);
        disruptor.start();
        RingBuffer<EventEntity<byte[]>> ringBuffer = disruptor.getRingBuffer();
        this.eventProducer = new BytesEventProducer(ringBuffer);
    }
}