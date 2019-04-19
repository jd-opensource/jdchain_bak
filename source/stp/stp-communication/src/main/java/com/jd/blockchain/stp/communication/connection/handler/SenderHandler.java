/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.handler.SenderHandler
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/16 下午2:00
 * Description:
 */
package com.jd.blockchain.stp.communication.connection.handler;

import com.jd.blockchain.stp.communication.message.SessionMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * Sender对应Handler
 * @author shaozhuguang
 * @create 2019/4/16
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class SenderHandler extends ChannelInboundHandlerAdapter {

    private SessionMessage sessionMessage;

    public SenderHandler(SessionMessage sessionMessage) {
        this.sessionMessage = sessionMessage;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 发送本机信息（包括IP、端口等）至对端
        System.out.println("Connection Receiver Success, Send Local Node Information !!!");
        ctx.writeAndFlush(sessionMessage.toTransferByteBuf());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}