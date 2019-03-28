/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.config.MsgQueueNetworkConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 下午4:55
 * Description:
 */
package com.jd.blockchain.consensus.mq.config;

import com.jd.blockchain.consensus.mq.settings.MsgQueueNetworkSettings;

import java.lang.reflect.Method;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueNetworkConfig implements MsgQueueNetworkSettings {

    private String server;

    private String txTopic;

    private String blTopic;

    private String msgTopic;

    public MsgQueueNetworkConfig setServer(String server) {
        this.server = server;
        return this;
    }

    public MsgQueueNetworkConfig setTxTopic(String txTopic) {
        this.txTopic = txTopic;
        return this;
    }

    public MsgQueueNetworkConfig setBlTopic(String blTopic) {
        this.blTopic = blTopic;
        return this;
    }

    public MsgQueueNetworkConfig setMsgTopic(String msgTopic) {
        this.msgTopic = msgTopic;
        return this;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public String getTxTopic() {
        return txTopic;
    }

    @Override
    public String getBlTopic() {
        return blTopic;
    }

    @Override
    public String getMsgTopic() {
        return msgTopic;
    }
}