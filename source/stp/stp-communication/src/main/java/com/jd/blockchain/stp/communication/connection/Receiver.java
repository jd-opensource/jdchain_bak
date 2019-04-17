/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.inner.Receiver
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午10:59
 * Description:
 */
package com.jd.blockchain.stp.communication.connection;

import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.connection.handler.HeartBeatReceiverHandler;
import com.jd.blockchain.stp.communication.connection.handler.HeartBeatReceiverTrigger;
import com.jd.blockchain.stp.communication.connection.handler.ReceiverHandler;
import com.jd.blockchain.stp.communication.connection.listener.ReplyListener;
import com.jd.blockchain.stp.communication.manager.ConnectionManager;
import com.jd.blockchain.stp.communication.manager.RemoteSessionManager;
import com.jd.blockchain.stp.communication.node.LocalNode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class Receiver extends AbstractAsyncExecutor implements Closeable {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private LocalNode localNode;

    private ReceiverHandler receiverHandler;

    public Receiver(LocalNode localNode) {
        this.localNode = localNode;
    }

    public void startListen() {
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .localAddress(new InetSocketAddress(this.localNode.getPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
//                                .addLast(new LoggingHandler(LogLevel.ERROR))
                                .addLast(new IdleStateHandler(8, 0, 0, TimeUnit.SECONDS))
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new HeartBeatReceiverTrigger())
                                .addLast(new HeartBeatReceiverHandler())
                                .addLast(receiverHandler);
                    }
                });

        // 由单独的线程启动
        ThreadPoolExecutor runThread = initRunThread();
        runThread.execute(() -> {
            try {
                ChannelFuture f = bootstrap.bind().sync();
                super.isStartSuccess = f.isSuccess();
                super.isStarted.release();
                if (super.isStartSuccess) {
                    // 启动成功
                    f.channel().closeFuture().sync();
                } else {
                    // 启动失败
                    throw new Exception("Receiver start fail :" + f.cause().getMessage() + " !!!");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                close();
            }
        });
    }

    @Override
    public String threadNameFormat() {
        return "receiver-pool-%d";
    }

    public void initReceiverHandler(ConnectionManager connectionManager, String messageExecuteClass) {
        receiverHandler = new ReceiverHandler(connectionManager, messageExecuteClass);
    }

    public void initRemoteSession(String sessionId, RemoteSession remoteSession) {
        receiverHandler.putRemoteSession(sessionId, remoteSession);
    }

    public void addListener(ReplyListener replyListener) {
        receiverHandler.addListener(replyListener);
    }

    @Override
    public void close() {
        receiverHandler.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public LocalNode localNode() {
        return this.localNode;
    }
}