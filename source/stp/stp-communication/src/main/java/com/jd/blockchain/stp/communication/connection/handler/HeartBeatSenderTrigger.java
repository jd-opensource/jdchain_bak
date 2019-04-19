/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.handler.HeartBeatSenderTrigger
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/15 上午10:11
 * Description:
 */
package com.jd.blockchain.stp.communication.connection.handler;

import com.jd.blockchain.stp.communication.message.HeartBeatMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳发送触发器
 * @author shaozhuguang
 * @create 2019/4/15
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class HeartBeatSenderTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // 心跳事件（状态空闲事件）
        if (evt instanceof IdleStateEvent) {
            IdleState idleState = ((IdleStateEvent) evt).state();
            if (idleState.equals(IdleState.READER_IDLE)) {
                // Sender读超时，表示在指定时间内未收到Receiver的应答
                // 此时关闭连接，自动调用重连机制，进行重连操作
                System.out.println("Long Time UnReceive HeartBeat Response, Close Connection !!!");
                ctx.close();
            } else if (idleState == IdleState.WRITER_IDLE) {
                // Sender写超时，表示很长时间没有发送消息了，需要发送消息至Receiver
                System.out.println("Read TimeOut Trigger, Send HeartBeat Request !!!");
                HeartBeatMessage.write(ctx);
            }
            // TODO 还有一种情况是读写超时，该情况暂不处理
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}