package com.jd.blockchain.statetransfer.result;

import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.node.RemoteNode;

/**
 *
 *
 *
 *
 */
public class DSInfoResponseResult {

    long maxHeight;
    RemoteNode maxHeightRemoteNode;

    public DSInfoResponseResult(long maxHeight, RemoteNode maxHeightRemoteNode) {
       this.maxHeight = maxHeight;
       this.maxHeightRemoteNode = maxHeightRemoteNode;
    }

    public long getMaxHeight() {
        return maxHeight;
    }

    public RemoteNode getMaxHeightRemoteNode() {
        return maxHeightRemoteNode;
    }

    public void setMaxHeight(long maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setMaxHeightRemoteNode(RemoteNode maxHeightRemoteNode) {
        this.maxHeightRemoteNode = maxHeightRemoteNode;
    }

}
