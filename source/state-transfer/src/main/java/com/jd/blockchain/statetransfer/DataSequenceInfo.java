package com.jd.blockchain.statetransfer;

/**
 *共识结点上的某个数据序列的当前状态信息，每个共识结点可以对应任意个数据序列；
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public class DataSequenceInfo {

    //数据序列的唯一标识
    private String id;

    //数据序列的当前高度
    private long height;

    public DataSequenceInfo(String id, long height) {
        this.id = id;
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }
}
