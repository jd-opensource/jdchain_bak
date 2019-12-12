package com.jd.blockchain.mocker.data;

public class KvData {

    private String dataAccount;

    private String key;

    private byte[] value;

    private long version;

    public KvData() {
    }

    public KvData(String dataAccount, String key, byte[] value, long version) {
        this.dataAccount = dataAccount;
        this.key = key;
        this.value = value;
        this.version = version;
    }

    public String getDataAccount() {
        return dataAccount;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    public long getVersion() {
        return version;
    }

    public void setDataAccount(String dataAccount) {
        this.dataAccount = dataAccount;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
