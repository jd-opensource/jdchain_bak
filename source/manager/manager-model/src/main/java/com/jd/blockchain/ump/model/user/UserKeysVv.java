package com.jd.blockchain.ump.model.user;

public class UserKeysVv {

    public static final int PRIVKEY_HEADER_LENGTH = 4;

    public static final int PRIVKEY_TAIL_LENGTH = 8;

    public static final String PRIVKEY_HIDE_CONTENT = "******";

    private int id;

    private String name;

    private String privKey;

    private String pubKey;

    public UserKeysVv() {
    }

    public UserKeysVv(int id, String name, String privKey, String pubKey) {
        this.id = id;
        this.name = name;
        this.privKey = encodePrivKey(privKey);
        this.pubKey = pubKey;
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

    private String encodePrivKey(final String privKey) {
        if (privKey != null && privKey.length() > (PRIVKEY_HEADER_LENGTH + PRIVKEY_TAIL_LENGTH)) {
            return privKey.substring(0, PRIVKEY_HEADER_LENGTH) +
                   PRIVKEY_HIDE_CONTENT +
                   privKey.substring(privKey.length() - PRIVKEY_TAIL_LENGTH);
        }
        return privKey;
    }
}
