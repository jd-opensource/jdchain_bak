/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.TransferMessage
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/11 上午11:00
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;

/**
 * 底层传输协议
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class TransferMessage extends AbstractMessage implements IMessage{

    /**
     * sessionId（描述节点信息）
     */
    private String sessionId;

    /**
     * 本次消息的类型
     * 0：请求；
     * 1：应答；
     */
    private int type;

    /**
     * 消息的Key
     */
    private String key;

    /**
     * 消息载体的内容
     * 本内容不可被序列化
     */
    private transient byte[] load;

    /**
     * 消息载体的内容->Base64转换
     */
    private String loadBase64;

    public TransferMessage() {
    }

    public TransferMessage(String sessionId, int type, String key, byte[] load) {
        this.sessionId = sessionId;
        this.type = type;
        this.key = key;
        this.load = load;
    }

    /**
     * 转换为TransferMessage对象
     *
     * @param msg
     * @return
     */
    public static TransferMessage toTransferMessage(Object msg) {
        if (msg == null) {
            return null;
        }
        TransferMessage tm;
        try {
            tm = JSON.parseObject(msg.toString(), TransferMessage.class);
            tm.initLoad();
        } catch (Exception e) {
            return null;
        }
        return tm;
    }

    public byte[] load() {
        return load;
    }

    public void initLoad() {
        if (loadBase64 != null && loadBase64.length() > 0) {
            load = Base64.decodeBase64(loadBase64);
        }
    }

    public void initLoadBase64() {
        if (load != null && load.length > 0) {
            loadBase64 = Base64.encodeBase64String(load);
        }
    }

    @Override
    public String toTransfer() {
        // 使用JSON的方式发送
        // 初始化load的base64转换
        initLoadBase64();

        // 将字符串转换为JSON
        return JSON.toJSONString(this);
    }

    /**
     * 转换为监听的Key
     * 该Key可描述为从远端发送来消息及其内容的唯一性
     *
     * @return
     */
    public String toListenKey() {
        return key;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String loadKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getLoadBase64() {
        return loadBase64;
    }

    public void setLoadBase64(String loadBase64) {
        this.loadBase64 = loadBase64;
    }

    public enum MESSAGE_TYPE {

        TYPE_REQUEST(0),

        TYPE_RESPONSE(1);

        private int code;

        MESSAGE_TYPE(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }

        public static MESSAGE_TYPE valueOf(int code) {
            switch (code) {
                case 0:
                    return TYPE_REQUEST;
                case 1:
                    return TYPE_RESPONSE;

            }
            return null;
        }

    }
}