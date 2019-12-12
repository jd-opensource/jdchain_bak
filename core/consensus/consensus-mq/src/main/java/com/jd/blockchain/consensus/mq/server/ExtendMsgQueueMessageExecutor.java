/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.server.DefaultMsgQueueMessageDispatcher
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/13 上午11:05
 * Description:
 */
package com.jd.blockchain.consensus.mq.server;

import com.jd.blockchain.consensus.event.EventEntity;
import com.jd.blockchain.consensus.mq.consumer.MsgQueueConsumer;
import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;
import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.lmax.disruptor.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/13
 * @since 1.0.0
 */

public class ExtendMsgQueueMessageExecutor implements MsgQueueMessageDispatcher, EventHandler<EventEntity<byte[]>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendMsgQueueMessageExecutor.class);

    private final ExecutorService dataExecutor = Executors.newSingleThreadExecutor();

    private MsgQueueProducer msgProducer;

    private MsgQueueConsumer msgConsumer;

    private MessageHandle messageHandle;

    private boolean isRunning;

    private boolean isConnected;

    public ExtendMsgQueueMessageExecutor setMsgProducer(MsgQueueProducer msgProducer) {
        this.msgProducer = msgProducer;
        return this;
    }

    public ExtendMsgQueueMessageExecutor setMsgConsumer(MsgQueueConsumer msgConsumer) {
        this.msgConsumer = msgConsumer;
        return this;
    }

    public ExtendMsgQueueMessageExecutor setMessageHandle(MessageHandle messageHandle) {
        this.messageHandle = messageHandle;
        return this;
    }

    @Override
    public void init() {
        // do nothing
    }

    public synchronized void connect() throws Exception {
        if (!isConnected) {
            msgProducer.connect();
            msgConsumer.connect(this);
            msgConsumer.start();
            isConnected = true;
        }
    }

    @Override
    public synchronized void stop() throws Exception {
        isRunning = false;
        close();
    }

    @Override
    public void run() {
        this.isRunning = true;
//        this.msgConsumer.start();
//        listen();
    }

//    private void listen() {
//        while (isRunning) {
//            try {
//                byte[] data = this.msgConsumer.start();
//                // 收到数据后由队列处理
//                handleData(data);
//            } catch (Exception e) {
//                // 日志打印
//                LOGGER.error("extend message handle exception {}", e.getMessage());
//            }
//        }
//    }

    private void handleData(byte[] data) {
        dataExecutor.execute(() -> {
            try {
                AsyncFuture<byte[]> result = messageHandle.processUnordered(data);
                msgProducer.publish(result.get());
            } catch (Exception e) {
                LOGGER.error("process Unordered message exception {}", e.getMessage());
            }
        });
    }

    @Override
    public void close() throws IOException {
        isConnected = false;
        this.msgProducer.close();
        this.msgConsumer.close();
    }

    @Override
    public void onEvent(EventEntity<byte[]> event, long sequence, boolean endOfBatch) throws Exception {
        byte[] data = event.getEntity();
        handleData(data);
    }
}