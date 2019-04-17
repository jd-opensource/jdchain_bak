/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.RemoteSessionManager
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午11:22
 * Description:
 */
package com.jd.blockchain.stp.communication.manager;


import com.jd.blockchain.stp.communication.MessageExecute;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.connection.Connection;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import org.apache.commons.codec.binary.Hex;


/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class RemoteSessionManager {

    private ConnectionManager connectionManager;

    private LocalNode localNode;

    public RemoteSessionManager(LocalNode localNode) {
        this.localNode = localNode;
        check();
        this.connectionManager = ConnectionManager.newConnectionManager(this.localNode);
        try {
            boolean isStartedSuccess = start(this.localNode.messageExecuteClass());
            if (!isStartedSuccess) {
                throw new RuntimeException(String.format("LocalNode {%s} Start Receiver Fail !!!", this.localNode.toString()));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw e;
        }

    }

    private void check() {
        // 要求端口范围：1~65535，messageExecuteClass不能为null
        int listenPort = this.localNode.getPort();
        if (listenPort <= 0 || listenPort > 65535) {
            throw new IllegalArgumentException("Illegal Local Listen Port, Please Check !!!");
        }

        String messageExecuteClass = this.localNode.messageExecuteClass();
        if (messageExecuteClass == null) {
            throw new IllegalArgumentException("Illegal MessageExecute Class, Please Check !!!");
        }
    }

    private boolean start(String messageExecuteClass) throws InterruptedException {
        return this.connectionManager.start(messageExecuteClass);
    }

    public RemoteSession newSession(RemoteNode remoteNode) {
        return newSession(null, remoteNode);
    }

    public RemoteSession newSession(String sessionId, RemoteNode remoteNode) {
        return newSession(sessionId, remoteNode, null);
    }

    public RemoteSession newSession(RemoteNode remoteNode, MessageExecute messageExecute) {
        return newSession(null, remoteNode, messageExecute);
    }

    public RemoteSession newSession(String sessionId, RemoteNode remoteNode, MessageExecute messageExecute) {
        if (sessionId == null) {
            sessionId = toSessionId(localNode);
        }
        Connection remoteConnection = this.connectionManager.connect(remoteNode, localNode.messageExecuteClass());

        RemoteSession remoteSession = new RemoteSession(sessionId, remoteConnection, messageExecute);

        remoteSession.init();

        return remoteSession;
    }

    public RemoteSession[] newSessions(RemoteNode[] remoteNodes) {
        return newSessions(null, remoteNodes);
    }

    public RemoteSession[] newSessions(String[] sessionIds, RemoteNode[] remoteNodes) {

        return newSessions(sessionIds, remoteNodes, null);
    }

    public RemoteSession[] newSessions(RemoteNode[] remoteNodes, MessageExecute messageExecute) {

        return newSessions(null, remoteNodes, messageExecute);
    }

    public RemoteSession[] newSessions(String[] sessionIds, RemoteNode[] remoteNodes, MessageExecute messageExecute) {
        if (remoteNodes == null || remoteNodes.length <= 0) {
            throw new IllegalArgumentException("RemoteNodes is empty !!!");
        }

        if (sessionIds != null) {
            if (sessionIds.length != remoteNodes.length) {
                throw new IllegalArgumentException("RemoteNodes and sessionIds are different in length !!!");
            }
        }

        RemoteSession[] remoteSessions = new RemoteSession[remoteNodes.length];

        for (int i = 0; i < remoteNodes.length; i++) {
            if (sessionIds == null) {
                remoteSessions[i] = newSession(remoteNodes[i], messageExecute);
            } else {
                remoteSessions[i] = newSession(sessionIds[i], remoteNodes[i], messageExecute);
            }
        }
        return remoteSessions;
    }

    public String toSessionId(RemoteNode remoteNode) {
        return Hex.encodeHexString(remoteNode.toString().getBytes());
    }
}