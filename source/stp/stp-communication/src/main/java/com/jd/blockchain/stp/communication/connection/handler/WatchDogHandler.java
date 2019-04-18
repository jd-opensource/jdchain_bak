/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.SenderWatchDog
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/12 下午4:56
 * Description:
 */
package com.jd.blockchain.stp.communication.connection.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jd.blockchain.stp.communication.message.HeartBeatMessage;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.Closeable;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 连接监听器
 * @author shaozhuguang
 * @create 2019/4/12
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class WatchDogHandler extends ChannelInboundHandlerAdapter implements Runnable, Closeable {

    /**
     * 当前连接活跃状态
     */
    private final AtomicBoolean currentActive = new AtomicBoolean(false);

    /**
     * 重连的控制锁
     * 防止重连过程中重复多次调用
     */
    private final Lock reconnectLock = new ReentrantLock();

    // 默认的最多重连次数
    private final int maxReconnectSize = 16;

    // 默认重连的时间
    private final int defaultReconnectSeconds = 2;

    // 标识是否正常工作中，假设不再工作则不再重连
    private boolean isWorking = true;

    /**
     * 重连调度器
     */
    private ScheduledExecutorService reconnectTimer;

    /**
     * 远端的IP（域名）信息
     */
    private String hostName;

    /**
     * 远端的端口
     */
    private int port;

    private Bootstrap bootstrap;

    /**
     * 第一组Handler数组
     */
    private ChannelHandler[] frontHandlers;

    /**
     * 后一组Handler数组
     */
    private ChannelHandler[] afterHandlers;

    /**
     * 用于重连时对象重置
     */
    private ChannelFuture channelFuture;

    /**
     * 构造器
     * @param hostName
     *     远端Host
     * @param port
     *     远端端口
     * @param bootstrap
     *     Netty工作启动器
     */
    public WatchDogHandler(String hostName, int port, Bootstrap bootstrap) {
        this.hostName = hostName;
        this.port = port;
        this.bootstrap = bootstrap;
    }

    /**
     * 构造器
     * @param remoteNode
     *     远端节点
     * @param bootstrap
     *     Netty工作启动器
     */
    public WatchDogHandler(RemoteNode remoteNode, Bootstrap bootstrap) {
        this(remoteNode.getHostName(), remoteNode.getPort(), bootstrap);
    }

    /**
     * 配置重连需要的Handler
     * 主要是为了对象的复用，同时有些Handler无法复用，对于每次连接请求必须要new新的对象
     * @param frontHandlers
     * @param afterHandlers
     */
    public void init(ChannelHandler[] frontHandlers, ChannelHandler[] afterHandlers) {
        this.frontHandlers = frontHandlers;
        this.afterHandlers = afterHandlers;
        initTimer();
    }

    public void initChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public ChannelFuture channelFuture() {
        try {
            reconnectLock.lock();
            return this.channelFuture;
        } finally {
            reconnectLock.unlock();
        }
    }

    /**
     * 连接成功调用
     * 该连接成功表示完全连接成功，对于TCP而言就是三次握手成功
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 调用该方法表示连接成功
        connectSuccess();

        // 连接成功后发送心跳消息至服务端
        HeartBeatMessage.write(ctx);

        ctx.fireChannelActive();
    }

    /**
     * 连接失败时调用
     * 此处是触发重连的入口
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.err.println("Connection Exception, Close And Reconnect !!!");
        // 调用该方法时表示连接关闭了（无论是什么原因）
        // 连接关闭的情况下需要重新连接

        connectFail();

        ctx.close();

        for (int i = 0; i < maxReconnectSize; i++) {
            reconnectTimer.schedule(this, defaultReconnectSeconds << i, TimeUnit.SECONDS);
        }

        ctx.fireChannelInactive();
    }

    @Override
    public void run() {
        if (isNeedReconnect()) {
            // 重连
            try {
                reconnectLock.lock();
                if (isNeedReconnect()) {

                    bootstrap.handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                              .addLast(frontHandlers)
                              .addLast(new IdleStateHandler(10, 4, 0, TimeUnit.SECONDS))
                              .addLast(new LineBasedFrameDecoder(1024))
                              .addLast(afterHandlers)
                            ;
                        }
                    });

                    channelFuture = bootstrap.connect(hostName, port);

                    // 增加监听器用于判断本次重连是否成功
                    channelFuture.addListener((ChannelFutureListener) future -> {
                        boolean isReconnectSuccess = future.isSuccess();
                        if (isReconnectSuccess) {
                            // 连接成功
                            connectSuccess();
                        } else {
                            connectFail();
                        }
                    });

                }
            } finally {
                reconnectLock.unlock();
            }
        }
    }

    private boolean isNeedReconnect() {
        return isWorking && !currentActive.get();
    }

    private void connectSuccess() {
        this.currentActive.set(true);
    }

    private void connectFail() {
        this.currentActive.set(false);
    }

    @Override
    public void close() {
        this.isWorking = false;
        this.reconnectTimer.shutdown();
    }

    /**
     * 设置调度器
     */
    private void initTimer() {
        ThreadFactory timerFactory = new ThreadFactoryBuilder()
                .setNameFormat("reconnect-pool-%d").build();

        reconnectTimer = new ScheduledThreadPoolExecutor(1, timerFactory, new ThreadPoolExecutor.AbortPolicy());
    }
}