/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.nats.RabbitConsumer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午10:40
 * Description:
 */
package com.jd.blockchain.consensus.mq.consumer;

import com.jd.blockchain.utils.ConsoleUtils;
import com.lmax.disruptor.EventHandler;
import io.nats.client.*;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class NatsConsumer extends AbstractConsumer implements MsgQueueConsumer {

    private final ExecutorService msgListener = Executors.newSingleThreadExecutor();

    private Connection nc;

    private Subscription sub;

    private String server;

    private String topic;

    public NatsConsumer(String server, String topic) {
        this.server = server;
        this.topic = topic;
    }

    @Override
    public void connect(EventHandler eventHandler) throws Exception {
        initEventHandler(eventHandler);
        Options options = new Options.Builder().server(server).noReconnect().build();
        this.nc = Nats.connect(options);
        this.sub = nc.subscribe(topic);
        this.nc.flush(Duration.ZERO);
        ConsoleUtils.info("[*] NatsConsumer[%s, %s] connect success !!!", this.server, this.topic);
    }

    @Override
    public void start() {
        msgListener.execute(() -> {
            for (;;) {
                try {
                    Message msg = this.sub.nextMessage(Duration.ZERO);
                    eventProducer.publish(msg.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        try {
            nc.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}