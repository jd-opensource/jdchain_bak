/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.RemoteNode
 * Author: shaozhuguang
 * Department: Y事业部
 * Date: 2019/4/11 下午3:40
 * Description:
 */
package com.jd.blockchain.stp.communication.node;

/**
 *
 * @author shaozhuguang
 * @create 2019/4/11
 * @since 1.0.0
 */

public class RemoteNode {

    private int port;

    private String hostName;

    public RemoteNode(String hostName, int port) {
        this.port = port;
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public int hashCode() {
        return (hostName + ":" + port).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof RemoteNode) {
            RemoteNode other = (RemoteNode) obj;
            if (this.hashCode() == other.hashCode()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.hostName + ":" + this.port;
    }
}