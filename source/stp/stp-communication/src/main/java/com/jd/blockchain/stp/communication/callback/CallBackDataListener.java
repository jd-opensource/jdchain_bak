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
 * 数据回调监听器
 * @author shaozhuguang
 * @create 2019/4/15
 * @since 1.0.0
 */

public class CallBackDataListener {

    /**
     * Future
     */
    private CompletableFuture<byte[]> future = new CompletableFuture<>();

    /**
     * 远端节点
     */
    private RemoteNode remoteNode;

    private boolean isFill = false;

    private Lock lock = new ReentrantLock();


    /**
     * 构造器
     * @param remoteNode
     *     远端节点信息
     */
    public CallBackDataListener(RemoteNode remoteNode) {
        this.remoteNode = remoteNode;
    }

    /**
     * 获取返回的数据
     * 调用该方法会阻塞当前线程，直到有数据返回或出现异常
     * @return
     *     应答结果
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public byte[] getCallBackData() throws InterruptedException, ExecutionException {
        return future.get();
    }

    /**
     * 指定时间内获取返回的数据
     * 调用该方法会阻塞当前线程，直到时间到达或有数据返回或出现异常
     * @param time
     *     超时时间
     * @param timeUnit
     *     超时单位
     * @return
     *     应答结果，若指定时间内没有数据，则返回null
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public byte[] getCallBackData(long time, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(time, timeUnit);
    }

    /**
     * 设置返回的数据
     * @param data
     */
    public void setCallBackData(byte[] data) {
        // 防止数据多次设置
        if (!isFill) {
            try {
                lock.lock();
                // Double Check
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

    /**
     * 判断是否异步操作完成
     * @return
     */
    public boolean isDone() {
        return future.isDone();
    }
}