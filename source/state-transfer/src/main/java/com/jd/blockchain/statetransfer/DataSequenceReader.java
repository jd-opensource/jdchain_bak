package com.jd.blockchain.statetransfer;
/**
 *数据序列差异的提供者需要使用的回调接口
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public interface DataSequenceReader {

    /**
     * 差异的提供者根据输入的数据序列标识符获取当前的数据序列信息；
     *
     */
    DataSequenceInfo getDSInfo(String id);


    /**
     * 差异的提供者根据输入的数据序列标识符以及起始，结束高度获得数据序列的差异内容；
     *
     */
    DataSequenceElement[] getDSContent(String id, long from, long to);
}
