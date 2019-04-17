/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.SessionMessage
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/16 上午10:40
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/16
 * @since 1.0.0
 */

public class SessionMessage extends AbstractMessage implements IMessage {

    private String localHost;

    private int listenPort;

    private String messageExecute;

    public SessionMessage() {
    }

    public SessionMessage(String localHost, int listenPort, String messageExecute) {
        this.localHost = localHost;
        this.listenPort = listenPort;
        this.messageExecute = messageExecute;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }


    public int getListenPort() {
        return listenPort;
    }

    public String getMessageExecute() {
        return messageExecute;
    }

    public void setMessageExecute(String messageExecute) {
        this.messageExecute = messageExecute;
    }

    public String sessionId() {
        return Hex.encodeHexString((this.localHost + ":" + this.listenPort).getBytes());
    }

    public static SessionMessage toNodeSessionMessage(Object msg) {
        String msgString = msg.toString();
        try {
            String[] msgArray = msgString.split("\\|");
            if (msgArray.length == 2 || msgArray.length == 3) {
                String host = msgArray[0];
                int port = Integer.parseInt(msgArray[1]);
                String msgExecuteClass = null;
                if (msgArray.length == 3) {
                    msgExecuteClass = msgArray[2];
                }
                return new SessionMessage(host, port, msgExecuteClass);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toTransfer() {
        // 为区别于TransferMessage的JSON格式，该处使用字符串连接处理
        // 格式：localHost|port|class
        String transferMsg = this.localHost + "|" + this.listenPort + "|" + this.messageExecute;
        return transferMsg;
    }
}