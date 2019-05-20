package com.jd.blockchain.statetransfer.process;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
import com.jd.blockchain.statetransfer.comparator.DataSequenceComparator;
import com.jd.blockchain.statetransfer.message.DSDefaultMessageExecutor;
import com.jd.blockchain.statetransfer.result.DSInfoResponseResult;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 * 数据序列状态复制过程管理器
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 *
 */
public class DSProcessManager {

    private static Map<String, DSTransferProcess> dSProcessMap = new ConcurrentHashMap<>();
    private RemoteSession[] remoteSessions;
    private long dsInfoResponseTimeout = 20000;
    private ExecutorService writeExecutors = Executors.newFixedThreadPool(5);
    private int returnCode = 0;

    /**
     * 启动一个指定数据序列的状态复制过程
     * @param dsInfo 数据序列当前状态信息
     * @param listener 本地监听者
     * @param targets 目标结点
     * @param dsWriter 差异请求者执行数据序列更新的执行器
     * @param dsReader 差异响应者执行数据序列读取的执行器
     * @return returnCode 执行结果码
     */
    public int startDSProcess(DataSequenceInfo dsInfo, InetSocketAddress listener, InetSocketAddress[] targets, DataSequenceWriter dsWriter, DataSequenceReader dsReader) {

        // create remote sessions manager， add listener
        LocalNode listenNode = new LocalNode(listener.getHostName(), listener.getPort(), new DSDefaultMessageExecutor(dsReader, dsWriter));

        RemoteSessionManager remoteSessionManager = new RemoteSessionManager(listenNode);

        // data sequence transfer process life cycle start
        DSTransferProcess dsTransferProcess = new DSTransferProcess(dsInfo, targets);
        dsTransferProcess.setDSReader(dsReader);
        dsTransferProcess.setDSWriter(dsWriter);
        dsTransferProcess.setRemoteSessionManager(remoteSessionManager);

        dSProcessMap.put(dsInfo.getId(), dsTransferProcess);

        try {

            //wait all listener nodes start
            Thread.sleep(2000);

            // start network connections with targets
            dsTransferProcess.start();

            //get all target sessions
            remoteSessions = dsTransferProcess.getSessions();

            // async message send process
            CallBackBarrier callBackBarrier = CallBackBarrier.newCallBackBarrier(remoteSessions.length, dsInfoResponseTimeout);

            // response message manage map
            LinkedList<CallBackDataListener> dsInfoResponses = new LinkedList<>();

            System.out.println("Async send CMD_DSINFO_REQUEST msg to targets will start!");
            // step1: send get dsInfo request, then hold
            for (RemoteSession remoteSession : remoteSessions) {
                CallBackDataListener dsInfoResponse = dsTransferProcess.send(DSTransferProcess.DataSequenceMsgType.CMD_DSINFO_REQUEST, remoteSession, 0, 0, callBackBarrier);
                dsInfoResponses.addLast(dsInfoResponse);
            }

            System.out.println("Wait CMD_DSINFO_RESPONSE msg from targets!");
            // step2: collect get dsInfo response
            LinkedList<CallBackDataListener> receiveResponses = new LinkedList<>();
            if (callBackBarrier.tryCall()) {
                Iterator<CallBackDataListener> iterator = dsInfoResponses.iterator();
                while (iterator.hasNext()) {
                    CallBackDataListener receiveResponse = iterator.next();
                    if (receiveResponse.isDone()) {
                        receiveResponses.addLast(receiveResponse);
                    }
                }
            }

            System.out.printf("%s:%d Compute diff info!\r\n", listener.getHostName(), listener.getPort());
            // step3: process received responses
            DSInfoResponseResult diffResult = dsTransferProcess.computeDiffInfo(receiveResponses);

            System.out.printf("%s:%d Diff info result height = %x!\r\n", listener.getHostName(), listener.getPort(), diffResult.getMaxHeight());

            // height diff
            long diff = dsInfo.getHeight() - diffResult.getMaxHeight();

            if (diff == 0 ||  diff > 0) {
                System.out.printf("%s:%d No duplication is required!\r\n", listener.getHostName(), listener.getPort());
                // no duplication is required， life cycle ends
//                dsTransferProcess.close();
                dSProcessMap.remove(dsInfo.getId());
                return returnCode;

            }
            else {
                System.out.printf("%s:%d Duplication is required!\r\n", listener.getHostName(), listener.getPort());
                // step4: async send get data sequence diff request
                // single step get diff
                // async message send process
                CallBackBarrier callBackBarrierDiff = CallBackBarrier.newCallBackBarrier((int)(diffResult.getMaxHeight() - dsInfo.getHeight()), dsInfoResponseTimeout);
                LinkedList<CallBackDataListener> dsDiffResponses = new LinkedList<>();

                RemoteSession responseSession = findResponseSession(diffResult.getMaxHeightRemoteNode(), remoteSessions);
                System.out.println("Async send CMD_GETDSDIFF_REQUEST msg to targets will start!");

                // step5: collect get data sequence diff response
                for (long height = dsInfo.getHeight() + 1; height < diffResult.getMaxHeight() + 1; height++) {
                    CallBackDataListener dsDiffResponse = dsTransferProcess.send(DSTransferProcess.DataSequenceMsgType.CMD_GETDSDIFF_REQUEST, responseSession, height, height, callBackBarrierDiff);
                    dsDiffResponses.addLast(dsDiffResponse);
                }
                // 上述发送不合理，考虑一次性发送请求
                System.out.println("Wait CMD_GETDSDIFF_RESPONSE msg from targets!");
                LinkedList<byte[]> receiveDiffResponses = new LinkedList<>();
                if (callBackBarrierDiff.tryCall()) {
                    for (int i = 0; i < dsDiffResponses.size(); i++) {
                        CallBackDataListener asyncFutureDiff = dsDiffResponses.get(i);
                        if (asyncFutureDiff.isDone()) {
                            receiveDiffResponses.addLast(asyncFutureDiff.getCallBackData());
                        }
                    }
                }

                System.out.printf("%s:%d ReceiveDiffResponses size = %d !\r\n", listener.getHostName(), listener.getPort(), receiveDiffResponses.size());
                // step6: process data sequence diff response, update local data sequence state
                System.out.println("Compute diff elements!");
                ArrayList<DataSequenceElement> dataSequenceElements = dsTransferProcess.computeDiffElement(receiveDiffResponses.toArray(new byte[receiveDiffResponses.size()][]));
                System.out.println("Update local data sequence!");
                Collections.sort(dataSequenceElements, new DataSequenceComparator());
                returnCode = dsWriter.updateDSInfo(dsInfo, dataSequenceElements.toArray(new DataSequenceElement[dataSequenceElements.size()]));

                // data sequence transfer complete, close all sessions, end process life cycle
                System.out.println("Close all sessions");
//                dsTransferProcess.close();
                dSProcessMap.remove(dsInfo.getId());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    /**
     * 根据远端结点找与远端结点建立的会话
     * @param remoteNode 远端结点
     * @param remoteSessions 本地维护的远端结点会话表
     * @return 与远端结点对应的会话
     */
    RemoteSession findResponseSession(RemoteNode remoteNode, RemoteSession[] remoteSessions) {
        for (RemoteSession remoteSession : remoteSessions) {
            if (remoteSession.remoteNode().equals(remoteNode)) {
                return remoteSession;
            }
        }
        return null;
    }
    /**
     *
     *
     */
//    void setDSReader(DataSequenceReader reader) {
//
//    }


}
