package com.jd.blockchain.ump.model;

public class MasterAddr {

    private String ipAddr;

    private int port;

    public MasterAddr() {
    }

    public MasterAddr(String ipAddr, int port) {
        this.ipAddr = ipAddr;
        this.port = port;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static MasterAddr newInstance(String ipAddr, int port) {
        return new MasterAddr(ipAddr, port);
    }

    public String toHttpUrl() {
        return "http://" + ipAddr + ":" + port;
    }

    public boolean legal() {
        if (this.ipAddr == null || this.ipAddr.length() == 0 || this.port == 0) {
            return false;
        }
        return true;
    }
}
