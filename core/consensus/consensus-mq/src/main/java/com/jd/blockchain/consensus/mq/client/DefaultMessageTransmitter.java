/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.client.DefaultMessageTransmitter
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 下午3:05
 * Description:
 */
package com.jd.blockchain.consensus.mq.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jd.blockchain.consensus.event.EventEntity;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.consensus.MessageService;
import com.jd.blockchain.consensus.mq.consumer.MsgQueueConsumer;
import com.jd.blockchain.consensus.mq.event.TxBlockedEvent;
import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;
import com.jd.blockchain.consensus.mq.util.MessageConvertUtil;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.jd.blockchain.utils.concurrent.CompletableAsyncFuture;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class DefaultMessageTransmitter implements MessageTransmitter, MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageTransmitter.class);

    private final ExecutorService messageExecutorArray = Executors.newFixedThreadPool(10);

//    private final ExecutorService blockExecutor = Executors.newSingleThreadExecutor();
//
//    private final ExecutorService extendExecutor = Executors.newSingleThreadExecutor();

    private final Map<String, MessageListener> messageListeners = new ConcurrentHashMap<>();

    private final BlockEventHandler blockEventHandler = new BlockEventHandler();

    private final ExtendEventHandler extendEventHandler = new ExtendEventHandler();

    private MsgQueueProducer txProducer;

    private MsgQueueProducer msgProducer;

    private MsgQueueConsumer blConsumer;

    private MsgQueueConsumer msgConsumer;

    private boolean isConnected = false;

    public DefaultMessageTransmitter setTxProducer(MsgQueueProducer txProducer) {
        this.txProducer = txProducer;
        return this;
    }

    public DefaultMessageTransmitter setMsgProducer(MsgQueueProducer msgProducer) {
        this.msgProducer = msgProducer;
        return this;
    }

    public DefaultMessageTransmitter setBlConsumer(MsgQueueConsumer blConsumer) {
        this.blConsumer = blConsumer;
        return this;
    }

    public DefaultMessageTransmitter setMsgConsumer(MsgQueueConsumer msgConsumer) {
        this.msgConsumer = msgConsumer;
        return this;
    }

    @Override
    public AsyncFuture<byte[]> sendOrdered(byte[] message) {

        AsyncFuture<byte[]> messageFuture;

        try {
            publishMessage(txProducer, message);
            messageFuture = messageHandle(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return messageFuture;
    }

    @Override
    public AsyncFuture<byte[]> sendUnordered(byte[] message) {
        AsyncFuture<byte[]> messageFuture;
        try {
            publishMessage(msgProducer, message);
            messageFuture = messageHandle(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return messageFuture;
    }

    @Override
    public void connect() throws Exception{
        if (!isConnected) {
            this.txProducer.connect();
            this.blConsumer.connect(blockEventHandler);
            this.msgProducer.connect();
            this.msgConsumer.connect(extendEventHandler);
            isConnected = true;
            blConsumer.start();
            msgConsumer.start();
//            blockConsumerListening();
//            extendConsumerListening();
        }
    }

    @Override
    public void publishMessage(MsgQueueProducer producer, byte[] message) throws Exception {
        producer.publish(message);
    }

    @Override
    public void close() {
        try {
            txProducer.close();
            blConsumer.close();
            msgProducer.close();
            msgConsumer.close();
            isConnected = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AsyncFuture<byte[]> messageHandle(byte[] message) throws Exception {
//      异步回调
//      需要监听MQ结块的应答
//      首先需要一个Consumer，在子类已实现
        String messageKey = messageKey(message);
        AsyncFuture<byte[]> messageFuture = registerMessageListener(messageKey);
        return messageFuture;
    }

    private String messageKey(byte[] message) {
        return MessageConvertUtil.messageKey(message);
    }

    private AsyncFuture<byte[]> registerMessageListener(String messageKey) {
        CompletableAsyncFuture<byte[]> future = new CompletableAsyncFuture<>();
        MessageListener messageListener = new MessageListener(messageKey, future);
        messageListener.addListener();
        return future;
    }

//    private void blockConsumerListening() {
//        // 区块事件由单独一个线程处理
//        blockExecutor.execute(() -> {
//            while(isConnected) {
//                try {
//                    byte[] txBlockedEventBytes = blConsumer.start();
//                    // 交由事件处理机制来处理
//                    if (txBlockedEventBytes != null && txBlockedEventBytes.length > 0) {
//                        txBlockedEventHandle(txBlockedEventBytes);
//                    }
//                } catch (Exception e) {
//                    LOGGER.error("process block listening message exception {}", e.getMessage());
//                }
//            }
//        });
//    }

//    private void extendConsumerListening() {
//        extendExecutor.execute(() -> {
//            while (isConnected) {
//                try {
//                    byte[] msgBytes = msgConsumer.start();
//                    // 交由事件处理机制来处理
//                    if (msgBytes != null && msgBytes.length > 0) {
//                        extendMessageHandle(msgBytes);
//                    }
//                } catch (Exception e) {
//                    LOGGER.error("process extend listening message exception {}", e.getMessage());
//                }
//            }
//        });
//    }

    private void txBlockedEventHandle(byte[] bytes) {
        messageExecutorArray.execute(() -> {
            if (!this.messageListeners.isEmpty()) {
                // 首先将字节数组转换为BlockEvent
                final TxBlockedEvent txBlockedEvent =
                        MessageConvertUtil.convertBytes2TxBlockedEvent(bytes);
                if (txBlockedEvent != null) {
                    // 需要判断该区块是否需要处理
                    if (isTxBlockedEventNeedManage(txBlockedEvent)) {
                        dealTxBlockedEvent(txBlockedEvent);
                    }
                }
            }
        });
    }

    private void extendMessageHandle(byte[] message) {
        messageExecutorArray.execute(() -> {
            String messageKey = messageKey(message);
            if (messageListeners.containsKey(messageKey)) {
                dealExtendMessage(messageKey, message);
            }
        });
    }

    private boolean isTxBlockedEventNeedManage(final TxBlockedEvent txBlockedEvent) {
        if (this.messageListeners.isEmpty()) {
            return false;
        }
        if (messageListeners.containsKey(txBlockedEvent.getTxKey())) {
            return true;
        }
        // 无须处理区块高度
        return false;
    }

    private void dealTxBlockedEvent(final TxBlockedEvent txBlockedEvent) {
        String txKey = txBlockedEvent.getTxKey();
        MessageListener txListener = this.messageListeners.get(txKey);
        if (txListener != null) {
            txListener.received(txBlockedEvent);
            this.messageListeners.remove(txKey);
        }
    }

    private void dealExtendMessage(final String messageKey, final byte[] message) {
        MessageListener txListener = this.messageListeners.get(messageKey);
        if (txListener != null) {
            txListener.received(message);
            this.messageListeners.remove(messageKey);
        }
    }

    private class MessageListener {

        final String messageKey;

        final CompletableAsyncFuture<byte[]> future;

        final AtomicBoolean isDeal = new AtomicBoolean(false);

        public MessageListener(String messageKey, CompletableAsyncFuture<byte[]> future) {
            this.messageKey = messageKey;
            this.future = future;
            addListener();
        }

        public void addListener() {
            synchronized (messageListeners) {
                messageListeners.put(messageKey, this);
            }
        }

        public void received(final TxBlockedEvent txBlockedEvent) {
            // 期望是false，假设是false则设置为true，成功的情况下表示是第一次
            byte[] txResp = txBlockedEvent.txResponseBytes();
            if (txResp != null) {
                if (isDeal.compareAndSet(false, true)) {
                    //生成对应的交易应答
                    future.complete(txResp);
                }
            }
        }

        public void received(final byte[] message) {
            // 期望是false，假设是false则设置为true，成功的情况下表示是第一次
            if (message != null) {
                if (isDeal.compareAndSet(false, true)) {
                    //生成对应的交易应答
                    future.complete(message);
                }
            }
        }
    }

    public class BlockEventHandler implements EventHandler<EventEntity<byte[]>> {

        @Override
        public void onEvent(EventEntity<byte[]> event, long sequence, boolean endOfBatch) throws Exception {
            byte[] txBlockedEventBytes = event.getEntity();
            if (txBlockedEventBytes != null && txBlockedEventBytes.length > 0) {
                txBlockedEventHandle(txBlockedEventBytes);
            }
        }
    }

    public class ExtendEventHandler implements EventHandler<EventEntity<byte[]>> {

        @Override
        public void onEvent(EventEntity<byte[]> event, long sequence, boolean endOfBatch) throws Exception {
            byte[] msgBytes = event.getEntity();
            if (msgBytes != null && msgBytes.length > 0) {
                extendMessageHandle(msgBytes);
            }
        }
    }
}