/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.CallBackBarrier
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/12 上午10:22
 * Description:
 */
package com.jd.blockchain.stp.communication.callback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/12
 * @since 1.0.0
 */

public class CallBackBarrier {

    private CountDownLatch countDownLatch;

    private long maxTryCallMillSeconds = 2000;

    public static final CallBackBarrier newCallBackBarrier(int barrierLength) {
        return new CallBackBarrier(barrierLength);
    }

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