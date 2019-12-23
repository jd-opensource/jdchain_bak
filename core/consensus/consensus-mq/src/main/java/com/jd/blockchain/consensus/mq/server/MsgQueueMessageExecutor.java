/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.server.MsgQueueMessageExecutor
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/13 下午2:10
 * Description:
 */
package com.jd.blockchain.consensus.mq.server;

import com.jd.blockchain.consensus.event.EventEntity;
import com.jd.blockchain.consensus.mq.event.MessageEvent;
import com.jd.blockchain.consensus.mq.event.TxBlockedEvent;
import com.jd.blockchain.consensus.mq.exchange.ExchangeEventInnerEntity;
import com.jd.blockchain.consensus.mq.exchange.ExchangeType;
import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;
import com.jd.blockchain.consensus.mq.util.MessageConvertUtil;
import com.jd.blockchain.consensus.service.MessageHandle;
import com.jd.blockchain.consensus.service.StateMachineReplicate;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.utils.concurrent.AsyncFuture;
import com.lmax.disruptor.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/13
 * @since 1.0.0
 */

public class MsgQueueMessageExecutor implements EventHandler<EventEntity<ExchangeEventInnerEntity>> {

    private static final Logger LOGGER  = LoggerFactory.getLogger(MsgQueueMessageExecutor.class);

    // todo 暂不处理队列溢出导致的OOM
    private final ExecutorService blockEventExecutor = Executors.newFixedThreadPool(10);

    private MsgQueueProducer blProducer;

    private List<MessageEvent> exchangeEvents = new ArrayList<>();

    private String realmName;

    private MessageHandle messageHandle;

    private final AtomicInteger messageId = new AtomicInteger();

    private int txSizePerBlock = 1000;

    private StateMachineReplicate stateMachineReplicator;

    public MsgQueueMessageExecutor setRealmName(String realmName) {
        this.realmName = realmName;
        return this;
    }

    public MsgQueueMessageExecutor setBlProducer(MsgQueueProducer blProducer) {
        this.blProducer = blProducer;
        return this;
    }

    public MsgQueueMessageExecutor setTxSizePerBlock(int txSizePerBlock) {
        this.txSizePerBlock = txSizePerBlock;
        return this;
    }

    public MsgQueueMessageExecutor setMessageHandle(MessageHandle messageHandle) {
        this.messageHandle = messageHandle;
        return this;
    }

    public MsgQueueMessageExecutor setStateMachineReplicator(StateMachineReplicate stateMachineReplicator) {
        this.stateMachineReplicator = stateMachineReplicator;
        return this;
    }

    public MsgQueueMessageExecutor init() {
        try {
            long latestStateId = stateMachineReplicator.getLatestStateID(realmName);
            // 设置基础消息ID
            messageId.set(((int)latestStateId + 1) * txSizePerBlock);
            blProducer.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public void onEvent(EventEntity<ExchangeEventInnerEntity> event, long sequence, boolean endOfBatch) throws Exception {
        ExchangeEventInnerEntity entity = event.getEntity();
        if (entity != null) {
            if (entity.getType() == ExchangeType.BLOCK || entity.getType() == ExchangeType.EMPTY) {
                if (!exchangeEvents.isEmpty()) {
                    process(exchangeEvents);
                    exchangeEvents.clear();
                }
            } else {
                byte[] bytes = event.getEntity().getContent();
                String key = bytes2Key(bytes);
                exchangeEvents.add(new MessageEvent(key, bytes));
            }
        }
    }

    private void process(List<MessageEvent> messageEvents) {
        if (messageEvents != null && !messageEvents.isEmpty()) {
            try {
                Map<String, AsyncFuture<byte[]>> txResponseMap = execute(messageEvents);
                if (txResponseMap != null && !txResponseMap.isEmpty()) {
//                    byte[] asyncFuture;
                    for (Map.Entry<String, AsyncFuture<byte[]>> entry : txResponseMap.entrySet()) {
                        final String txKey = entry.getKey();
                        final AsyncFuture<byte[]> asyncFuture = entry.getValue();
//                        asyncFuture = entry.getValue().get();

                        blockEventExecutor.execute(() -> {
                            TxBlockedEvent txBlockedEvent = new TxBlockedEvent(txKey,
                                    MessageConvertUtil.base64Encode(asyncFuture.get()));
                            byte[] serializeBytes = MessageConvertUtil.serializeTxBlockedEvent(txBlockedEvent);
                            // 通过消息队列发送该消息
                            try {
                                this.blProducer.publish(serializeBytes);
                            } catch (Exception e) {
                                LOGGER.error("publish block event message exception {}", e.getMessage());
                            }
                        });
                    }
                }
            } catch (Exception e) {
                // 打印日志
                LOGGER.error("process message exception {}", e.getMessage());
            }
        }
    }

    private Map<String, AsyncFuture<byte[]>> execute(List<MessageEvent> messageEvents) {
//        System.out.printf("Thread[%s %s] execute messageEvents !!! \r\n",
//                Thread.currentThread().getId(), Thread.currentThread().getName());
        Map<String, AsyncFuture<byte[]>> asyncFutureMap = new HashMap<>();
        // 使用MessageHandle处理
//        long startTime = System.currentTimeMillis();
//        int txSize = messageEvents.size();
        String batchId = messageHandle.beginBatch(realmName);
        try {
            for (MessageEvent messageEvent : messageEvents) {
                String txKey = messageEvent.getMessageKey();
                byte[] txContent = messageEvent.getMessage();
                AsyncFuture<byte[]> asyncFuture = messageHandle.processOrdered(messageId.getAndIncrement(), txContent, realmName, batchId);
                asyncFutureMap.put(txKey, asyncFuture);
            }
            messageHandle.completeBatch(realmName, batchId);
            messageHandle.commitBatch(realmName, batchId);
//            long totalTime = System.currentTimeMillis() - startTime;
//            String content = String.format("batch[%s] process, time = {%s}ms, TPS = %.2f \r\n",
//                    batchId, totalTime, txSize * 1000.0D / totalTime);
//            System.out.println(content);
//            logQueue.put(content);
            // 提交之后需要获取对应的结果
        } catch (Exception e) {
            // todo 需要处理应答码 404
            messageHandle.rollbackBatch(realmName, batchId, TransactionState.CONSENSUS_ERROR.CODE);
        }
        return asyncFutureMap;
    }


    private String bytes2Key(byte[] bytes) {
        return MessageConvertUtil.messageKey(bytes);
    }
}