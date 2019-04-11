/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.RemoteSession
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午11:15
 * Description:
 */
package com.jd.blockchain.stp.communication;

import com.jd.blockchain.stp.communication.inner.Receiver;
import com.jd.blockchain.stp.communication.inner.Sender;
import com.jd.blockchain.stp.communication.message.LoadMessage;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class RemoteSession {

    private String id;

    private RemoteNode remoteNode;

    private Sender sender;

    private Receiver receiver;

    private MessageHandler messageHandler;

    public void initHandler(MessageHandler messageHandler) {

    }

    public void connect() {

    }

    public byte[] send(LoadMessage loadMessage) {

        return null;
    }

    public Future<byte[]> asyncSend(LoadMessage loadMessage) {
        return null;
    }

    public Future<byte[]> asyncSend(LoadMessage loadMessage, CountDownLatch countDownLatch) {
        return null;
    }

    public byte[] send(byte[] loadMessage) {

        return null;
    }

    public Future<byte[]> asyncSend(byte[] loadMessage) {

        return null;
    }

    public Future<byte[]> asyncSend(byte[] loadMessage, CountDownLatch countDownLatch) {

        return null;
    }

    public void reply(byte[] key, LoadMessage loadMessage) {

    }

    public void asyncReply(byte[] key, LoadMessage loadMessage) {

    }

    public void reply(byte[] key, byte[] loadMessage) {

    }

    public void asyncReply(byte[] key, byte[] loadMessage) {

    }

    public void close() {

    }
}