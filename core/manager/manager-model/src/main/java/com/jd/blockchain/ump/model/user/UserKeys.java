package com.jd.blockchain.ump.model.user;

public class UserKeys {

    private int id;

    private String name;

    private String privKey;

    private String pubKey;

    private String encodePwd;

    public UserKeys() {
    }

    public UserKeys(String name, String privKey, String pubKey, String encodePwd) {
        this.name = name;
        this.privKey = privKey;
        this.pubKey = pubKey;
        this.encodePwd = encodePwd;
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

    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getEncodePwd() {
        return encodePwd;
    }

    public void setEncodePwd(String encodePwd) {
        this.encodePwd = encodePwd;
    }

    public UserKeysVv toUserKeysVv() {
        return new UserKeysVv(id, name, privKey, pubKey);
    }
}
