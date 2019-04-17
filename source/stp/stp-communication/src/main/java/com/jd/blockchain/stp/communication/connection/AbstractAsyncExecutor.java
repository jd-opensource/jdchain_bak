/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.AbstractAsyncExecutor
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/17 上午11:16
 * Description:
 */
package com.jd.blockchain.stp.communication.connection;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/17
 * @since 1.0.0
 */

public abstract class AbstractAsyncExecutor implements AsyncExecutor{

    private static final int QUEUE_CAPACITY = 1024;

    protected final Semaphore isStarted = new Semaphore(0, true);

    protected boolean isStartSuccess = false;

    @Override
    public ThreadPoolExecutor initRunThread() {
        ThreadFactory timerFactory = new ThreadFactoryBuilder()
                .setNameFormat(threadNameFormat()).build();

        ThreadPoolExecutor runThread = new ThreadPoolExecutor(1, 1,
                0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                timerFactory,
                new ThreadPoolExecutor.AbortPolicy());

        return runThread;
    }

    @Override
    public boolean waitStarted() throws InterruptedException {
        this.isStarted.acquire();
        return this.isStartSuccess;
    }

    public abstract String threadNameFormat();
}