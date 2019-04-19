/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.MessageExecutor
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/11 上午10:59
 * Description:
 */
package com.jd.blockchain.stp.communication;

/**
 * 消息执行器
 * 该执行器由其他应用实现
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 * @date 2019-04-18 15:29
 */

public interface MessageExecutor {

    /**
     * 接收到receive消息如何处理
     *
     * @param key
     *     请求消息的Key，调用者需要在应答时通过该Key应答远端
     * @param data
     *     请求消息的内容
     * @param session
     *     远端Session，描述该消息是从哪发送来的
     * @return
     *     应答结果
     */
    byte[] receive(String key, byte[] data, RemoteSession session);

    /**
     * 应答方式
     *
     * @return
     *     参考：{@link REPLY}
     */
    REPLY replyType();

    // 应答方式
    enum REPLY {
        // 手动应答：Receiver不会自动发送应答请求，需要调用
        // session.reply(String key, LoadMessage loadMessage) 或
        // asyncReply(String key, LoadMessage loadMessage)
        MANUAL,

        // 自动应答：Receiver会根据receive方法的应答结果自动调用应答
        // 使用者不能重新调用
        AUTO,
        ;
    }
}