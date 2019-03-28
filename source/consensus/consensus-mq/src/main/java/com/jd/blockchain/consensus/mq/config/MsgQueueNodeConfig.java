/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.config.MsgQueueNodeConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 上午11:33
 * Description:
 */
package com.jd.blockchain.consensus.mq.config;

import com.jd.blockchain.consensus.mq.settings.MsgQueueNodeSettings;
import com.jd.blockchain.crypto.PubKey;

/**
 * peer节点IP
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */

public class MsgQueueNodeConfig implements MsgQueueNodeSettings {

    private String address;

    private PubKey pubKey;

    public MsgQueueNodeConfig setAddress(String address) {
        this.address = address;
        return this;
    }

    public MsgQueueNodeConfig setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
        return this;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public PubKey getPubKey() {
        return this.pubKey;
    }
}