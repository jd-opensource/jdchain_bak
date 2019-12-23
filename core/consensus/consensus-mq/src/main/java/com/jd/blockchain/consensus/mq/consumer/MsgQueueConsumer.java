/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: MsgQueueConsumer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午10:38
 * Description:
 */
package com.jd.blockchain.consensus.mq.consumer;

import com.lmax.disruptor.EventHandler;

import java.io.Closeable;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public interface MsgQueueConsumer extends Closeable {

    void connect(EventHandler eventHandler) throws Exception;

    void start() throws Exception;
}

