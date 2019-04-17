/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.AsyncExecutor
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/17 上午11:14
 * Description:
 */
package com.jd.blockchain.stp.communication.connection;

import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/17
 * @since 1.0.0
 */

public interface AsyncExecutor {

    ThreadPoolExecutor initRunThread();

    boolean waitStarted() throws InterruptedException;
}