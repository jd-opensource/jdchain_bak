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
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;
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

    /**
     * 本地session信息
     */
    private SessionMessage sessionMessage;

    /**
     * 本地节点
     */
    private LocalNode localNode;

    /**
     * 远端节点
     */
    private RemoteNode remoteNode;

    public SenderHandler(LocalNode localNode, RemoteNode remoteNode, SessionMessage sessionMessage) {
        this.localNode = localNode;
        this.remoteNode = remoteNode;
        this.sessionMessage = sessionMessage;
    }

    /**
     * 连接远端节点成功时触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // 发送本机信息（包括IP、端口等）至对端
        System.out.printf("%s Connect %s Success, Send Local Node Information !!! \r\n", this.localNode, this.remoteNode);
        ctx.writeAndFlush(sessionMessage.toTransferByteBuf());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}