package com.jd.blockchain.statetransfer.callback;

import com.jd.blockchain.statetransfer.DataSequenceElement;

/**
 *数据序列差异的请求者获得差异内容后需要回调该接口，通过接口提供的方法对指定数据序列执行差异内容的重放，并更新数据序列的当前状态;
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public interface DataSequenceWriter {

    /**
     *更新数据序列的当前状态，一次更新多个高度的差异
     * return void
     */
    int updateDSInfo(String id, DataSequenceElement[] diffContents);

    /**
     *更新数据序列的当前状态，一次更新一个高度的差异
     * return void
     */
    int updateDSInfo(String id, DataSequenceElement diffContents);

}
