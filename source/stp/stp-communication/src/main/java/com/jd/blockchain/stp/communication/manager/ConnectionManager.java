/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.ConnectionManager
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 下午6:11
 * Description:
 */
package com.jd.blockchain.stp.communication.manager;

import com.jd.blockchain.stp.communication.MessageExecute;
import com.jd.blockchain.stp.communication.connection.Receiver;
import com.jd.blockchain.stp.communication.connection.Connection;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class ConnectionManager {

    private static final Map<RemoteNode, Connection> connectionMap = new ConcurrentHashMap<>();

    private static final Map<Integer, ConnectionManager> connectionManagerMap = new ConcurrentHashMap<>();

    private static final Lock managerLock = new ReentrantLock();

    private static final Lock connectionLock = new ReentrantLock();

    private Receiver receiver;

    public static final ConnectionManager newConnectionManager(LocalNode localNode) {
        int listenPort = localNode.getPort();
        if (!connectionManagerMap.containsKey(listenPort)) {
            try {
                managerLock.lock();
                if (!connectionManagerMap.containsKey(listenPort)) {
                    ConnectionManager connectionManager = newInstance(localNode);
                    connectionManagerMap.put(listenPort, connectionManager);
                    return connectionManager;
                }
            } finally {
                managerLock.unlock();
            }
        }
        return connectionManagerMap.get(listenPort);
    }

    private static final ConnectionManager newInstance(LocalNode localNode) {
        return new ConnectionManager(new Receiver(localNode));
    }

    public final boolean start(String messageExecuteClass) throws InterruptedException {
        receiver.initReceiverHandler(this, messageExecuteClass);
        receiver.startListen();
        // 判断是否启动完成，启动完成后再返回
        return receiver.waitStarted();
    }

    private ConnectionManager(Receiver receiver) {
        this.receiver = receiver;
    }

    public Connection connect(RemoteNode remoteNode, MessageExecute messageExecute) {
        return connect(remoteNode, messageExecute.getClass().toString());
    }

    public Connection connect(RemoteNode remoteNode, String messageExecuteClass) {
        if (!connectionMap.containsKey(remoteNode)) {
            try {
                connectionLock.lock();
                if (!connectionMap.containsKey(remoteNode)) {
                    Connection connection = init(remoteNode, messageExecuteClass);
                    connectionMap.put(remoteNode, connection);
                }
            } finally {
                connectionLock.unlock();
            }
        }
        return connectionMap.get(remoteNode);
    }

    private Connection init(RemoteNode remoteNode, String messageExecuteClass) {

        // 初始化Connection
        Connection remoteConnection = new Connection(this.receiver);

        try {
            // 连接远端
            boolean isSuccess = remoteConnection.connect(remoteNode, messageExecuteClass);
            if (!isSuccess) {
                throw new RuntimeException(String.format("RemoteNode {%s} Connect Fail !!!", remoteNode.toString()));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw e;
        }
        return remoteConnection;
    }
}