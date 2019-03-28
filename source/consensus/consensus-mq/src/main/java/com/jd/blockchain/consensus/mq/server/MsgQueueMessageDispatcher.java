/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.server.MsgQueueMessageDispatcher
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 上午11:30
 * Description:
 */
package com.jd.blockchain.consensus.mq.server;


import java.io.Closeable;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public interface MsgQueueMessageDispatcher extends Runnable, Closeable {

    void init();

    void connect() throws Exception;

    void stop() throws Exception;
}