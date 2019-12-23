/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.config.MsgQueueClientSettings
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/13 下午4:30
 * Description:
 */
package com.jd.blockchain.consensus.mq.settings;

import com.jd.blockchain.consensus.client.ClientSettings;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/13
 * @since 1.0.0
 */

public interface MsgQueueClientSettings extends ClientSettings {

    MsgQueueNetworkSettings getMsgQueueNetworkSettings();
}