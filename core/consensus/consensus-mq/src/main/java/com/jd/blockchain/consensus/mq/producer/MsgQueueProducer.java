/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: MsgQueueProducer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午10:37
 * Description:
 */
package com.jd.blockchain.consensus.mq.producer;

import java.io.Closeable;
import java.util.List;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public interface MsgQueueProducer extends Closeable {

    void connect() throws Exception;

    void publish(byte[] message) throws Exception;

    void publishString(String message) throws Exception;

    void publishStringList(List<String> messages) throws Exception;

    void publishStringArray(String[] messages) throws Exception;

    void publishBytesArray(byte[][] message) throws Exception;

    void publishBytesList(List<byte[]> messages) throws Exception;
}