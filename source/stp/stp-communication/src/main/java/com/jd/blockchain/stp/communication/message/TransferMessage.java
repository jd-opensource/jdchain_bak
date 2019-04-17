/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.TransferMessage
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午11:00
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class TransferMessage extends AbstractMessage implements IMessage{

    private String sessionId;

    private int type;

    private String key;

    private transient byte[] load;

    private String loadBase64;

    public TransferMessage() {
    }

    public TransferMessage(String sessionId, int type, String key, byte[] load) {
        this.sessionId = sessionId;
        this.type = type;
        this.key = key;
        this.load = load;
    }

    public static TransferMessage toTransferMessageObj(Object msg) {
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

    public String toListenKey() {
        // 格式：sessionId:key
        return sessionId + ":" + key;
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