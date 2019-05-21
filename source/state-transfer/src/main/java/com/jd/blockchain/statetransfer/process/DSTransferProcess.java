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
 * 数据序列状态复制过程
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
     * @param dsInfo 数据序列当前状态信息
     * @param targets 目标结点
     */
    public DSTransferProcess(DataSequenceInfo dsInfo, InetSocketAddress[] targets) {
        this.dsInfo = dsInfo;
        this.targets = targets;
        this.id = dsInfo.getId();
    }

    /**
     * @param dsWriter 差异请求者执行数据序列更新的执行器
     * @return void
     */
    public void setDSWriter(DataSequenceWriter dsWriter) {
        this.dsWriter = dsWriter;
    }

    /**
     * @param dsReader 差异响应者执行数据序列读取的执行器
     * @return void
     */
    public void setDSReader(DataSequenceReader dsReader) {
        this.dsReader = dsReader;
    }

    /**
     * @param remoteSessionManager 远端会话管理器
     * @return void
     */
    public void setRemoteSessionManager(RemoteSessionManager remoteSessionManager) {
        this.remoteSessionManager = remoteSessionManager;
    }


    /**
     *
     * @return 数据序列标识符
     */
    public String getId() {
        return id;
    }

    /**
     * @param msgType 数据序列差异请求消息类型
     * @param remoteSession 目标结点对应的会话
     * @param fromHeight 差异起始高度
     * @param toHeight 差异结束高度
     * @param callBackBarrier 异步回调
     * @return 异步回调
     */
    CallBackDataListener send(DataSequenceMsgType msgType, RemoteSession remoteSession, long fromHeight, long toHeight, CallBackBarrier callBackBarrier) {

        byte[] loadMessage = DSMsgResolverFactory.getEncoder(dsWriter, dsReader).encode(msgType, id, fromHeight, toHeight);

        return remoteSession.asyncRequest(new DataSequenceLoadMessage(loadMessage), callBackBarrier);
    }

    /**
     * 计算数据序列差异元素数组
     * @param diffArray 差异的字节数组
     * @return 对差异字节数组的解码结果
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
     * 根据差异提供者响应的数据序列状态信息找到拥有最大数据序列高度的远端结点
     * @param receiveResponses 数据序列差异请求者收到的远端结点状态的响应信息
     * @return 得到远端数据序列的最大高度以及拥有者结点
     */
    public DSInfoResponseResult computeDiffInfo(LinkedList<CallBackDataListener> receiveResponses) {
        long maxHeight = 0;
        RemoteNode maxHeightRemoteNode = null;

        System.out.println("ComputeDiffInfo receiveResponses size = "+ receiveResponses.size());

        try {
            for (CallBackDataListener receiveResponse : receiveResponses) {
                Object object = DSMsgResolverFactory.getDecoder(dsWriter, dsReader).decode(receiveResponse.getCallBackData());
                if (object instanceof DataSequenceInfo) {
                    DataSequenceInfo dsInfo = (DataSequenceInfo) object;
                    long height = dsInfo.getHeight();
                    // sava max height and its remote node
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
     * 获取本复制过程维护的远端会话表
     * @param
     * @return 远端会话表数组
     */
    public RemoteSession[] getSessions() {
        return remoteSessions;
    }

    /**
     * 关闭本复制过程维护的所有远端会话
     * @return void
     */
    public void close() {
        for (RemoteSession session : remoteSessions) {
            session.closeAll();
        }
    }

    /**
     * 建立与远端目标结点的连接，产生本地维护的远端会话表
     * @return void
     */
    public void start() {

        RemoteNode[] remoteNodes = new RemoteNode[targets.length];

        for (int i = 0; i < remoteNodes.length; i++) {
            remoteNodes[i] = new RemoteNode(targets[i].getHostName(), targets[i].getPort());
        }

        remoteSessions = remoteSessionManager.newSessions(remoteNodes);
    }


    /**
     * 数据序列状态传输使用的消息类型
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
