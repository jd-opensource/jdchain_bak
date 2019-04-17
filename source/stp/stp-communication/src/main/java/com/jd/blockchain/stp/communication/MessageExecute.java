/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.MessageExecute
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 上午10:59
 * Description:
 */
package com.jd.blockchain.stp.communication;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public interface MessageExecute {

    byte[] receive(String key, byte[] data, RemoteSession session);

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