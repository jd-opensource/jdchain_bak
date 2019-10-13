package com.jd.blockchain.ledger;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * 即将要注册的参与方的信息
 * @author zhangshuang
 * @create 2019/7/8
 * @since 1.0.0
 */
public class ParticipantInfoData implements ParticipantInfo {


    private String name;

    private PubKey pubKey;

    private NetworkAddress networkAddress;

//    private String flag;//代表注册参与方或者删除参与方

    public ParticipantInfoData(String name, PubKey pubKey, NetworkAddress networkAddress) {
        this.name = name;
        this.pubKey = pubKey;
        this.networkAddress = networkAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public PubKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
    }

    @Override
    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    public void setNetworkAddress(NetworkAddress networkAddress) {
        this.networkAddress = networkAddress;
    }
}
