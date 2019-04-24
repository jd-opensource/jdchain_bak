package com.jd.blockchain.statetransfer.process;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
import com.jd.blockchain.statetransfer.message.DSMsgResolverFactory;
import com.jd.blockchain.statetransfer.message.DataSequenceLoadMessage;
import com.jd.blockchain.statetransfer.result.DSInfoResponseResult;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import com.jd.blockchain.utils.IllegalDataException;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

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

    /**
     *
     *
     */
    public DSTransferProcess(DataSequenceInfo dsInfo, InetSocketAddress[] targets) {
        this.dsInfo = dsInfo;
        this.targets = targets;
        this.id = dsInfo.getId();
    }

    public void setDSWriter(DataSequenceWriter dsWriter) {
        this.dsWriter = dsWriter;
    }

    public void setDSReader(DataSequenceReader dsReader) {
        this.dsReader = dsReader;
    }

    public void setRemoteSessionManager(RemoteSessionManager remoteSessionManager) {
        this.remoteSessionManager = remoteSessionManager;
    }


    /**
     * get unique id from data sequence transfer process
     *
     */
    public String getId() {
        return id;
    }

    /**
     *
     *
     */
    CallBackDataListener send(DataSequenceMsgType msgType, RemoteSession remoteSession, long fromHeight, long toHeight, CallBackBarrier callBackBarrier) {

        byte[] loadMessage = DSMsgResolverFactory.getEncoder(dsWriter, dsReader).encode(msgType, id, fromHeight, toHeight);

        return remoteSession.asyncRequest(new DataSequenceLoadMessage(loadMessage), callBackBarrier);
    }
    /**
     *
     *
     */
    public ArrayList<DataSequenceElement> computeDiffElement(byte[][] diffArray) {

        ArrayList<DataSequenceElement> dataSequenceElements = new ArrayList<>();

        for (int i = 0 ; i < diffArray.length; i++) {
            Object object = DSMsgResolverFactory.getDecoder(dsWriter, dsReader).decode(diffArray[i]);
            if (object instanceof DataSequenceElement) {
               dataSequenceElements.add((DataSequenceElement) object);
            }
            else {
                throw new IllegalDataException("Unknown instance object!");
            }
        }

        return dataSequenceElements;
    }

    /**
     *
     *
     */
    public DSInfoResponseResult computeDiffInfo(LinkedList<CallBackDataListener> receiveResponses) {
        long maxHeight = 0;
        RemoteNode maxHeightRemoteNode = null;

        System.out.println("ComputeDiffInfo receiveResponses size = "+ receiveResponses.size());

        try {
            for (CallBackDataListener receiveResponse : receiveResponses) {
                Object object = DSMsgResolverFactory.getDecoder(dsWriter, dsReader).decode(receiveResponse.getCallBackData());
//                System.out.println("ComputeDiffInfo object = "+object);
                if (object instanceof DataSequenceInfo) {
                    DataSequenceInfo dsInfo = (DataSequenceInfo) object;
                    long height = dsInfo.getHeight();
//                    System.out.println("ComputeDiffInfo height = " +height);
                    if (maxHeight < height) {
                        maxHeight = height;
                        maxHeightRemoteNode = receiveResponse.remoteNode();
                    }
                }
                else {
                    throw new IllegalDataException("Unknown instance object!");
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return new DSInfoResponseResult(maxHeight, maxHeightRemoteNode);
    }

    /**
     *
     *
     */
    public void getDSInfo(String id) {

    }

    /**
     *
     *
     */
    public RemoteSession[] getSessions() {
        return remoteSessions;
    }

    /**
     * close all sessions
     *
     */
    public void close() {
        for (RemoteSession session : remoteSessions) {
            session.closeAll();
        }
    }

    /**
     * establish connections with target remote nodes
     *
     */
    public void start() {

        RemoteNode[] remoteNodes = new RemoteNode[targets.length];

        for (int i = 0; i < remoteNodes.length; i++) {
            remoteNodes[i] = new RemoteNode(targets[i].getHostName(), targets[i].getPort());
        }

        remoteSessions = remoteSessionManager.newSessions(remoteNodes);
    }


    /**
     * data sequence transfer message type
     *
     */
    public enum DataSequenceMsgType {
        CMD_DSINFO_REQUEST((byte) 0x1),
        CMD_DSINFO_RESPONSE((byte) 0x2),
        CMD_GETDSDIFF_REQUEST((byte) 0x3),
        CMD_GETDSDIFF_RESPONSE((byte) 0x4),
        ;
        public final byte CODE;

        private DataSequenceMsgType(byte code) {
            this.CODE = code;
        }

        public static DataSequenceMsgType valueOf(byte code) {
            for (DataSequenceMsgType msgType : DataSequenceMsgType.values()) {
                if (msgType.CODE == code) {
                    return msgType;
                }
            }
            throw new IllegalArgumentException("Unsupported code[" + code + "] of msgType!");
        }
    }


}
