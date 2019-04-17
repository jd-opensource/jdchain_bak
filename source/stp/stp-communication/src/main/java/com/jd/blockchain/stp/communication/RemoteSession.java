/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.RemoteSession
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午11:15
 * Description:
 */
package com.jd.blockchain.stp.communication;

import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.connection.Connection;
import com.jd.blockchain.stp.communication.message.LoadMessage;


/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class RemoteSession {

    private String id;

    private Connection connection;

    private MessageExecute messageExecute;

    public RemoteSession(String id, Connection connection, MessageExecute messageExecute) {
        this.id = id;
        this.connection = connection;
        this.messageExecute = messageExecute;
    }

    public void init() {
        connection.initSession(this);
    }

    public void initExecute(MessageExecute messageExecute) {
        this.messageExecute = messageExecute;
    }

    public byte[] request(LoadMessage loadMessage) throws Exception {
        return this.connection.request(this.id, loadMessage, null).getCallBackData();
    }

    public CallBackDataListener asyncRequest(LoadMessage loadMessage) {
        return asyncRequest(loadMessage, null);
    }

    public CallBackDataListener asyncRequest(LoadMessage loadMessage, CallBackBarrier callBackBarrier) {
        return this.connection.request(this.id, loadMessage, callBackBarrier);
    }

    public void reply(String key, LoadMessage loadMessage) {
        this.connection.reply(this.id, key, loadMessage);
    }

    public void closeAll() {
        this.connection.closeAll();
    }

    public void closeReceiver() {
        this.connection.closeReceiver();
    }

    public void closeSender() {
        this.connection.closeSender();
    }

    public String sessionId() {
        return id;
    }

    public MessageExecute messageExecute() {
        return this.messageExecute;
    }
}