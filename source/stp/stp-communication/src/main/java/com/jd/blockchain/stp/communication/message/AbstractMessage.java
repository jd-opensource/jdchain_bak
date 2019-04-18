/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.AbstractMessage
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/17 下午4:00
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 抽象消息
 * @author shaozhuguang
 * @create 2019/4/17
 * @since 1.0.0
 */

public abstract class AbstractMessage implements IMessage {

    @Override
    public ByteBuf toTransferByteBuf() {
        byte[] message = (toTransfer() + "\r\n").getBytes();
        ByteBuf byteBuf = Unpooled.buffer(message.length);
        byteBuf.writeBytes(message);
        return byteBuf;
    }
}