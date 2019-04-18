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
import com.jd.blockchain.stp.communication.callback.CallBackLauncher;

import java.util.concurrent.*;

/**
 * 抽象异步执行器
 * @author shaozhuguang
 * @create 2019/4/17
 * @since 1.0.0
 */

public abstract class AbstractAsyncExecutor implements AsyncExecutor{

    /**
     * 线程池可处理队列的容量
     */
    private static final int QUEUE_CAPACITY = 1024;

    /**
     * 回调执行器
     */
    protected final CallBackLauncher callBackLauncher = new CallBackLauncher();

    /**
     * 默认提供的初始化活跃线程调度器
     * @return
     */
    @Override
    public ThreadPoolExecutor initRunThread() {
        ThreadFactory timerFactory = new ThreadFactoryBuilder()
                .setNameFormat(threadNameFormat()).build();

        ThreadPoolExecutor runThread = new ThreadPoolExecutor(1, 1,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                timerFactory,
                new ThreadPoolExecutor.AbortPolicy());

        return runThread;
    }

    /**
     * 启动完成后回调
     * 该调用会阻塞当前线程，直到启动完成，无论是成功或失败
     * @return
     *     回调执行器
     *     成功或失败会在回调执行器中有所体现
     * @throws InterruptedException
     */
    @Override
    public CallBackLauncher waitBooted() throws InterruptedException {
        return callBackLauncher.waitingBooted();
    }

    /**
     * 线程池中的线程命名格式
     * @return
     */
    public abstract String threadNameFormat();
}