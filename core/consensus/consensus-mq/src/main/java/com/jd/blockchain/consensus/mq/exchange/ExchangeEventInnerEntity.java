/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.ExchangeEventInnerEntity
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午4:04
 * Description:
 */
package com.jd.blockchain.consensus.mq.exchange;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class ExchangeEventInnerEntity {

    private ExchangeType type;

    private byte[] content;

    public ExchangeEventInnerEntity() {
    }

    public ExchangeEventInnerEntity(ExchangeType type) {
        this.type = type;
    }

    public ExchangeEventInnerEntity(ExchangeType type, byte[] content) {
        this.type = type;
        this.content = content;
    }



    public ExchangeType getType() {
        return type;
    }

    public void setType(ExchangeType type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}