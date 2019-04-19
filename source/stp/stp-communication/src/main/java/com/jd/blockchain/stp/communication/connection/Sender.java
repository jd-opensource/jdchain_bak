/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.inner.Sender
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/11 上午10:58
 * Description:
 */
package com.jd.blockchain.stp.communication.connection;

import com.jd.blockchain.stp.communication.connection.handler.*;
import com.jd.blockchain.stp.communication.message.IMessage;
import com.jd.blockchain.stp.communication.message.SessionMessage;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 发送器
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 * @date 2019-04-18 15:08
 */
public class Sender extends AbstractAsyncExecutor implements Closeable {

    private final EventLoopGroup loopGroup = new NioEventLoopGroup();

    private Bootstrap bootstrap;

    private ChannelFuture channelFuture;

    /**
     * 当前节点的SessionMessage
     */
    private SessionMessage sessionMessage;

    /**
     * 远端HOST
     */
    private String remoteHost;

    /**
     * 远端端口
     */
    private int remotePort;

    /**
     * 监听Handler（重连Handler）
     */
    private WatchDogHandler watchDogHandler;

    public Sender(RemoteNode remoteNode, SessionMessage sessionMessage) {
        init(remoteNode, sessionMessage);
    }

    public Sender(String remoteHost, int remotePort, SessionMessage sessionMessage) {
        init(remoteHost, remotePort, sessionMessage);
    }

    /**
     * 连接
     */
    public void connect() {
        watchDogHandler = new WatchDogHandler(this.remoteHost, this.remotePort, bootstrap);

        ChannelHandlers frontChannelHandlers = new ChannelHandlers()
                .addHandler(watchDogHandler);

        ChannelHandlers afterChannelHandlers = new ChannelHandlers()
                .addHandler(new StringDecoder())
                .addHandler(new HeartBeatSenderTrigger())
                .addHandler(new HeartBeatSenderHandler())
                .addHandler(new SenderHandler(this.sessionMessage));

        // 初始化watchDogHandler
        watchDogHandler.init(frontChannelHandlers.toArray(), afterChannelHandlers.toArray());

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                          .addLast(frontChannelHandlers.toArray())
                          .addLast(new IdleStateHandler(10, 4, 0, TimeUnit.SECONDS))
                          .addLast(new LineBasedFrameDecoder(1024))
                          .addLast(afterChannelHandlers.toArray());
                    }
                });

        ThreadPoolExecutor runThread = initRunThread();

        // 单独线程进行连接，防止当前调用线程阻塞
        runThread.execute(() -> {
            try {
                // 发起连接请求
                channelFuture = bootstrap.connect(this.remoteHost, this.remotePort).sync();
                boolean isStartSuccess = channelFuture.isSuccess();
                if (isStartSuccess) {
                    // 启动成功
                    // 设置ChannelFuture对象，以便于发送的连接状态处理
                    watchDogHandler.initChannelFuture(channelFuture);
                    // 释放等待
                    super.callBackLauncher.bootSuccess();
                    // 等待客户端关闭连接
                    channelFuture.channel().closeFuture().sync();
                } else {
                    // 启动失败
                    throw new Exception("Sender start fail :" + channelFuture.cause().getMessage() + " !!!");
                }
            } catch (Exception e) {
                super.callBackLauncher.bootFail(e);
            } finally {
                close();
            }
        });
    }

    /**
     * 初始化相关配置
     *
     * @param remoteNode
     *     远端节点
     * @param sessionMessage
     *     本地节点连接到远端节点后发送的SessionMessage
     */
    private void init(RemoteNode remoteNode, SessionMessage sessionMessage) {
        init(remoteNode.getHostName(), remoteNode.getPort(), sessionMessage);
    }

    /**
     * 初始化相关配置
     *
     * @param remoteHost
     *     远端HOST
     * @param remotePort
     *     远端端口
     * @param sessionMessage
     *     本地节点连接到远端节点后发送的SessionMessage
     */
    private void init(String remoteHost, int remotePort, SessionMessage sessionMessage) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;

        this.sessionMessage = sessionMessage;

        this.bootstrap = new Bootstrap().group(loopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
    }

    @Override
    public String threadNameFormat() {
        return "sender-pool-%d";
    }

    /**
     * 发送消息
     *
     * @param message
     *     消息统一接口
     */
    public void send(IMessage message) {
        watchDogHandler.channelFuture().channel().writeAndFlush(message.toTransferByteBuf());
    }

    @Override
    public void close() {
        // 因为要重连，需要仍然需要使用该LoopGroup，因此不能关闭
//        loopGroup.shutdownGracefully();
    }

    /**
     * ChannelHandler集合管理类
     */
    public static class ChannelHandlers {

        private List<ChannelHandler> channelHandlers = new ArrayList<>();

        /**
         * 添加指定的ChannelHandler
         *
         * @param channelHandler
         *     需要加入的ChannelHandler
         * @return
         */
        public ChannelHandlers addHandler(ChannelHandler channelHandler) {
            channelHandlers.add(channelHandler);
            return this;
        }

        /**
         * List集合转换为数组
         *
         * @return
         */
        public ChannelHandler[] toArray() {
            ChannelHandler[] channelHandlerArray = new ChannelHandler[channelHandlers.size()];
            return channelHandlers.toArray(channelHandlerArray);
        }
    }
}