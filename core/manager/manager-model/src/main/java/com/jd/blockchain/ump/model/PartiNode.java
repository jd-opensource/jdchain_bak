package com.jd.blockchain.ump.model;

import java.util.ArrayList;
import java.util.List;

public class PartiNode {

    private int id;

    private String name;

    private String pubKey;

    private String initHost;

    private int initPort;

    private boolean isSecure;

    public List<String> toConfigChars(List<String> partiRoleConfigs) {

        List<String> configCharList = new ArrayList<>();

        configCharList.add(formatConfig(UmpConstant.PARTINODE_NAME_FORMAT, name));

        configCharList.add(formatConfig(UmpConstant.PARTINODE_PUBKEY_FORMAT, pubKey));

        if (partiRoleConfigs != null && !partiRoleConfigs.isEmpty()) {
            configCharList.addAll(partiRoleConfigs);
        }

        configCharList.add(formatConfig(UmpConstant.PARTINODE_INIT_HOST_FORMAT, initHost));

        configCharList.add(formatConfig(UmpConstant.PARTINODE_INIT_PORT_FORMAT, initPort));

        configCharList.add(formatConfig(UmpConstant.PARTINODE_INIT_SECURE_FORMAT, isSecure));

        return configCharList;
    }

    private String formatConfig(String formatter, Object value) {
        return String.format(formatter, id) + "=" + value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getInitHost() {
        return initHost;
    }

    public void setInitHost(String initHost) {
        this.initHost = initHost;
    }

    public int getInitPort() {
        return initPort;
    }

    public void setInitPort(int initPort) {
        this.initPort = initPort;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void setSecure(boolean secure) {
        isSecure = secure;
    }
}
