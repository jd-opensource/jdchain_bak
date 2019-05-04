/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.mq.config.MsgQueueBlockSettings
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/13 下午4:28
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
 * @create 2018/12/13
 * @since 1.0.0
 */
@DataContract(code = DataCodes.CONSENSUS_MSGQUEUE_BLOCK_SETTINGS)
public interface MsgQueueBlockSettings {

    @DataField(order = 0, primitiveType = PrimitiveType.INT32)
    int getTxSizePerBlock();

    @DataField(order = 1, primitiveType = PrimitiveType.INT64)
    long getMaxDelayMilliSecondsPerBlock();
}