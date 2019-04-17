/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.listener.ReplyListener
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/12 上午10:36
 * Description:
 */
package com.jd.blockchain.stp.communication.connection.listener;

import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.node.RemoteNode;


/**
 *
 * @author shaozhuguang
 * @create 2019/4/12
 * @since 1.0.0
 */

public class ReplyListener {

    private String listenKey;

    private MANAGE_TYPE manageType = MANAGE_TYPE.REMOVE;

    private CallBackDataListener callBackDataListener;

    private CallBackBarrier callBackBarrier;

    public ReplyListener(String listenKey, RemoteNode remoteNode) {
        this.listenKey = listenKey;
        this.callBackDataListener = new CallBackDataListener(remoteNode);
    }

    public ReplyListener(String listenKey, RemoteNode remoteNode, CallBackBarrier callBackBarrier) {
        this.listenKey = listenKey;
        this.callBackDataListener = new CallBackDataListener(remoteNode);
        this.callBackBarrier = callBackBarrier;
    }

    public void setManageType(MANAGE_TYPE manageType) {
        this.manageType = manageType;
    }

    public String listenKey() {
        return listenKey;
    }

    public CallBackDataListener callBackDataListener() {
        return this.callBackDataListener;
    }

    public void replyData(byte[] reply) {
        // 设置数据
        this.callBackDataListener.setCallBackData(reply);
        if (this.callBackBarrier != null) {
            // 同步释放对应的栅栏
            this.callBackBarrier.release();
        }
    }

    public MANAGE_TYPE manageType() {
        return this.manageType;
    }

    public enum MANAGE_TYPE {
        HOLD,
        REMOVE,
        ;
    }
}