/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.RemoteSessionManager
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午11:22
 * Description:
 */
package com.jd.blockchain.stp.communication;

import com.jd.blockchain.stp.communication.inner.Receiver;

import java.net.InetAddress;
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

public class RemoteSessionManager {

    private static Map<Integer, Receiver> receiverMap = new ConcurrentHashMap<>();

    private static final Lock lock = new ReentrantLock();

    public RemoteSessionManager(int listenPort) {
        if (listenPort <= 0) {
            throw new IllegalArgumentException("Illegal port, please check !!!");
        }

        if (!receiverMap.containsKey(listenPort)) {
            try {
                lock.lock();
                if (!receiverMap.containsKey(listenPort)) {
                    Receiver receiver = initReceiver(listenPort);
                    receiverMap.put(listenPort, receiver);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public RemoteSession newSession(RemoteNode remoteNode) {
        return newSession(toRemoteId(remoteNode), remoteNode);
    }

    public RemoteSession newSession(String remoteId, RemoteNode remoteNode) {
        return newSession(remoteId, remoteNode, null);
    }

    public RemoteSession newSession(RemoteNode remoteNode, MessageHandler messageHandler) {
        return newSession(toRemoteId(remoteNode), remoteNode, messageHandler);
    }

    public RemoteSession newSession(String remoteId, RemoteNode remoteNode, MessageHandler messageHandler) {


        return null;
    }

    public RemoteSession[] newSessions(RemoteNode[] remoteNodes) {


        return null;
    }

    public RemoteSession[] newSessions(String[] remoteIds, RemoteNode[] remoteNodes) {

        return null;
    }

    public RemoteSession[] newSessions(RemoteNode[] remoteNodes, MessageHandler messageHandler) {

        return null;
    }

    public RemoteSession[] newSessions(String[] remoteIds, RemoteNode[] remoteNodes, MessageHandler messageHandler) {

        return null;
    }

    public String toRemoteId(RemoteNode remoteNode) {

        return null;
    }

    private Receiver initReceiver(int port) {

        return null;
    }
}