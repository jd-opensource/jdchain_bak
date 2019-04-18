/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.StpSenderTest
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/18 下午3:56
 * Description:
 */
package com.jd.blockchain;

import com.jd.blockchain.stp.commucation.MyMessageExecutor;
import com.jd.blockchain.stp.commucation.StpReceiversBoot;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import com.jd.blockchain.stp.communication.message.LoadMessage;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/18
 * @since 1.0.0
 */

public class StpSenderTest {

    // 本地的端口
    private static final int localPort = 9800;

    // 连接的远端端口集合
    private static final int[] remotePorts = StpReceiversBootTest.localPorts;

    // 本地节点信息
    private static final String localHost = "127.0.0.1";

    @Test
    public void test() {
        // 首先启动本地节点
        StpReceiversBoot stpReceiversBoot = new StpReceiversBoot(localPort);
        RemoteSessionManager[] sessionManagers = stpReceiversBoot.start(new MyMessageExecutor());

        // 本地节点启动完成后
        if (sessionManagers != null && sessionManagers.length > 0) {
            RemoteSessionManager localSessionManager = sessionManagers[0];

            // 连接远端的两个节点
            RemoteNode[] remoteNodes = new RemoteNode[]{
                    new RemoteNode(localHost, remotePorts[0]),
                    new RemoteNode(localHost, remotePorts[1])
            };

            RemoteSession[] remoteSessions = localSessionManager.newSessions(remoteNodes);

            // 生成请求对象
            LoadMessage loadMessage = new StpTest.StpLoadMessage(localHost + ":" + localPort);

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
    }
}