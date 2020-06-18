/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.gateway.web.GatewayTimeTasks
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/16 下午6:17
 * Description:
 */
package com.jd.blockchain.gateway.web;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jd.blockchain.gateway.PeerConnector;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/16
 * @since 1.0.0
 */
@Component
@EnableScheduling
public class GatewayLedgerLoadTimer {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GatewayLedgerLoadTimer.class);

    private static final ExecutorService LEDGER_LOAD_EXECUTOR = initLedgerLoadExecutor();

    private static final Lock LOCK = new ReentrantLock();

    @Autowired
    private PeerConnector peerConnector;

    /**
     * 账本加载许可，主要作用两个
     *     1、防止启动时加载账本与当前定时器加载冲突
     *     2、每次加载完成后释放许可，以便于下次定时任务加载，若不存在许可，则下次定时任务放弃执行
     */
    private Semaphore loadSemaphore = new Semaphore(0);

    //每1钟执行一次
    @Scheduled(cron = "0 */1 * * * * ")
    public void ledgerLoad(){
        boolean acquire = false;
        LOCK.lock();
        try {
            // 5秒内获取授权
            acquire = loadSemaphore.tryAcquire(5, TimeUnit.SECONDS);
            if (acquire) {
                // 授权成功的情况下，进行单线程重连
                LEDGER_LOAD_EXECUTOR.execute(() -> {
                    peerConnector.monitorAndReconnect();
                });
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (acquire) {
                // 授权成功的情况下，释放该许可
                release();
            }
            LOCK.unlock();
        }
    }

    /**
     * 释放许可
     */
    public void release() {
        loadSemaphore.release();
    }

    private static ThreadPoolExecutor initLedgerLoadExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("gateway-ledger-loader-%d").build();

        return new ThreadPoolExecutor(1, 1,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }
}