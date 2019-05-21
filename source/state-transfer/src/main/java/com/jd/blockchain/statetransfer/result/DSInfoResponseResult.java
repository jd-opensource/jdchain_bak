package com.jd.blockchain.statetransfer.result;

import com.jd.blockchain.stp.communication.node.RemoteNode;

/**
 * 数据序列差异请求者解码提供者"CMD_DSINFO_RESPONSE"消息时得到的结果
 * @author zhangshuang
 * @create 2019/4/18
 * @since 1.0.0
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
