package com.jd.blockchain.statetransfer.process;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;
import com.jd.blockchain.statetransfer.callback.DataSequenceReader;
import com.jd.blockchain.statetransfer.callback.DataSequenceWriter;
import com.jd.blockchain.statetransfer.message.DSDefaultMessageExecutor;
import com.jd.blockchain.statetransfer.result.DSInfoResponseResult;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.utils.concurrent.CompletableAsyncFuture;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Map;
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
    private long dsInfoResponseTimeout = 2000;
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
            // start network connections with targets
            dsTransferProcess.start();

            //get all target sessions
            remoteSessions = dsTransferProcess.getSessions();

            // async message send process
            CallBackBarrier callBackBarrier = CallBackBarrier.newCallBackBarrier(remoteSessions.length, dsInfoResponseTimeout);

            // response message manage map
            Map<RemoteSession, CallBackDataListener> dsInfoResponses = new ConcurrentHashMap<>();

            // step1: send get dsInfo request, then hold
            for (RemoteSession remoteSession : remoteSessions) {

                CallBackDataListener dsInfoResponse = dsTransferProcess.send(DSTransferProcess.DataSequenceMsgType.CMD_DSINFO_REQUEST, remoteSession, 0, 0, callBackBarrier);

                dsInfoResponses.put(remoteSession, dsInfoResponse);
            }

            // step2: collect get dsInfo response
            Map<RemoteSession, byte[]> receiveResponses = new ConcurrentHashMap<>();
            if (callBackBarrier.tryCall()) {
                for (RemoteSession remoteSession : dsInfoResponses.keySet()) {
                    CallBackDataListener  asyncFuture = dsInfoResponses.get(remoteSession);
                    // if really done
                    if (asyncFuture.isDone()) {
                        receiveResponses.put(remoteSession, asyncFuture.getCallBackData());
                    }
                }
            }

            // step3: process received responses
            DSInfoResponseResult diffResult = dsTransferProcess.computeDiffInfo(receiveResponses);

            // height diff
            long diff = dsInfo.getHeight() - diffResult.getMaxHeight();

            if (diff == 0 ||  diff > 0) {
                // no duplication is required， life cycle ends
                dsTransferProcess.close();
                dSProcessMap.remove(dsInfo.getId());
                return returnCode;

            }
            else {

                // step4: async send get data sequence diff request
                // single step get diff
                // async message send process
                CallBackBarrier callBackBarrierDiff = CallBackBarrier.newCallBackBarrier((int)(diffResult.getMaxHeight() - dsInfo.getHeight()), dsInfoResponseTimeout);
                LinkedList<CallBackDataListener> dsDiffResponses = new LinkedList<>();

                // step5: collect get data sequence diff response
                for (long height = dsInfo.getHeight() + 1; height < diffResult.getMaxHeight() + 1; height++) {
                    CallBackDataListener dsDiffResponse = dsTransferProcess.send(DSTransferProcess.DataSequenceMsgType.CMD_GETDSDIFF_REQUEST, diffResult.getMaxHeightSession(), height, height, callBackBarrierDiff);
                    dsDiffResponses.addLast(dsDiffResponse);
                }

                  // 考虑性能
//                writeExecutors.execute(() -> {
//
//                });

                LinkedList<byte[]> receiveDiffResponses = new LinkedList<>();
                if (callBackBarrierDiff.tryCall()) {
                    for (int i = 0; i < dsDiffResponses.size(); i++) {
                        CallBackDataListener asyncFutureDiff = dsDiffResponses.get(i);
                        if (asyncFutureDiff.isDone()) {
                            receiveDiffResponses.addLast(asyncFutureDiff.getCallBackData());
                        }
                    }
                }
                // step6: process data sequence diff response, update local data sequence state
                DataSequenceElement[] dataSequenceElements = dsTransferProcess.computeDiffElement(receiveDiffResponses.toArray(new byte[receiveDiffResponses.size()][]));
                returnCode = dsWriter.updateDSInfo(dsInfo, dataSequenceElements);

                // data sequence transfer complete, close all sessions, end process life cycle

                dsTransferProcess.close();
                dSProcessMap.remove(dsInfo.getId());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }


    /**
     *
     *
     */
    void setDSReader(DataSequenceReader reader) {

    }


}
