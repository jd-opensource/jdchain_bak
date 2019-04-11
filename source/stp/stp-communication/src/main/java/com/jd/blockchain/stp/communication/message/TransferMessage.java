/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.TransferMessage
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午11:00
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class TransferMessage {

    private byte[] key;

    private byte[] load;

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getLoad() {
        return load;
    }

    public void setLoad(byte[] load) {
        this.load = load;
    }

    public String toBase64() {



        return null;
    }
}