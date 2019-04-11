package com.jd.blockchain.statetransfer;

import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.RemoteSessionManager;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public class DSProcessManager {

    private Map<String, DSTransferProcess> dSProcessMap = new ConcurrentHashMap<>();
    private RemoteSession[] remoteSessions;

    DSTransferProcess startDSProcess(DataSequenceInfo dsInfo, InetSocketAddress listener, InetSocketAddress[] targets, DataSequenceWriter dsWriter, DataSequenceReader dsReader) {

        RemoteSessionManager remoteSessionManager = new RemoteSessionManager(listener.getPort());
        DSTransferProcess dsTransferProcess =  new DSTransferProcess(dsInfo, remoteSessionManager, targets, dsWriter, dsReader);

        dsTransferProcess.start();
        remoteSessions = dsTransferProcess.getSessions();

       for(RemoteSession session : remoteSessions) {
           dsTransferProcess.send(DSTransferProcess.DataSequenceMsgType.CMD_DSINFO, session);
       }


        dSProcessMap.put(dsInfo.getId(), dsTransferProcess);

        return dsTransferProcess;
    }

    void setDSReader(DataSequenceReader reader) {

    }


}
