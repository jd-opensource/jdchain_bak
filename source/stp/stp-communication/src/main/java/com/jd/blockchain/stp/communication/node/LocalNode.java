/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.node.LocalNode
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/16 下午3:12
 * Description:
 */
package com.jd.blockchain.stp.communication.node;

import com.jd.blockchain.stp.communication.MessageExecute;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/16
 * @since 1.0.0
 */

public class LocalNode extends RemoteNode {

    private Class<?> messageExecute;

    public LocalNode(String hostName, int port) {
        super(hostName, port);
    }

    public LocalNode(String hostName, int port, MessageExecute messageExecute) {
        super(hostName, port);
        this.messageExecute = messageExecute.getClass();
    }

    public LocalNode(String hostName, int port, Class<?> messageExecute) {
        super(hostName, port);
        this.messageExecute = messageExecute;
    }

    public String messageExecuteClass() {
        if (this.messageExecute == null) {
            return null;
        }
        return this.messageExecute.getName();
    }

    public void setMessageExecute(MessageExecute messageExecute) {
        this.messageExecute = messageExecute.getClass();
    }

    public void setMessageExecute(Class<?> messageExecute) {
        this.messageExecute = messageExecute;
    }
}