/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: BlockEvent
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/20 上午11:32
 * Description:
 */
package com.jd.blockchain.consensus.mq.event;


import com.jd.blockchain.consensus.mq.util.MessageConvertUtil;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/20
 * @since 1.0.0
 */

public class TxBlockedEvent {

    private String txKey;

    private String transaction;

    public TxBlockedEvent() {
    }

    public TxBlockedEvent(String txKey, String transaction) {
        this.txKey = txKey;
        this.transaction = transaction;
    }

    public void setTxKey(String txKey) {
        this.txKey = txKey;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getTxKey() {
        return txKey;
    }

    public String getTransaction() {
        return transaction;
    }

    public byte[] txResponseBytes() {
        if (transaction != null && transaction.length() > 0) {
            // 字符串转字节数组
            return MessageConvertUtil.base64Decode(transaction);
        }
        return null;
    }
}