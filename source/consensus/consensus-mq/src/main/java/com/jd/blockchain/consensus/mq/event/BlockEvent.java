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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/20
 * @since 1.0.0
 */

public class BlockEvent {

    private Map<String, String> txMap = new HashMap<>();

    public Map<String, String> getTxMap() {
        return txMap;
    }

    public void setTxMap(Map<String, String> txMap) {
        this.txMap = txMap;
    }

    public void put(String txKey, String txResp) {
        txMap.put(txKey, txResp);
    }

    public void put(String txKey, byte[] txResp) {
        put(txKey, MessageConvertUtil.base64Encode(txResp));
    }

    public String getTxResp(String txKey) {
        return txMap.get(txKey);
    }

    public byte[] getTxRespBytes(String txKey) {
        String txResp = getTxResp(txKey);
        if (txResp != null && txResp.length() > 0) {
            // 字符串转字节数组
            return MessageConvertUtil.base64Decode(txResp);
        }
        return null;
    }

    public boolean containTxResp(String txKey) {
        return txMap.containsKey(txKey);
    }

    public boolean isEmpty() {
        if (txMap == null) return true;
        return txMap.isEmpty();
    }
}