/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.ExchangeEntityFactory
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午4:08
 * Description:
 */
package com.jd.blockchain.consensus.mq.exchange;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class ExchangeEntityFactory {

    public static ExchangeEventInnerEntity newBlockInstance() {
        return new ExchangeEventInnerEntity(ExchangeType.BLOCK);
    }

    public static ExchangeEventInnerEntity newEmptyInstance() {
        return new ExchangeEventInnerEntity(ExchangeType.EMPTY);
    }

    public static ExchangeEventInnerEntity newTransactionInstance(byte[] content) {
        return new ExchangeEventInnerEntity(ExchangeType.TRANSACTION, content);
    }
}