/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.StpTest
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 下午3:31
 * Description:
 */
package com.jd.blockchain;

import com.jd.blockchain.stp.communication.MessageHandler;
import com.jd.blockchain.stp.communication.RemoteNode;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.RemoteSessionManager;
import com.jd.blockchain.stp.communication.message.LoadMessage;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class StpTest {

    private int listenPort = 6000;

    private int maxWaitTime = 2000;

    private RemoteNode[] remoteNodes = new RemoteNode[3];

    @Before
    public void init() {
        for (int i = 0; i < remoteNodes.length; i++) {
            remoteNodes[i] = new RemoteNode("127.0.0.1", 6001 + i);
        }
    }


    @Test
    public void test() {
        // 创建RemoteSessionManager对象
        RemoteSessionManager sessionManager = new RemoteSessionManager(listenPort);

        // 创建RemoteSession[]对象
        RemoteSession[] remoteSessions = sessionManager.newSessions(remoteNodes);

        // 设置MessageHandler并连接
        for (RemoteSession remoteSession : remoteSessions) {

            // 设置MessageHandler
            remoteSession.initHandler(new StpMessageHandler());

            // session连接
            remoteSession.connect();
        }

        // 生成请求对象
        LoadMessage loadMessage = new StpLoadMessage();

        // 异步发送处理过程

        CountDownLatch countDownLatch = new CountDownLatch(remoteSessions.length);

        // 发送请求至remotes
        LinkedList<Future<byte[]>> responses = new LinkedList<>();
        for (RemoteSession remoteSession : remoteSessions) {
            Future<byte[]> response = remoteSession.asyncSend(loadMessage, countDownLatch);
            responses.addLast(response);
        }

        // 超时判断
        try {
            if (countDownLatch.await(maxWaitTime, TimeUnit.MILLISECONDS)) {
                // 汇总异步消息结果
                LinkedList<byte[]> receiveResponses = new LinkedList<>();
                // 通过迭代器遍历链表
                Iterator<Future<byte[]>> iterator = responses.iterator();
                while (iterator.hasNext()) {
                    Future<byte[]> response = iterator.next();
                    // 判断是否已完成，对于没有完成的直接放弃（因为已经超时）
                    if (response.isDone()) {
                        receiveResponses.addLast(response.get());
                    }
                }

                //TODO 检查汇总后的应答消息，准备开始新一轮的请求




            }
        } catch (Exception e) {

        }
    }


    public static class StpMessageHandler implements MessageHandler {

        @Override
        public void receive(byte[] key, byte[] data, RemoteSession session) {
            // 作为Receiver接收到请求消息后需要发送应答
            // 生成应答消息
            LoadMessage replyLoadMessage = new StpLoadMessage();

            // 发送应答消息（注意key必须保持一致）
            // 异步发送应答消息可使用asyncReply
            session.reply(key, replyLoadMessage);
        }
    }

    public static class StpLoadMessage implements LoadMessage {

        @Override
        public byte[] toBytes() {
            return new byte[0];
        }
    }
}