/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.config.MsgQueueBlockConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/13 下午2:57
 * Description:
 */
package com.jd.blockchain.consensus.mq.config;

import com.jd.blockchain.consensus.mq.settings.MsgQueueBlockSettings;

import java.lang.reflect.Method;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/13
 * @since 1.0.0
 */

public class MsgQueueBlockConfig implements MsgQueueBlockSettings {

    private int txSizePerBlock;

    private long maxDelayMilliSecondsPerBlock;

    @Override
    public int getTxSizePerBlock() {
        return txSizePerBlock;
    }

    public MsgQueueBlockConfig setTxSizePerBlock(int txSizePerBlock) {
        this.txSizePerBlock = txSizePerBlock;
        return this;
    }

    @Override
    public long getMaxDelayMilliSecondsPerBlock() {
        return maxDelayMilliSecondsPerBlock;
    }

    public MsgQueueBlockConfig setMaxDelayMilliSecondsPerBlock(long maxDelayMilliSecondsPerBlock) {
        this.maxDelayMilliSecondsPerBlock = maxDelayMilliSecondsPerBlock;
        return this;
    }
}