/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.CallBackBarrier
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/12 上午10:22
 * Description:
 */
package com.jd.blockchain.stp.communication.callback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 回调栅栏
 * 用于对批量请求的应答回调处理
 * @author shaozhuguang
 * @create 2019/4/12
 * @since 1.0.0
 */

public class CallBackBarrier {

    private CountDownLatch countDownLatch;

    /**
     * 默认最大尝试调用时间（单位：毫秒）
     */
    private long maxTryCallMillSeconds = 2000;


    /**
     * 静态构造器
     * @param barrierLength
     *     请求的远端数量
     * @return
     */
    public static final CallBackBarrier newCallBackBarrier(int barrierLength) {
        return new CallBackBarrier(barrierLength);
    }

    /**
     * 静态构造器
     * @param barrierLength
     *     请求的远端数量
     * @param maxTryCallMillSeconds
     *     最大尝试的时间，单位：毫秒
     * @return
     */
    public static final CallBackBarrier newCallBackBarrier(int barrierLength, long maxTryCallMillSeconds) {
        return new CallBackBarrier(barrierLength, maxTryCallMillSeconds);
    }

    private CallBackBarrier(int barrierLength) {
        this.countDownLatch = new CountDownLatch(barrierLength);
    }

    private CallBackBarrier(int barrierLength, long maxTryCallMillSeconds) {
        this.countDownLatch = new CountDownLatch(barrierLength);
        this.maxTryCallMillSeconds = maxTryCallMillSeconds;
    }

    public void release() {
        countDownLatch.countDown();
    }

    public boolean tryCall() throws InterruptedException {
        return countDownLatch.await(maxTryCallMillSeconds, TimeUnit.MILLISECONDS);
    }

    public boolean tryCall(long timeout, TimeUnit unit) throws InterruptedException {
        return countDownLatch.await(timeout, unit);
    }
}