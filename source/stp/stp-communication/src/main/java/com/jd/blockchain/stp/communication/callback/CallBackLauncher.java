/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.callback.CallBackLauncher
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/17 下午6:27
 * Description:
 */
package com.jd.blockchain.stp.communication.callback;

import java.util.concurrent.Semaphore;

/**
 * 启动器回调
 * @author shaozhuguang
 * @create 2019/4/17
 * @since 1.0.0
 * @date 2019-04-19 09:53
 */

public class CallBackLauncher {

    /**
     * 是否启动成功
     */
    private boolean isBootSuccess = false;

    /**
     * 信号量
     */
    private Semaphore isBooted = new Semaphore(0, true);

    /**
     * 异常
     */
    private Exception exception;

    /**
     * 标识当前启动成功
     */
    public void bootSuccess() {
        isBootSuccess = true;
        release();
    }

    /**
     * 标识当前启动失败
     * @param e
     *     导致失败的异常信息
     */
    public void bootFail(Exception e) {
        this.exception = e;
        isBootSuccess = false;
        release();
    }

    /**
     * 等待启动完成
     * 调用该方法会阻塞当前线程，知道启动完成或发生异常
     * @return
     *     当前对象
     * @throws InterruptedException
     */
    public CallBackLauncher waitingBooted() throws InterruptedException {
        this.isBooted.acquire();
        return this;
    }

    public boolean isBootSuccess() {
        return isBootSuccess;
    }

    public Exception exception() {
        return exception;
    }

    private void release() {
        this.isBooted.release();
    }
}