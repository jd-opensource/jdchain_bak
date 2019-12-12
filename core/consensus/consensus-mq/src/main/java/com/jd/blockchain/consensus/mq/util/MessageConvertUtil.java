/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.mq.event.MessageConvertUtil
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/21 下午7:28
 * Description:
 */
package com.jd.blockchain.consensus.mq.util;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.consensus.mq.event.BlockEvent;
import com.jd.blockchain.consensus.mq.event.TxBlockedEvent;
import com.jd.blockchain.utils.security.ShaUtils;

import org.springframework.util.Base64Utils;


/**
 *
 * @author shaozhuguang
 * @create 2018/11/21
 * @since 1.0.0
 */

public class MessageConvertUtil {

    public static final String defaultCharsetName = "UTF-8";

    public static String base64Encode(byte[] src) {
        return Base64Utils.encodeToString(src);
    }

    public static byte[] base64Decode(String src) {
        return Base64Utils.decodeFromString(src);
    }

    public static String messageKey(byte[] src) {
        return base64Encode(ShaUtils.hash_256(src));
    }

    public static BlockEvent convertBytes2BlockEvent(byte[] serializeBytes) {
        String text;
        try{
            text = new String(serializeBytes, defaultCharsetName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return convertString2BlockEvent(text);
    }

    public static BlockEvent convertString2BlockEvent(String serializeString) {
        return JSON.parseObject(serializeString, BlockEvent.class);
    }

    public static TxBlockedEvent convertBytes2TxBlockedEvent(byte[] serializeBytes) {
        String text;
        try{
            text = new String(serializeBytes, defaultCharsetName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return convertString2TxBlockedEvent(text);
    }

    public static TxBlockedEvent convertString2TxBlockedEvent(String serializeString) {
        return JSON.parseObject(serializeString, TxBlockedEvent.class);
    }

    public static byte[] serializeBlockEvent(BlockEvent blockEvent) {
        String serializeString = serializeEvent(blockEvent);
        byte[] serializeBytes;
        try {
            serializeBytes = serializeString.getBytes(defaultCharsetName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return serializeBytes;
    }

    public static byte[] serializeTxBlockedEvent(TxBlockedEvent txBlockedEvent) {
        String serializeString = JSON.toJSONString(txBlockedEvent);
        byte[] serializeBytes;
        try {
            serializeBytes = serializeString.getBytes(defaultCharsetName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return serializeBytes;
    }

    public static String serializeEvent(BlockEvent blockEvent) {
        return JSON.toJSONString(blockEvent);
    }
}