/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.rabbitmq.nats.consensus.disruptor.ExchangeEventFactory
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 上午10:48
 * Description:
 */
package com.jd.blockchain.consensus.mq.exchange;

import com.jd.blockchain.consensus.event.EventEntity;
import com.lmax.disruptor.EventFactory;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class BytesEventFactory implements EventFactory<EventEntity<byte[]>> {

    public static final int BUFFER_SIZE = 256 * 1024;
//    public static final int BUFFER_SIZE = 8 * 1024;

    @Override
    public EventEntity<byte[]> newInstance() {
        return new EventEntity<>();
    }
}