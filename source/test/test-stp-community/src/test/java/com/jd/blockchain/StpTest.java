/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.StpTest
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 下午3:31
 * Description:
 */
package com.jd.blockchain;

import com.jd.blockchain.stp.commucation.MyMessageExecutor;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import com.jd.blockchain.stp.communication.message.LoadMessage;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertNull;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class StpTest {

    private int maxWaitTime = 2000;

    private final String remoteHost = "127.0.0.1";

    private final int localPort = 9001;

    private final int[] listenPorts = new int[]{9001, 9002, 9003, 9004};

    private final RemoteSessionManager[] sessionManagers = new RemoteSessionManager[listenPorts.length];

    private final ExecutorService threadPool = Executors.newFixedThreadPool(6);

    private RemoteSession[] remoteSessions;

    @Before
    public void init() {

        System.out.println("---------- listenStart -----------");
        listenStart();
        System.out.println("---------- listenComplete -----------");
        System.out.println("---------- ConnectionStart ----------");
        connectOneOther();
        System.out.println("---------- ConnectionComplete ----------");
    }

    private void listenStart() {
        CountDownLatch countDownLatch = new CountDownLatch(listenPorts.length);

        for (int i = 0; i < listenPorts.length; i++) {
            final int port = listenPorts[i], index = i;
            threadPool.execute(() -> {
                // 创建本地节点
                final LocalNode localNode = new LocalNode(remoteHost, port, new MyMessageExecutor());
                try {
                    // 启动当前节点
                    RemoteSessionManager sessionManager = new RemoteSessionManager(localNode);
                    sessionManagers[index] = sessionManager;
                    System.out.printf("Current Node {%s} start success !!! \r\n", localNode.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        // 等待所有节点启动完成
        try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectAllOthers() {
        // 所有节点完成之后，需要启动
        // 启动一个节点
        RemoteSessionManager starter = sessionManagers[0];

        // 当前节点需要连接到其他3个节点
        RemoteNode[] remoteNodes = new RemoteNode[listenPorts.length - 1];
        int index = 0;
        for (int port : listenPorts) {
            if (port != localPort) {
                remoteNodes[index++] = new RemoteNode(remoteHost, port);
            }
        }

        remoteSessions = starter.newSessions(remoteNodes);
    }

    private void connectOneOther() {
        // 所有节点完成之后，需要启动
        // 启动一个节点
        RemoteSessionManager starter = sessionManagers[0];

        // 当前节点需要连接到其他3个节点
        RemoteNode[] remoteNodes = new RemoteNode[1];
        int index = 0;
        for (int port : listenPorts) {
            if (port != localPort && index < 1) {
                remoteNodes[index++] = new RemoteNode(remoteHost, port);
            }
        }

        remoteSessions = starter.newSessions(remoteNodes);
    }

    private void connectOneErrorNode() {
        // 所有节点完成之后，需要启动
        // 启动一个节点
        RemoteSessionManager starter = sessionManagers[0];

        // 当前节点需要连接到其他3个节点
        RemoteNode[] remoteNodes = new RemoteNode[1];

        remoteNodes[0] = new RemoteNode(remoteHost, 10001);

        remoteSessions = starter.newSessions(remoteNodes);

        assertNull(remoteSessions);
    }


    @Test
    public void test() {

        try {
            Thread.sleep(3000);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 生成请求对象
        LoadMessage loadMessage = new StpLoadMessage(remoteHost + ":" + localPort);

        // 异步发送处理过程
        CallBackBarrier callBackBarrier = CallBackBarrier.newCallBackBarrier(remoteSessions.length, 10000);

        // 发送请求至remotes
        LinkedList<CallBackDataListener> responses = new LinkedList<>();
        for (RemoteSession remoteSession : remoteSessions) {
            CallBackDataListener response = remoteSession.asyncRequest(loadMessage, callBackBarrier);
            responses.addLast(response);
        }

        // 超时判断
        try {
            if (callBackBarrier.tryCall()) {

                // 说明结果已经全部返回
                // 打印出所有的结果
                // 通过迭代器遍历链表
                Iterator<CallBackDataListener> iterator = responses.iterator();
                while (iterator.hasNext()) {
                    CallBackDataListener response = iterator.next();
                    // 判断是否已完成，对于没有完成的直接放弃（因为已经超时）
                    if (response.isDone()) {
                        System.out.printf("Receive Response {%s} {%s} \r\n",
                                response.remoteNode().toString(), new String(response.getCallBackData()));
                    }
                }
            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static class StpLoadMessage implements LoadMessage {

        private String localInfo;

        public StpLoadMessage(String localInfo) {
            this.localInfo = localInfo;
        }

        @Override
        public byte[] toBytes() {
            String msg = localInfo + " -> Send !!!";
            return msg.getBytes(Charset.defaultCharset());
        }
    }
}