/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.nats.RabbitFactory
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午10:15
 * Description:
 */
package com.jd.blockchain.consensus.mq.factory;

import com.jd.blockchain.consensus.mq.consumer.MsgQueueConsumer;
import com.jd.blockchain.consensus.mq.consumer.RabbitConsumer;
import com.jd.blockchain.consensus.mq.producer.MsgQueueProducer;
import com.jd.blockchain.consensus.mq.producer.RabbitProducer;
import com.rabbitmq.client.ConnectionFactory;

import static com.jd.blockchain.consensus.mq.factory.MsgQueueConfig.*;


/**
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class RabbitFactory {

    public static MsgQueueProducer newProducer(String server, String topic) throws Exception {
        return new RabbitProducer(server, topic);
    }

    public static MsgQueueConsumer newConsumer(String server, String topic) throws Exception {
        return new RabbitConsumer(server, topic);
    }

    public static ConnectionFactory initConnectionFactory(String server) {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            //amqp协议默认格式：amqp://user:pass@host:10000/vhost ,更多内容参考：https://www.rabbitmq.com/uri-spec.html
            if (server.startsWith(AMQP_PREFIX)) {
                factory.setUri(server);
            } else {
                // 解析server，生成host+port，默认格式：rabbit://localhost:5672
                String[] hostAndPort = server.split("//")[1].split(":");
                if (hostAndPort.length == 1) {
                    factory.setHost(hostAndPort[0]);
                } else {
                    factory.setHost(hostAndPort[0]);
                    factory.setPort(Integer.parseInt(hostAndPort[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Connection RabbitMQ failed！");
        }
        return factory;
    }
}
