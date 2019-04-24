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
import com.jd.blockchain.utils.concurrent.CompletableAsyncFuture;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public class DSProcessManager {

    private static Map<String, DSTransferProcess> dSProcessMap = new ConcurrentHashMap<>();
    private RemoteSession[] remoteSessions;
    private long dsInfoResponseTimeout = 20000;
    private ExecutorService writeExecutors = Executors.newFixedThreadPool(5);
    private int returnCode = 0;
    /**
     *
     *
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
            Thread.sleep(10000);

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

            System.out.println("Compute diff info!");
            // step3: process received responses
            DSInfoResponseResult diffResult = dsTransferProcess.computeDiffInfo(receiveResponses);

            System.out.println("Diff info result height = " + diffResult.getMaxHeight() + "!");

            // height diff
            long diff = dsInfo.getHeight() - diffResult.getMaxHeight();

            if (diff == 0 ||  diff > 0) {
                System.out.println("No duplication is required!");
                // no duplication is required， life cycle ends
//                dsTransferProcess.close();
                dSProcessMap.remove(dsInfo.getId());
                return returnCode;

            }
            else {
                System.out.println("Duplication is required!");
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

                  // 考虑性能
//                writeExecutors.execute(() -> {
//
//                });

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

                System.out.println("ReceiveDiffResponses size =  "+ receiveDiffResponses.size());
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
    void setDSReader(DataSequenceReader reader) {

    }


}
