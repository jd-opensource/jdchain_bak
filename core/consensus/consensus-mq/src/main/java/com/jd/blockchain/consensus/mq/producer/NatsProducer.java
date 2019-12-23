/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: NatsProducer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午10:39
 * Description:
 */
package com.jd.blockchain.consensus.mq.producer;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.jd.blockchain.utils.ConsoleUtils;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class NatsProducer implements MsgQueueProducer {

    // 主要操作：发送MQ请求
    private Connection nc;

    private String server;

    // 主题
    private String topic;

    public NatsProducer() {

    }

    public NatsProducer(String server, String topic) {
        this.topic = topic;
        this.server = server;
    }

    @Override
    public void connect() throws Exception{
        Options o = new Options.Builder().server(server).noReconnect().build();
        this.nc = Nats.connect(o);
        ConsoleUtils.info("[*] NatsProducer[%s, %s] connect success !!!", this.server, this.topic);
    }

    @Override
    public void publish(byte[] message) {
        nc.publish(topic, message);
    }

    @Override
    public void publishString(String message) {
        publish(message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void publishStringList(List<String> messages) {
        for (String message : messages) {
            publishString(message);
        }
    }

    @Override
    public void publishStringArray(String[] messages) {
        for (String message : messages) {
            publishString(message);
        }
    }

    @Override
    public void publishBytesArray(byte[][] message) {
        for (byte[] bytes : message) {
            publish(bytes);
        }
    }

    @Override
    public void publishBytesList(List<byte[]> messages) {
        for (byte[] message : messages) {
            publish(message);
        }
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