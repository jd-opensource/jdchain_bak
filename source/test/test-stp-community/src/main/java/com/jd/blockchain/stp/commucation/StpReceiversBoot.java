/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.StpReceiversBoot
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/18 下午3:44
 * Description:
 */
package com.jd.blockchain.stp.commucation;

import com.jd.blockchain.stp.communication.MessageExecutor;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import com.jd.blockchain.stp.communication.node.LocalNode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/18
 * @since 1.0.0
 */

public class StpReceiversBoot {

    private int[] listenPorts;

    private final String remoteHost = "127.0.0.1";

    private ExecutorService threadPool;

    public StpReceiversBoot(int... ports) {
        listenPorts = ports;
        threadPool = Executors.newFixedThreadPool(ports.length + 2);
    }

    public RemoteSessionManager[] start(MessageExecutor messageExecutor) {

        final int totalSessionSize = listenPorts.length;

        CountDownLatch countDownLatch = new CountDownLatch(totalSessionSize);

        RemoteSessionManager[] sessionManagers = new RemoteSessionManager[totalSessionSize];
        for (int i = 0; i < totalSessionSize; i++) {
            final int port = listenPorts[i], index = i;
            threadPool.execute(() -> {
                // 创建本地节点
                final LocalNode localNode = new LocalNode(remoteHost, port, messageExecutor);
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

        return sessionManagers;
    }


}