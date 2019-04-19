/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.LoadMessage
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/11 上午10:59
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

/**
 * 负载消息
 * 该接口用于应用实现
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public interface LoadMessage {

    /**
     * 将负载消息转换为字节数组
     * @return
     */
    byte[] toBytes();
}