/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.config.MsgQueueServerSettings
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/13 下午4:39
 * Description:
 */
package com.jd.blockchain.consensus.mq.settings;

import com.jd.blockchain.consensus.service.ServerSettings;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/13
 * @since 1.0.0
 */

public interface MsgQueueServerSettings extends ServerSettings {

    MsgQueueBlockSettings getBlockSettings();

    MsgQueueConsensusSettings getConsensusSettings();
}