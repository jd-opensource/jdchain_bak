package com.jd.blockchain.ledger;

/**
 * for BlockBrowserController.java, param is json ,then match it;
 * @author zhaogw
 * date 2019/5/14 14:19
 */
public class KVInfoVO {
    private KVDataVO[] data;

    public KVInfoVO() {
    }

    public KVInfoVO(KVDataVO[] data) {
        this.data = data;
    }

    public KVDataVO[] getData() {
        return data;
    }

    public void setData(KVDataVO[] data) {
        this.data = data;
    }
}
