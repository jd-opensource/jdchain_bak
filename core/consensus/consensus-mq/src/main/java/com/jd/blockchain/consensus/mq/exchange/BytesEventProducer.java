/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.rabbitmq.nats.consensus.disruptor.ExchangeEventProducer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 上午10:50
 * Description:
 */
package com.jd.blockchain.consensus.mq.exchange;

import com.jd.blockchain.consensus.event.EventEntity;
import com.jd.blockchain.consensus.event.EventProducer;
import com.lmax.disruptor.RingBuffer;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class BytesEventProducer implements EventProducer<byte[]> {

    private final RingBuffer<EventEntity<byte[]>> ringBuffer;

    public BytesEventProducer(RingBuffer<EventEntity<byte[]>> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void publish(byte[] entity) {
        long sequence = ringBuffer.next();
        try {
            EventEntity<byte[]> event = ringBuffer.get(sequence);
            event.setEntity(entity);
        } finally {
            this.ringBuffer.publish(sequence);
        }
    }
}