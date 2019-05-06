/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.mq.config.MsgQueueNetworkSettings
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/12 上午11:43
 * Description:
 */
package com.jd.blockchain.consensus.mq.settings;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/12
 * @since 1.0.0
 */
@DataContract(code = DataCodes.CONSENSUS_MSGQUEUE_NETWORK_SETTINGS)
public interface MsgQueueNetworkSettings {

    @DataField(order = 0, primitiveType = PrimitiveType.TEXT)
    String getServer();

    @DataField(order = 1, primitiveType = PrimitiveType.TEXT)
    String getTxTopic();

    @DataField(order = 2, primitiveType = PrimitiveType.TEXT)
    String getBlTopic();

    @DataField(order = 3, primitiveType = PrimitiveType.TEXT)
    String getMsgTopic();
}