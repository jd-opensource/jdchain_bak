/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.ConnectionManager
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 下午6:11
 * Description:
 */
package com.jd.blockchain.stp.communication.manager;

import com.jd.blockchain.stp.communication.callback.CallBackLauncher;
import com.jd.blockchain.stp.communication.connection.Receiver;
import com.jd.blockchain.stp.communication.connection.Connection;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 连接管理器
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 * @date 2019-04-18 15:11
 */

public class ConnectionManager {

    /**
     * Connection对应Map
     * RemoteNode唯一性：IP（HOST）+PORT
     */
    private static final Map<RemoteNode, Connection> connectionMap = new ConcurrentHashMap<>();

    /**
     * 连接管理器对应MAP
     * 以监听端口（int）作为Key，进行唯一性约束
     */
    private static final Map<Integer, ConnectionManager> connectionManagerMap = new ConcurrentHashMap<>();

    /**
     * connectionManagerMap控制锁
     */
    private static final Lock managerLock = new ReentrantLock();

    /**
     * connectionMap控制锁
     */
    private static final Lock connectionLock = new ReentrantLock();

    /**
     * 当前ConnectionManager对应的Receiver
     */
    private Receiver receiver;

    /**
     * 静态ConnectionManager构造器
     *
     * @param localNode
     *     本地节点
     * @return
     *     优先返回Map中的对象
     */
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

    /**
     * 内部调用的静态构造器
     *
     * @param localNode
     *     本地节点
     * @return
     */
    private static final ConnectionManager newInstance(LocalNode localNode) {
        return new ConnectionManager(new Receiver(localNode));
    }

    /**
     * 启动
     * 该启动是启动Receiver，返回启动的状态
     *
     * @param messageExecutorClass
     *     当前节点希望其他节点收到该节点信息时的处理Handler
     * @return
     *     回调执行器
     * @throws InterruptedException
     */
    public final CallBackLauncher start(String messageExecutorClass) throws InterruptedException {
        receiver.initReceiverHandler(this, messageExecutorClass);
        receiver.startListen();
        // 判断是否启动完成，启动完成后再返回
        return receiver.waitBooted();
    }

    private ConnectionManager(Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     * 连接远端节点
     *
     * @param remoteNode
     *     远端节点信息
     * @param messageExecutorClass
     *     希望远端节点接收到本节点消息时的处理Handler
     * @return
     */
    public Connection connect(RemoteNode remoteNode, String messageExecutorClass) {
        if (!connectionMap.containsKey(remoteNode)) {
            try {
                connectionLock.lock();
                if (!connectionMap.containsKey(remoteNode)) {
                    Connection connection = init(remoteNode, messageExecutorClass);
                    if (connection != null) {
                        // 保证都是连接成功的
                        connectionMap.put(remoteNode, connection);
                        return connection;
                    } else {
                        // 连接失败返回null
                        return null;
                    }
                }
            } finally {
                connectionLock.unlock();
            }
        }
        return connectionMap.get(remoteNode);
    }

    /**
     * 关闭Receiver
     *
     */
    public void closeReceiver() {
        this.receiver.close();
    }

    private Connection init(RemoteNode remoteNode, String messageExecutorClass) {

        // 初始化Connection
        Connection remoteConnection = new Connection(this.receiver);

        try {
            // 连接远端，需要发送当前节点处理的MessageExecuteClass
            CallBackLauncher callBackLauncher = remoteConnection.connect(remoteNode, messageExecutorClass);
            if (!callBackLauncher.isBootSuccess()) {
                // TODO 打印错误日志
                callBackLauncher.exception().printStackTrace();
                return null;
            }
            return remoteConnection;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw e;
        }
    }
}