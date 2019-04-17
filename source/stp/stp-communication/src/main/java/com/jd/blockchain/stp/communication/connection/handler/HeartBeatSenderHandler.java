/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.handler.HeartBeatSenderHandler
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/15 上午10:10
 * Description:
 */
package com.jd.blockchain.stp.communication.connection.handler;

import com.jd.blockchain.stp.communication.message.HeartBeatMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/15
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class HeartBeatSenderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断收到的消息
        if (HeartBeatMessage.isHeartBeat(msg)) {
            // 假设收到的消息是字符串，并且是心跳消息，说明由服务端发送了心跳信息
            // TODO 此处不需要进行消息反馈，只需要打印日志即可
            System.out.println("Receive HeartBeat Response Message -> " + msg.toString());
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 出现异常直接关闭连接
        ctx.close();
    }
}