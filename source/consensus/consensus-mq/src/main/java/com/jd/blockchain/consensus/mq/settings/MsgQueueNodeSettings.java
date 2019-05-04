/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.settings.MsgQueueNodeSettings
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/13 下午4:50
 * Description:
 */
package com.jd.blockchain.consensus.mq.settings;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consensus.NodeSettings;
import com.jd.blockchain.consts.DataCodes;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/13
 * @since 1.0.0
 */

@DataContract(code=DataCodes.CONSENSUS_MSGQUEUE_NODE_SETTINGS)
public interface MsgQueueNodeSettings extends NodeSettings {

}