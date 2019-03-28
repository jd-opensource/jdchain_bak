/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.nats.RabbitConsumer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午10:40
 * Description:
 */
package com.jd.blockchain.consensus.mq.consumer;

import com.jd.blockchain.consensus.mq.factory.RabbitFactory;
import com.jd.blockchain.utils.ConsoleUtils;
import com.lmax.disruptor.EventHandler;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class RabbitConsumer extends AbstractConsumer implements MsgQueueConsumer {

    private Connection connection;

    private Channel channel;

    private String exchangeName;

    private String server;

    private String queueName;

    public RabbitConsumer(String server, String topic) {
        this.server = server;
        this.exchangeName = topic;
    }

    private void rabbitConsumerHandle() throws Exception {
        rabbitConsumerHandleByQueue();
    }

    private void rabbitConsumerHandleByQueue() throws IOException {
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                // 此处将收到的消息加入队列即可
                try {
                    eventProducer.publish(body);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        this.channel.basicConsume(this.queueName, false, consumer);
    }

    @Override
    public void connect(EventHandler eventHandler) throws Exception {
        initEventHandler(eventHandler);
        ConnectionFactory factory = RabbitFactory.initConnectionFactory(server);
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(this.exchangeName, "fanout");
        queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, this.exchangeName, "");
        channel.basicQos(8);

        ConsoleUtils.info("[*] RabbitConsumer[%s, %s] connect success !!!", this.server, this.exchangeName);
    }

    @Override
    public void start() throws Exception {
        rabbitConsumerHandle();
    }

    @Override
    public void close() throws IOException {
        try {
            this.channel.close();
            this.connection.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}