package com.jd.blockchain.ledger;

/**
 * @author zhaogw
 * date 2019/5/14 14:17
 */
public class KVDataVO {
    private String key;
    private long[] version;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long[] getVersion() {
        return version;
    }

    public void setVersion(long[] version) {
        this.version = version;
    }
}
