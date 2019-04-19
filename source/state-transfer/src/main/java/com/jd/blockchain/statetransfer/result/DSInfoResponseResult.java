package com.jd.blockchain.statetransfer.result;

import com.jd.blockchain.stp.communication.RemoteSession;

/**
 *
 *
 *
 *
 */
public class DSInfoResponseResult {

    long maxHeight;
    RemoteSession maxHeightSession;

    public DSInfoResponseResult(long maxHeight, RemoteSession maxHeightSession) {
       this.maxHeight = maxHeight;
       this.maxHeightSession = maxHeightSession;
    }

    public long getMaxHeight() {
        return maxHeight;
    }

    public RemoteSession getMaxHeightSession() {
        return maxHeightSession;
    }

    public void setMaxHeight(long maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setMaxHeightSession(RemoteSession maxHeightSession) {
        this.maxHeightSession = maxHeightSession;
    }

}
