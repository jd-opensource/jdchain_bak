package com.jd.blockchain.statetransfer.callback;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;

/**
 * 数据序列差异请求者获得差异内容后需要回调该接口
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public interface DataSequenceWriter {

    /**
     * 差异请求者更新本地数据序列的状态,一次可以更新多个差异元素
     * @param dsInfo 数据序列当前状态信息
     * @param diffContents 需要更新的差异元素数组
     * @return 更新结果编码
     */
    int updateDSInfo(DataSequenceInfo dsInfo, DataSequenceElement[] diffContents);

    /**
     * 差异请求者更新本地数据序列的状态，一次只更新一个差异元素
     * @param dsInfo 数据序列当前状态信息
     * @param diffContent 需要更新的差异元素
     * @return 更新结果编码
     */
    int updateDSInfo(DataSequenceInfo dsInfo, DataSequenceElement diffContent);

}
