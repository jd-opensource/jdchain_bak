/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.handler.ReceiverHandler
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/12 上午11:14
 * Description:
 */
package com.jd.blockchain.stp.communication.connection.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jd.blockchain.stp.communication.MessageExecute;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.connection.Connection;
import com.jd.blockchain.stp.communication.connection.listener.ReplyListener;
import com.jd.blockchain.stp.communication.manager.ConnectionManager;
import com.jd.blockchain.stp.communication.message.SessionMessage;
import com.jd.blockchain.stp.communication.message.TransferMessage;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/12
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class ReceiverHandler extends ChannelInboundHandlerAdapter implements Closeable {

    // 队列的最大容量为256K（防止队列溢出）
    private static final int QUEUE_CAPACITY = 256 * 1024;

    private final Map<String, RemoteSession> remoteSessions = new ConcurrentHashMap<>();

    private final Map<String, ReplyListener> allReplyListeners = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    private String messageExecuteClass;

    private ConnectionManager connectionManager;

    private ExecutorService msgExecutePool;

    public ReceiverHandler(ConnectionManager connectionManager, String messageExecuteClass) {
        this.connectionManager = connectionManager;
        this.messageExecuteClass = messageExecuteClass;
        init();
    }

    public void putRemoteSession(String sessionId, RemoteSession remoteSession) {
        remoteSessions.put(sessionId, remoteSession);
    }

    public void addListener(ReplyListener replyListener) {
        allReplyListeners.put(replyListener.listenKey(), replyListener);
    }

    public void removeListener(String key) {
        this.allReplyListeners.remove(key);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("Receive Biz Message -> " + msg.toString());
        // 有数据接入
        // 首先判断数据是否TransferMessage，当前Handler不处理非TransferMessage
        TransferMessage tm = TransferMessage.toTransferMessageObj(msg);
        if (tm == null) {
            // 判断是否是SessionMessage
            SessionMessage sm = SessionMessage.toNodeSessionMessage(msg);
            if (sm != null) {
                executeSessionMessage(sm);
            } else {
                super.channelRead(ctx, msg);
            }
        } else {
            TransferMessage.MESSAGE_TYPE messageType = TransferMessage.MESSAGE_TYPE.valueOf(tm.getType());
            // 对于请求和应答处理方式不同
            if (messageType.equals(TransferMessage.MESSAGE_TYPE.TYPE_REQUEST)) {
                // 假设是请求消息
                executeRequest(tm);
            } else if (messageType.equals(TransferMessage.MESSAGE_TYPE.TYPE_RESPONSE)) {
                // 假设是应答消息
                executeResponse(tm);
            } else {
                // todo 其他消息只需要打印日志即可


            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    // 防止消息的处理过程阻塞主进程
    private void executeRequest(final TransferMessage transferMessage) {
        msgExecutePool.execute(() -> {
            RemoteSession remoteSession = remoteSessions.get(transferMessage.getSessionId());
            if (remoteSession != null) {
                MessageExecute messageExecute = remoteSession.messageExecute();
                if (messageExecute != null) {
                    MessageExecute.REPLY replyType = messageExecute.replyType();
                    if (replyType != null) {
                        switch (messageExecute.replyType()) {
                            case MANUAL:
                                messageExecute.receive(transferMessage.loadKey(), transferMessage.load(), remoteSession);
                                break;
                            case AUTO:
                                String requestKey = transferMessage.loadKey();
                                byte[] replyMsg = messageExecute.receive(requestKey, transferMessage.load(), remoteSession);
                                // 应答
                                remoteSession.reply(requestKey, () -> replyMsg);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    private void executeResponse(final TransferMessage transferMessage) {
        msgExecutePool.execute(() -> {
            // listenKey和msgKey是不一致的
            // msgKey是对消息本身设置key，listenKey是对整个消息（包括session信息）
            String listenKey = transferMessage.toListenKey();

            ReplyListener replyListener = allReplyListeners.get(listenKey);

            if (replyListener != null) {
                // 填充对应的结果
                replyListener.replyData(transferMessage.load());

                ReplyListener.MANAGE_TYPE manageType = replyListener.manageType();

                if (manageType != null) {
                    switch (manageType) {
                        case REMOVE:
                            // 将对象从Map中移除
                            removeListener(listenKey);
                            break;
                        case HOLD:
                        default:
                            // todo 打印日志

                            break;
                    }
                }
            }
        });
    }

    private void executeSessionMessage(SessionMessage sessionMessage) {
        // 处理SessionMessage
        String sessionId = sessionMessage.sessionId();
        if (sessionId != null && !remoteSessions.containsKey(sessionId)) {

            try {
                lock.lock();
                // 生成对应的MessageExecute对象
                String messageExecuteClass = sessionMessage.getMessageExecute();
                MessageExecute messageExecute = null;
                if (messageExecuteClass != null && messageExecuteClass.length() > 0) {
                    try {
                        Class<?> clazz = Class.forName(messageExecuteClass);
                        messageExecute = (MessageExecute) clazz.newInstance();
                    } catch (Exception e) {
                        // TODO 打印日志
                        e.printStackTrace();
                    }
                }

                // 必须保证该对象不为空
                if (messageExecute != null) {
                    // 说明尚未和请求来的客户端建立连接，需要建立连接
                    Connection remoteConnection = this.connectionManager.connect(new RemoteNode(
                                    sessionMessage.getLocalHost(), sessionMessage.getListenPort()),
                            this.messageExecuteClass);
                    RemoteSession remoteSession = new RemoteSession(sessionId, remoteConnection, messageExecute);

                    // Double check ！！！
                    if (!remoteSessions.containsKey(sessionId)) {
                        remoteSessions.put(sessionId, remoteSession);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private void init() {

        ThreadFactory msgExecuteThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("msg-execute-pool-%d").build();

        //Common Thread Pool
        msgExecutePool = new ThreadPoolExecutor(5, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                msgExecuteThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void close() {
        msgExecutePool.shutdown();
    }
}