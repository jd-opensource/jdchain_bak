/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.callback.CallBackDataListener
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/15 下午4:40
 * Description:
 */
package com.jd.blockchain.stp.communication.callback;

import com.jd.blockchain.stp.communication.node.RemoteNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/15
 * @since 1.0.0
 */

public class CallBackDataListener {

    private CompletableFuture<byte[]> future = new CompletableFuture<>();

    private RemoteNode remoteNode;

    private boolean isFill = false;

    private Lock lock = new ReentrantLock();

    public CallBackDataListener(RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }

    public byte[] getCallBackData() throws InterruptedException, ExecutionException {
        return future.get();
    }

    public byte[] getCallBackData(long time, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(time, timeUnit);
    }

    public void setCallBackData(byte[] data) {
        if (!isFill) {
            try {
                lock.lock();
                if (!isFill) {
                    future.complete(data);
                    isFill = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public RemoteNode remoteNode() {
        return this.remoteNode;
    }

    public boolean isDone() {
        return future.isDone();
    }
}