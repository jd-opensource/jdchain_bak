/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.HeartBeatMessage
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/12 下午4:55
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * 心跳消息
 * @author shaozhuguang
 * @create 2019/4/12
 * @since 1.0.0
 */

public class HeartBeatMessage implements IMessage {

    private static final String HEARTBEAT_STRING = "JDChainHeartBeat";

    private static final ByteBuf HEARTBEAT_MESSAGE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(HEARTBEAT_STRING + "\r\n",
            CharsetUtil.UTF_8));

    public static final void write(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(HEARTBEAT_MESSAGE.duplicate());
    }

    public static final boolean isHeartBeat(Object msg) {
        if (msg instanceof String) {
            return isHeartBeat((String) msg);
        }
        return false;
    }

    public static final boolean isHeartBeat(String msg) {
        if (HEARTBEAT_STRING.equals(msg)) {
            return true;
        }
        return false;
    }

    @Override
    public String toTransfer() {
        return HEARTBEAT_STRING;
    }

    @Override
    public ByteBuf toTransferByteBuf() {
        return HEARTBEAT_MESSAGE;
    }
}