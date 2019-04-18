package com.jd.blockchain.statetransfer;

import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import com.jd.blockchain.stp.communication.node.RemoteNode;

import java.net.InetSocketAddress;

/**
 *
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public class DSTransferProcess {

    private InetSocketAddress[] targets;
    private DataSequenceWriter dsWriter;
    private DataSequenceReader dsReader;
    private DataSequenceInfo dsInfo;
    private RemoteSessionManager remoteSessionManager;
    private RemoteSession[] remoteSessions;
    private String id;


    public DSTransferProcess(DataSequenceInfo dsInfo, RemoteSessionManager remoteSessionManager, InetSocketAddress[] targets, DataSequenceWriter dsWriter, DataSequenceReader dsReader) {

        this.dsInfo = dsInfo;
        this.targets = targets;
        this.dsWriter = dsWriter;
        this.dsReader = dsReader;
        this.remoteSessionManager = remoteSessionManager;
        this.id = dsInfo.getId();

    }

    void send(DataSequenceMsgType msgType, RemoteSession session) {

        //session.send();

    }

    byte[] createMsg(DataSequenceMsgType msgType) {
        return null;
    }

    public void computeDiff() {
        //todo
    }

    public void getDSInfo(String id) {
        //todo
    }

    public RemoteSession[] getSessions() {
        //todo
        return remoteSessions;
    }

    public void start() {

        RemoteNode[] remoteNodes = new RemoteNode[targets.length];

        for (int i = 0; i< remoteNodes.length; i++) {
            remoteNodes[i] = new RemoteNode(targets[i].getHostName(), targets[i].getPort());
        }

        remoteSessions = remoteSessionManager.newSessions(remoteNodes);

        for (int i = 0; i < remoteSessions.length; i++) {
            DataSequenceMsgHandle msgHandle = new DataSequenceMsgHandle(dsReader, dsWriter);
            remoteSessions[i].initExecutor(msgHandle);
            remoteSessions[i].init();
        }
    }

    enum DataSequenceMsgType {

        CMD_DSINFO,
        CMD_GETDSDIFF
    }


}
