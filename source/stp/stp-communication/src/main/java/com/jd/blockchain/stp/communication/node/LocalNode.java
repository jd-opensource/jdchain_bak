/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.node.LocalNode
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/16 下午3:12
 * Description:
 */
package com.jd.blockchain.stp.communication.node;

import com.jd.blockchain.stp.communication.MessageExecutor;

/**
 * 本地节点
 * @author shaozhuguang
 * @create 2019/4/16
 * @since 1.0.0
 * @date 2019-04-19 09:28
 */

public class LocalNode extends RemoteNode {

    /**
     * 当前节点消息处理器
     * 该消息处理器用于描述远端节点收到当前节点的消息该如何处理
     * 通常该消息处理器会以字符串的形式发送至远端节点
     */
    private Class<?> messageExecutorClass;

    /**
     * 当前节点接收消息默认处理器
     * 与messageExecutor不同，该字段描述的是当前节点接收到其他节点信息时的默认处理器
     * 该参数硬性要求必须不能为空
     */
    private MessageExecutor defaultMessageExecutor;

    /**
     * 构造器
     * @param hostName
     *     当前节点Host，该Host必须是一种远端节点可访问的形式
     * @param port
     *     当前节点监听端口
     * @param defaultMessageExecutor
     *     当前节点接收到远端消息无法处理时的消息处理器
     *
     */
    public LocalNode(String hostName, int port, MessageExecutor defaultMessageExecutor) {
        this(hostName, port, null, defaultMessageExecutor);
    }

    /**
     * 构造器
     * @param hostName
     *     当前节点Host，该Host必须是一种远端节点可访问的形式
     * @param port
     *     当前节点监听端口
     * @param messageExecutorClass
     *     当前节点期望远端节点接收到消息后的处理器
     * @param defaultMessageExecutor
     *     当前节点接收到远端消息无法处理时的消息处理器
     *
     */
    public LocalNode(String hostName, int port, Class<?> messageExecutorClass, MessageExecutor defaultMessageExecutor) {
        super(hostName, port);
        this.messageExecutorClass = messageExecutorClass;
        this.defaultMessageExecutor = defaultMessageExecutor;
    }

    /**
     * 返回消息执行器的类对应的字符串
     * 该返回值通常用于消息传递
     * @return
     */
    public String messageExecutorClass() {
        if (this.messageExecutorClass == null) {
            return null;
        }
        return this.messageExecutorClass.getName();
    }

    /**
     * 返回默认的消息处理器
     * @return
     */
    public MessageExecutor defaultMessageExecutor() {
        return this.defaultMessageExecutor;
    }
}