/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.message.SessionMessage
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/16 上午10:40
 * Description:
 */
package com.jd.blockchain.stp.communication.message;

import org.apache.commons.codec.binary.Hex;

/**
 * Session消息
 * 该消息用于发送至远端节点，告诉远端节点本地的信息
 * @author shaozhuguang
 * @create 2019/4/16
 * @since 1.0.0
 */

public class SessionMessage extends AbstractMessage implements IMessage {

    /**
     * 本地节点HOST
     */
    private String localHost;

    /**
     * 本地节点监听端口
     */
    private int listenPort;

    /**
     * 远端接收到本地节点信息时处理的Class
     */
    private String messageExecutor;

    public SessionMessage() {
    }

    public SessionMessage(String localHost, int listenPort, String messageExecutor) {
        this.localHost = localHost;
        this.listenPort = listenPort;
        this.messageExecutor = messageExecutor;
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

    public String getMessageExecutor() {
        return messageExecutor;
    }

    public void setMessageExecutor(String messageExecutor) {
        this.messageExecutor = messageExecutor;
    }

    public String sessionId() {
        return Hex.encodeHexString((this.localHost + ":" + this.listenPort).getBytes());
    }

    /**
     * 将对象（或者说接收到的消息）转换为SessionMessage
     * @param msg
     *     接收到的消息对象
     * @return
     *     可正确解析则返回，否则返回NULL
     */
    public static SessionMessage toSessionMessage(Object msg) {
        String msgString = msg.toString();
        try {
            String[] msgArray = msgString.split("\\|");
            if (msgArray.length == 2 || msgArray.length == 3) {
                String host = msgArray[0];
                int port = Integer.parseInt(msgArray[1]);
                String msgExecutorClass = null;
                if (msgArray.length == 3) {
                    msgExecutorClass = msgArray[2];
                }
                return new SessionMessage(host, port, msgExecutorClass);
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
        if (this.messageExecutor == null) {
            return this.localHost + "|" + this.listenPort;
        } else {
            return this.localHost + "|" + this.listenPort + "|" + this.messageExecutor;
        }
    }
}