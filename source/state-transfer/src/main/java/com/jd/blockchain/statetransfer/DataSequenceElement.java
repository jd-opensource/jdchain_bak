package com.jd.blockchain.statetransfer;

/**
 *数据序列复制的元素或单位
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public class DataSequenceElement {

    //数据序列的唯一标识符；
    private String id;

    //数据序列的某个高度；
    private long height;

    //对应某个高度的数据序列内容
    private byte[][] data;

    public DataSequenceElement(String id, long height, byte[][] data) {
        this.id = id;
        this.height = height;
        this.data = data;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }

    public byte[][] getData() {
        return data;
    }

    public void setData(byte[][] data) {
        this.data = data;
    }
}
