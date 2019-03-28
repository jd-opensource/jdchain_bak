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
import com.jd.blockchain.consensus.event.EventProducer;
import com.jd.blockchain.consensus.mq.consumer.MsgQueueConsumer;
import com.jd.blockchain.consensus.mq.exchange.ExchangeEntityFactory;
import com.jd.blockchain.consensus.mq.exchange.ExchangeEventFactory;
import com.jd.blockchain.consensus.mq.exchange.ExchangeEventInnerEntity;
import com.jd.blockchain.consensus.mq.exchange.ExchangeEventProducer;
import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/13
 * @since 1.0.0
 */

public class DefaultMsgQueueMessageDispatcher implements MsgQueueMessageDispatcher, EventHandler<EventEntity<byte[]>> {

    private static final byte[] blockCommitBytes = new byte[]{0x00};

    private final BlockingQueue<byte[]> dataQueue = new ArrayBlockingQueue<>(1024 * 16);

    private final ExecutorService dataExecutor = Executors.newSingleThreadExecutor();

    private final ScheduledThreadPoolExecutor timeHandleExecutor = new ScheduledThreadPoolExecutor(2);

    private final AtomicLong blockIndex = new AtomicLong();

    private long syncIndex = 0L;

    private MsgQueueProducer txProducer;

    private MsgQueueConsumer txConsumer;

    private EventProducer eventProducer;

    private EventHandler eventHandler;

    private final int TX_SIZE_PER_BLOCK;

    private final long MAX_DELAY_MILLISECONDS_PER_BLOCK;

    private boolean isRunning;

    private boolean isConnected;

    public DefaultMsgQueueMessageDispatcher(int txSizePerBlock, long maxDelayMilliSecondsPerBlock) {
        this.TX_SIZE_PER_BLOCK = txSizePerBlock;
        this.MAX_DELAY_MILLISECONDS_PER_BLOCK = maxDelayMilliSecondsPerBlock;
    }

    public DefaultMsgQueueMessageDispatcher setTxProducer(MsgQueueProducer txProducer) {
        this.txProducer = txProducer;
        return this;
    }

    public DefaultMsgQueueMessageDispatcher setTxConsumer(MsgQueueConsumer txConsumer) {
        this.txConsumer = txConsumer;
        return this;
    }

    public DefaultMsgQueueMessageDispatcher setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
        return this;
    }

    public void init() {
        handleDisruptor(eventHandler);
    }

    private void handleDisruptor(EventHandler eventHandler) {
        Disruptor<EventEntity<ExchangeEventInnerEntity>> disruptor =
                new Disruptor<>(new ExchangeEventFactory(),
                        ExchangeEventFactory.BUFFER_SIZE, r -> {
                    return new Thread(r);
                }, ProducerType.SINGLE, new BlockingWaitStrategy());

        disruptor.handleEventsWith(eventHandler);
        disruptor.start();
        RingBuffer<EventEntity<ExchangeEventInnerEntity>> ringBuffer = disruptor.getRingBuffer();

        this.eventProducer = new ExchangeEventProducer(ringBuffer);
    }

    public synchronized void connect() throws Exception {
        if (!isConnected) {
            txProducer.connect();
            txConsumer.connect(this);
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
        try {
            txConsumer.start();
        } catch (Exception e) {

        }
//        handleData();
//        listen();
    }

//    private void listen() {
//        while (isRunning) {
//            try {
//                byte[] data = this.txConsumer.start();
//                dataQueue.put(data);
//                // 收到数据后由队列处理
////                handleData(data);
//            } catch (Exception e) {
//                // 日志打印
//                ConsoleUtils.info("ERROR dispatcher start data exception {%s}", e.getMessage());
//            }
//        }
//    }

//    private void handleData() {
//        dataExecutor.execute(() -> {
//            byte[] data;
//            for (;;) {
//                try {
//                    data = dataQueue.take();
//                    if (data.length == 1) {
//                        // 结块标识优先处理
//                        syncIndex = 0L;
//                        this.blockIndex.getAndIncrement();
//                        eventProducer.publish(ExchangeEntityFactory.newBlockInstance());
//                    } else {
//                        if (syncIndex == 0) { // 收到第一个交易
//                            // 需要判断是否需要进行定时任务
//                            if (MAX_DELAY_MILLISECONDS_PER_BLOCK > 0) {
//                                this.timeHandleExecutor.schedule(
//                                        timeBlockTask(this.blockIndex.get()),
//                                        MAX_DELAY_MILLISECONDS_PER_BLOCK, TimeUnit.MILLISECONDS);
//                            }
//                        }
//                        syncIndex++;
//                        eventProducer.publish(ExchangeEntityFactory.newTransactionInstance(data));
//                        if (syncIndex == TX_SIZE_PER_BLOCK) {
//                            syncIndex = 0L;
//                            this.blockIndex.getAndIncrement();
//                            eventProducer.publish(ExchangeEntityFactory.newBlockInstance());
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        for (;;) {
//            try {
//                final byte[] data = dataQueue.take();
//                dataExecutor.execute(() -> {
//                        if (data.length == 1) {
//                            // 结块标识优先处理
//                            syncIndex = 0L;
//                            this.blockIndex.getAndIncrement();
//                            eventProducer.publish(ExchangeEntityFactory.newBlockInstance());
//                        } else {
//                            if (syncIndex == 0) { // 收到第一个交易
//                                // 需要判断是否需要进行定时任务
//                                if (MAX_DELAY_MILLISECONDS_PER_BLOCK > 0) {
//                                    this.timeHandleExecutor.schedule(
//                                            timeBlockTask(this.blockIndex.get()),
//                                            MAX_DELAY_MILLISECONDS_PER_BLOCK, TimeUnit.MILLISECONDS);
//                                }
//                            }
//                            syncIndex++;
//                            eventProducer.publish(ExchangeEntityFactory.newTransactionInstance(data));
//                            if (syncIndex == TX_SIZE_PER_BLOCK) {
//                                syncIndex = 0L;
//                                this.blockIndex.getAndIncrement();
//                                eventProducer.publish(ExchangeEntityFactory.newBlockInstance());
//                            }
//                        }
//                    }
//                );
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private void handleData(final byte[] data) {
//        dataExecutor.execute(() -> {
//            try {
//                if (data.length == 1) {
//                    // 结块标识优先处理
//                    syncIndex = 0L;
//                    this.blockIndex.getAndIncrement();
//                    eventProducer.publish(ExchangeEntityFactory.newBlockInstance());
//                } else {
//                    if (syncIndex == 0) { // 收到第一个交易
//                        // 需要判断是否需要进行定时任务
//                        if (MAX_DELAY_MILLISECONDS_PER_BLOCK > 0) {
//                            this.timeHandleExecutor.schedule(
//                                    timeBlockTask(this.blockIndex.get()),
//                                    MAX_DELAY_MILLISECONDS_PER_BLOCK, TimeUnit.MILLISECONDS);
//                        }
//                    }
//                    syncIndex++;
//                    eventProducer.publish(ExchangeEntityFactory.newTransactionInstance(data));
//                    if (syncIndex == TX_SIZE_PER_BLOCK) {
//                        syncIndex = 0L;
//                        this.blockIndex.getAndIncrement();
//                        eventProducer.publish(ExchangeEntityFactory.newBlockInstance());
//                    }
//                }
//            } catch (Exception e) {
//                // 记录日志
//                ConsoleUtils.info("ERROR TransactionDispatcher process queue data exception {%s}", e.getMessage());
//            }
//        });
//
//    }

    private Runnable timeBlockTask(final long currentBlockIndex) {
        return () -> {
            final boolean isEqualBlock = this.blockIndex.compareAndSet(
                    currentBlockIndex, currentBlockIndex + 1);
            if (isEqualBlock) {
                try {
                    txProducer.publish(blockCommitBytes);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public void close() throws IOException {
        this.txProducer.close();
        this.txConsumer.close();
    }

    @Override
    public void onEvent(EventEntity<byte[]> event, long sequence, boolean endOfBatch) throws Exception {
        try {
            byte[] data = event.getEntity();
//            System.out.printf("Thread [%s, $s] on event !!!\r\n",
//                    Thread.currentThread().getId(), Thread.currentThread().getName());
            if (data.length == 1) {
                // 结块标识优先处理
                syncIndex = 0L;
                this.blockIndex.getAndIncrement();
                eventProducer.publish(ExchangeEntityFactory.newBlockInstance());
            } else {
                if (syncIndex == 0) { // 收到第一个交易
                    // 需要判断是否需要进行定时任务
                    if (MAX_DELAY_MILLISECONDS_PER_BLOCK > 0) {
                        this.timeHandleExecutor.schedule(
                                timeBlockTask(this.blockIndex.get()),
                                MAX_DELAY_MILLISECONDS_PER_BLOCK, TimeUnit.MILLISECONDS);
                    }
                }
                syncIndex++;
                eventProducer.publish(ExchangeEntityFactory.newTransactionInstance(data));
                if (syncIndex == TX_SIZE_PER_BLOCK) {
                    syncIndex = 0L;
                    this.blockIndex.getAndIncrement();
                    eventProducer.publish(ExchangeEntityFactory.newBlockInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}