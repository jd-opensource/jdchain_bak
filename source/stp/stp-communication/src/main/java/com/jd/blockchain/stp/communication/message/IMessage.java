/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.IMessage
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/16 下午1:58
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

import io.netty.buffer.ByteBuf;

/**
 * 消息接口
 * @author shaozhuguang
 * @create 2019/4/16
 * @since 1.0.0
 */

public interface IMessage {

    /**
     * 消息转换为字符串
     * @return
     */
    String toTransfer();

    /**
     * 消息转换为ByteBuf
     * @return
     */
    ByteBuf toTransferByteBuf();
}