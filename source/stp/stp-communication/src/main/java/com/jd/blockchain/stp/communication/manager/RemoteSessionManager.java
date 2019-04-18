/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.RemoteSessionManager
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午11:22
 * Description:
 */
package com.jd.blockchain.stp.communication.manager;


import com.jd.blockchain.stp.communication.MessageExecutor;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.callback.CallBackLauncher;
import com.jd.blockchain.stp.communication.connection.Connection;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 远端Session管理器
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class RemoteSessionManager {

    /**
     * 可监听的最大端口
     */
    private static final int MAX_PORT = 65535;

    /**
     * 节点Session的集合信息
     */
    private Map<RemoteNode, RemoteSession> nodeRemoteSessionMap = new ConcurrentHashMap<>();

    /**
     * nodeRemoteSessionMap的控制锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * 连接管理器
     * 用于管理底层的通信连接
     */
    private ConnectionManager connectionManager;

    /**
     * 本地节点信息
     */
    private LocalNode localNode;

    /**
     * 构造器
     * @param localNode
     *     本地节点信息
     */
    public RemoteSessionManager(LocalNode localNode) {
        this.localNode = localNode;
        // 校验本地节点的配置，防止异常
        check();
        this.connectionManager = ConnectionManager.newConnectionManager(this.localNode);
        try {
            CallBackLauncher callBackLauncher = start();
            if (!callBackLauncher.isBootSuccess()) {
                // 启动当前端口连接必须要成功，否则则退出，交由应用程序处理
                throw new RuntimeException(callBackLauncher.exception());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * 生成新的Session
     * @param remoteNode
     * @return
     */
    public RemoteSession newSession(RemoteNode remoteNode) {
        return newSession(null, remoteNode);
    }

    /**
     * RemoteSession对象生成器
     * @param sessionId
     *     RemoteSession的Key
     * @param remoteNode
     *     远端节点信息
     * @return
     */
    public RemoteSession newSession(String sessionId, RemoteNode remoteNode) {

        RemoteSession remoteSession = nodeRemoteSessionMap.get(remoteNode);

        if (remoteSession != null) {
            return remoteSession;
        } else {
            try {
                lock.lock();

                // Double Check !!!
                if (!nodeRemoteSessionMap.containsKey(remoteNode)) {
                    if (sessionId == null) {
                        sessionId = sessionId(localNode);
                    }
                    Connection remoteConnection = this.connectionManager.connect(remoteNode, localNode.messageExecutorClass());

                    if (remoteConnection == null) {
                        return null;
                    }

                    remoteSession = new RemoteSession(sessionId, remoteConnection);

                    remoteSession.init();

                    nodeRemoteSessionMap.put(remoteNode, remoteSession);

                    return remoteSession;
                }
            } finally {
                lock.unlock();
            }
        }
        return remoteSession;
    }

    public RemoteSession[] newSessions(RemoteNode[] remoteNodes) {

        return newSessions(null, remoteNodes);
    }

    public RemoteSession[] newSessions(String[] sessionIds, RemoteNode[] remoteNodes) {
        checkSessions(sessionIds, remoteNodes);

        List<RemoteSession> remoteSessionList = new ArrayList<>();

        for (int i = 0; i < remoteNodes.length; i++) {
            RemoteSession remoteSession;
            if (sessionIds == null) {
                remoteSession = newSession(remoteNodes[i]);
            } else {
                remoteSession = newSession(sessionIds[i], remoteNodes[i]);
            }
            if (remoteSession != null) {
                remoteSessionList.add(remoteSession);
            }
        }

        if (remoteSessionList.isEmpty()) {
            return null;
        }

        RemoteSession[] remoteSessions = new RemoteSession[remoteSessionList.size()];

        return remoteSessionList.toArray(remoteSessions);
    }

    private void check() {
        // 要求端口范围：1~65535，messageExecuteClass不能为null
        int listenPort = this.localNode.getPort();
        if (listenPort <= 0 || listenPort > MAX_PORT) {
            throw new IllegalArgumentException("Illegal Local Listen Port, Please Check !!!");
        }

        // 默认处理器必须包含，可不包含本机需要对端知晓的处理器
        MessageExecutor defaultMessageExecutor = this.localNode.defaultMessageExecutor();
        if (defaultMessageExecutor == null) {
            throw new IllegalArgumentException("Illegal Default MessageExecutor, Please Check !!!");
        }
    }

    private CallBackLauncher start() throws InterruptedException {
        return this.connectionManager.start(this.localNode.messageExecutorClass());
    }

    private void checkSessions(String[] sessionIds, RemoteNode[] remoteNodes) {
        if (remoteNodes == null || remoteNodes.length <= 0) {
            throw new IllegalArgumentException("RemoteNodes is empty !!!");
        }

        if (sessionIds != null) {
            if (sessionIds.length != remoteNodes.length) {
                throw new IllegalArgumentException("RemoteNodes and sessionIds are different in length !!!");
            }
        }
    }

    private String sessionId(RemoteNode remoteNode) {
        return Hex.encodeHexString(remoteNode.toString().getBytes());
    }
}