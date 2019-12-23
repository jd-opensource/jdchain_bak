/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.nats.RabbitProducer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午10:39
 * Description:
 */
package com.jd.blockchain.consensus.mq.producer;

import com.jd.blockchain.consensus.mq.factory.RabbitFactory;
import com.jd.blockchain.utils.ConsoleUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class RabbitProducer implements MsgQueueProducer {

    // 主要操作时发送JMQ请求
    private Channel channel;

    private Connection connection;

    private String exchangeName;

    private String server;

    public RabbitProducer() {

    }

    public RabbitProducer(String server, String topic) throws Exception {
        this.exchangeName = topic;
        this.server = server;
    }

    @Override
    public void connect() throws Exception {
        ConnectionFactory factory = RabbitFactory.initConnectionFactory(server);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(this.exchangeName, "fanout");
        ConsoleUtils.info("[*] RabbitProducer[%s, %s] connect success !!!", this.server, this.exchangeName);
    }

    @Override
    public void publish(byte[] message) throws Exception {
        channel.basicPublish(this.exchangeName, "", null, message);
    }

    @Override
    public void publishString(String message) throws Exception {
        publish(message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void publishStringList(List<String> messages) throws Exception {
        for (String message : messages) {
            publishString(message);
        }
    }

    @Override
    public void publishStringArray(String[] messages) throws Exception {
        for (String message : messages) {
            publishString(message);
        }
    }

    @Override
    public void publishBytesArray(byte[][] message) throws Exception {
        for (byte[] bytes : message) {
            publish(bytes);
        }
    }

    @Override
    public void publishBytesList(List<byte[]> messages) throws Exception {
        for (byte[] message : messages) {
            publish(message);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


}