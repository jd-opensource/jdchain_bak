package com.jd.blockchain.statetransfer.result;

/**
 * 数据序列差异提供者解码请求者"CMD_GETDSDIFF_REQUEST"消息时得到的结果
 * @author zhangshuang
 * @create 2019/4/18
 * @since 1.0.0
 */
public class DSDiffRequestResult {

    String id;
    long fromHeight;
    long toHeight;

    public DSDiffRequestResult(String id ,long fromHeight, long toHeight) {
        this.id = id;
        this.fromHeight = fromHeight;
        this.toHeight = toHeight;
    }

    public String getId() {
        return id;
    }

    public long getFromHeight() {
        return fromHeight;
    }

    public long getToHeight() {
        return toHeight;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFromHeight(long fromHeight) {
        this.fromHeight = fromHeight;
    }

    public void setToHeight(long toHeight) {
        this.toHeight = toHeight;
    }

}
