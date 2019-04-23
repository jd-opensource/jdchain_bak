/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.handler.ReceiverHandler
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/12 上午11:14
 * Description:
 */
package com.jd.blockchain.stp.communication.connection.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jd.blockchain.stp.communication.MessageExecutor;
import com.jd.blockchain.stp.communication.RemoteSession;
import com.jd.blockchain.stp.communication.connection.Connection;
import com.jd.blockchain.stp.communication.connection.listener.ReplyListener;
import com.jd.blockchain.stp.communication.manager.ConnectionManager;
import com.jd.blockchain.stp.communication.message.SessionMessage;
import com.jd.blockchain.stp.communication.message.TransferMessage;
import com.jd.blockchain.stp.communication.node.LocalNode;
import com.jd.blockchain.stp.communication.node.RemoteNode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.codec.binary.Hex;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 接收者消息处理Handler
 * @author shaozhuguang
 * @create 2019/4/12
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class ReceiverHandler extends ChannelInboundHandlerAdapter implements Closeable {

    /**
     * 队列的最大容量设置，默认为256K（防止队列溢出）
     */
    private static final int QUEUE_CAPACITY = 256 * 1024;

    /**
     * 远端RemoteSession信息集合
     * Key为SessionId
     * Sender发送的消息中会携带SessionId
     * ReceiverHandler会根据不同的SessionId采用不同的MessageExecutor处理策略
     */
    private final Map<String, RemoteSession> remoteSessions = new ConcurrentHashMap<>();

    /**
     * 监听器集合
     * 对应Sender在发送请求之前会设置ReplyListener
     * Key为每个请求消息的Hash，用于描述消息的唯一性
     * 应答一方会在应答中加入对应的key，用于消息的映射
     */
    private final Map<String, ReplyListener> allReplyListeners = new ConcurrentHashMap<>();

    /**
     * session控制锁
     * 用于防止对统一RemoteSession对象进行重复设置
     */
    private final Lock sessionLock = new ReentrantLock();

    /**
     * 当前节点（本地节点）的消息处理器对应Class
     * 该信息用于发送至其他节点，向其他节点通知遇到本节点请求时该如何处理
     */
    private String localMsgExecutorClass;

    /**
     * 连接控制器，用于与远端节点连接
     */
    private ConnectionManager connectionManager;

    /**
     * 消息处理执行线程池
     * 防止执行内容过长，导致阻塞
     */
    private ExecutorService msgExecutorPool;

    /**
     * 默认消息处理器
     * 当对应session获取到的RemoteSession中没有获取到指定MessageExecutor时，短时间内由其进行处理
     */
    private MessageExecutor defaultMessageExecutor;

    /**
     * 本地节点
     */
    private LocalNode localNode;

    public ReceiverHandler(ConnectionManager connectionManager, String localMsgExecutorClass,
                           LocalNode localNode) {
        this.connectionManager = connectionManager;
        this.localMsgExecutorClass = localMsgExecutorClass;
        this.defaultMessageExecutor = localNode.defaultMessageExecutor();
        this.localNode = localNode;
        initMsgExecutorPool();
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

        System.out.printf("%s Receive Biz Message -> %s \r\n", this.localNode.toString(), msg.toString());
        // 有数据接入
        // 首先判断数据是否TransferMessage，当前Handler不处理非TransferMessage
        TransferMessage tm = TransferMessage.toTransferMessage(msg);
        if (tm == null) {
            // 判断是否是SessionMessage
            SessionMessage sm = SessionMessage.toSessionMessage(msg);
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

    /**
     * 处理请求消息
     *
     * @param transferMessage
     *     接收到的请求消息
     */
    private void executeRequest(final TransferMessage transferMessage) {
        msgExecutorPool.execute(() -> {
            RemoteSession remoteSession = remoteSessions.get(transferMessage.getSessionId());
            if (remoteSession != null) {
                MessageExecutor messageExecutor = remoteSession.messageExecutor();
                if (messageExecutor == null) {
                    // 采用默认处理器进行处理
                    messageExecutor = defaultMessageExecutor;
                }
                MessageExecutor.REPLY replyType = messageExecutor.replyType();
                if (replyType != null) {
                    switch (replyType) {
                        case MANUAL:
                            messageExecutor.receive(transferMessage.loadKey(), transferMessage.load(), remoteSession);
                            break;
                        case AUTO:
                            String requestKey = transferMessage.loadKey();
                            byte[] replyMsg = messageExecutor.receive(requestKey, transferMessage.load(), remoteSession);
                            // 应答
                            remoteSession.reply(requestKey, () -> replyMsg);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    /**
     * 处理应答消息
     * @param transferMessage
     *     接收到的应答消息
     */
    private void executeResponse(final TransferMessage transferMessage) {
        msgExecutorPool.execute(() -> {
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

    /**
     * 处理SessionMessage
     * @param sessionMessage
     *     描述Session的消息对象
     */
    private void executeSessionMessage(SessionMessage sessionMessage) {
        // 处理SessionMessage
        String sessionId = sessionMessage.sessionId();
        if (sessionId != null) {
            // 对于含有的RemoteSession的Map，需要判断其MessageExecutor是否为NULL
            RemoteSession remoteSession = remoteSessions.get(sessionId);
            if (remoteSession == null) {
                try {
                    sessionLock.lock();
                    // 生成对应的MessageExecute对象
                    String meClass = sessionMessage.getMessageExecutor();
                    MessageExecutor messageExecutor = initMessageExecutor(meClass);

                    // 说明尚未和请求来的客户端建立连接，需要建立连接
                    Connection remoteConnection = this.connectionManager.connect(new RemoteNode(
                                    sessionMessage.getLocalHost(), sessionMessage.getListenPort()),
                            this.localMsgExecutorClass);
                    // 假设连接失败的话，返回的Connection对象为null，此时不放入Map，等后续再处理
                    if (remoteConnection != null) {

                        remoteSession = new RemoteSession(this.localId(), remoteConnection, messageExecutor);

                        // Double check ！！！
                        if (!remoteSessions.containsKey(sessionId)) {
                            remoteSessions.put(sessionId, remoteSession);
                        }
                    }
                } finally {
                    sessionLock.unlock();
                }
            } else {
                // 需要判断MessageExecutor
                MessageExecutor me = remoteSession.messageExecutor();
                if (me == null) {
                    try {
                        sessionLock.lock();
                        // Double Check !!!
                        if (remoteSession.messageExecutor() == null) {
                            // 表明上次存储的MessageExecutor未创建成功，本次进行更新
                            String meClass = sessionMessage.getMessageExecutor();
                            MessageExecutor messageExecutor = initMessageExecutor(meClass);

                            // 防止NULL将其他的进行覆盖
                            if (messageExecutor != null) {
                                remoteSession.initExecutor(messageExecutor);
                            }
                        }
                    } finally {
                        sessionLock.unlock();
                    }
                }
            }
        }
    }

    /**
     * 初始化消息执行器
     * 根据消息执行器的Class字符串生成对应的消息处理对象
     * @param messageExecutorClass
     *     消息执行器的Class字符串
     * @return
     *     对应的消息处理对象，产生任何异常都返回NULL
     */
    private MessageExecutor initMessageExecutor(String messageExecutorClass) {
        // 生成对应的MessageExecute对象
        MessageExecutor messageExecutor = null;
        if (messageExecutorClass != null && messageExecutorClass.length() > 0) {
            try {
                Class<?> clazz = Class.forName(messageExecutorClass);
                messageExecutor = (MessageExecutor) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return messageExecutor;
    }

    /**
     * 初始化消息处理线程池
     */
    private void initMsgExecutorPool() {

        ThreadFactory msgExecuteThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("msg-executor-pool-%d").build();

        //Common Thread Pool
        msgExecutorPool = new ThreadPoolExecutor(5, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                msgExecuteThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 返回本地节点
     *
     * @return
     */
    public LocalNode localNode() {
        return localNode;
    }

    /**
     * 返回本地节点ID
     *
     * @return
     */
    private String localId() {
        return Hex.encodeHexString(localNode.toString().getBytes());
    }

    @Override
    public void close() {
        msgExecutorPool.shutdown();
    }
}