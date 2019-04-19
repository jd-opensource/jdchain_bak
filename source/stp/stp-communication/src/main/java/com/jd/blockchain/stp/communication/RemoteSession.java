/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.RemoteSession
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/11 上午11:15
 * Description:
 */
package com.jd.blockchain.stp.communication;

import com.jd.blockchain.stp.communication.callback.CallBackBarrier;
import com.jd.blockchain.stp.communication.callback.CallBackDataListener;
import com.jd.blockchain.stp.communication.connection.Connection;
import com.jd.blockchain.stp.communication.message.LoadMessage;

import java.util.concurrent.TimeUnit;


/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class RemoteSession {

    /**
     * 远端节点ID
     */
    private String id;

    /**
     * 远端连接
     */
    private Connection connection;

    /**
     * 对应远端节点消息的处理器
     * 该处理器若为NULL，则使用当前节点默认处理器
     */
    private MessageExecutor messageExecutor;


    /**
     * 构造器
     * @param id
     *     远端节点ID
     * @param connection
     *     对应连接
     */
    public RemoteSession(String id, Connection connection) {
        this(id, connection, null);
    }

    /**
     * 构造器
     * @param id
     *     远端ID
     * @param connection
     *     对应连接
     * @param messageExecutor
     *     对应远端消息处理器
     */
    public RemoteSession(String id, Connection connection, MessageExecutor messageExecutor) {
        this.id = id;
        this.connection = connection;
        this.messageExecutor = messageExecutor;
    }

    public void init() {
        connection.initSession(this);
    }

    public void initExecutor(MessageExecutor messageExecutor) {
        this.messageExecutor = messageExecutor;
    }

    /**
     * 同步请求
     * 该请求会阻塞原线程
     *
     * @param loadMessage
     *     要请求的负载消息
     * @return
     *     应答，直到有消息应答或出现异常
     * @throws Exception
     */
    public byte[] request(LoadMessage loadMessage) throws Exception {
        return this.connection.request(this.id, loadMessage, null).getCallBackData();
    }

    /**
     * 同步请求
     * 该请求会阻塞原线程
     *
     * @param loadMessage
     *     要请求的负载消息
     * @param time
     *     请求的最长等待时间
     * @param timeUnit
     *     请求的最长等待单位
     * @return
     *     应答，直到有消息或时间截止或出现异常
     * @throws Exception
     */
    public byte[] request(LoadMessage loadMessage, long time, TimeUnit timeUnit) throws Exception {
        return this.connection.request(this.id, loadMessage, null).getCallBackData(time, timeUnit);
    }

    /**
     * 异步请求
     * 不会阻塞调用线程
     *
     * @param loadMessage
     *     要发送的负载消息
     * @return
     *     应答，需要调用者从Listener中获取结果
     */
    public CallBackDataListener asyncRequest(LoadMessage loadMessage) {
        return asyncRequest(loadMessage, null);
    }

    /**
     * 异步请求
     * 不会阻塞调用线程
     *
     * @param loadMessage
     *     要请求的负载消息
     * @param callBackBarrier
     *     回调栅栏（用于多个请求时进行统一阻拦）
     * @return
     *     应答，需要调用者从Listener中获取结果
     */
    public CallBackDataListener asyncRequest(LoadMessage loadMessage, CallBackBarrier callBackBarrier) {
        return this.connection.request(this.id, loadMessage, callBackBarrier);
    }

    /**
     * 应答
     *
     * @param key
     *     请求消息的Key
     * @param loadMessage
     *     需要应答的负载消息
     */
    public void reply(String key, LoadMessage loadMessage) {
        this.connection.reply(this.id, key, loadMessage);
    }

    public void closeAll() {
        this.connection.closeAll();
    }

    public void closeReceiver() {
        this.connection.closeReceiver();
    }

    public void closeSender() {
        this.connection.closeSender();
    }

    public String sessionId() {
        return id;
    }

    public MessageExecutor messageExecutor() {
        return this.messageExecutor;
    }
}