/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.Connection
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 下午5:39
 * Description:
 */
package com.jd.blockchain.stp.communication.connection;

import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.connection.listener.ReplyListener;
import com.jd.blockchain.stp.communication.message.LoadMessage;
import com.jd.blockchain.stp.communication.message.SessionMessage;
import com.jd.blockchain.stp.communication.message.TransferMessage;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class Connection {

    private RemoteNode remoteNode;

    private Receiver receiver;

    private Sender sender;

    public Connection(Receiver receiver) {
        this.receiver = receiver;
    }

    public void initSession(RemoteSession remoteSession) {
        this.receiver.initRemoteSession(remoteSession.sessionId(), remoteSession);
    }

    public boolean connect(RemoteNode remoteNode, String messageExecuteClass) throws InterruptedException {
        this.remoteNode = remoteNode;
        this.sender = new Sender(this.remoteNode, sessionMessage(messageExecuteClass));
        this.sender.connect();
        return this.sender.waitStarted();
    }

    public CallBackDataListener request(String sessionId, LoadMessage loadMessage, CallBackBarrier callBackBarrier) {

        TransferMessage transferMessage = transferMessage(sessionId, null, loadMessage, TransferMessage.MESSAGE_TYPE.TYPE_REQUEST);

        // 监听器的Key
        String listenKey = transferMessage.toListenKey();

        // 创建监听器
        ReplyListener replyListener = new ReplyListener(listenKey, this.remoteNode, callBackBarrier);

        // 添加监听器至Receiver
        this.receiver.addListener(replyListener);

        // 发送请求
        this.sender.send(transferMessage);

        return replyListener.callBackDataListener();
    }

    public void reply(String sessionId, String key, LoadMessage loadMessage) {
        TransferMessage transferMessage = transferMessage(sessionId, key, loadMessage, TransferMessage.MESSAGE_TYPE.TYPE_RESPONSE);

        // 通过Sender发送数据
        this.sender.send(transferMessage);
    }

    private String loadKey(LoadMessage loadMessage) {
        // 使用Sha256求Hash
        byte[] sha256Bytes = DigestUtils.sha256(loadMessage.toBytes());
        // 使用base64作为Key
        return Base64.encodeBase64String(sha256Bytes);
    }

    private TransferMessage transferMessage(String sessionId, String key, LoadMessage loadMessage, TransferMessage.MESSAGE_TYPE messageType) {

        if (key == null || key.length() == 0) {
            key = loadKey(loadMessage);
        }

        TransferMessage transferMessage = new TransferMessage(
                sessionId, messageType.code(), key, loadMessage.toBytes());

        return transferMessage;
    }

    private SessionMessage sessionMessage(String messageExecuteClass) {

        LocalNode localNode = this.receiver.localNode();

        SessionMessage sessionMessage = new SessionMessage(
                localNode.getHostName(), localNode.getPort(), messageExecuteClass);

        return sessionMessage;
    }

    public void closeAll() {
        closeReceiver();
        closeSender();
    }

    public void closeReceiver() {
        this.receiver.close();
    }

    public void closeSender() {
        this.sender.close();
    }
}