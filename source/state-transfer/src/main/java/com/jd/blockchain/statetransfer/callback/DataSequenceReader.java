package com.jd.blockchain.statetransfer.callback;

import com.jd.blockchain.statetransfer.DataSequenceElement;
import com.jd.blockchain.statetransfer.DataSequenceInfo;

/**
 * 数据序列差异提供者需要使用的回调接口
 * @author zhangshuang
 * @create 2019/4/11
 * @since 1.0.0
 */
public interface DataSequenceReader {

    /**
     * 差异提供者根据数据序列标识符获取数据序列当前状态；
     * @param id 数据序列标识符
     * @return 数据序列当前状态信息
     */
    DataSequenceInfo getDSInfo(String id);


    /**
     * 差异提供者根据数据序列标识符以及起始，结束高度提供数据序列该范围的差异内容；
     * @param id 数据序列标识符
     * @param from 差异的起始高度
     * @param to 差异的结束高度
     * @return 差异元素组成的数组
     */
    DataSequenceElement[] getDSDiffContent(String id, long from, long to);


    /**
     * 差异提供者根据数据序列标识符以及高度提供数据序列的差异内容；
     * @param id 数据序列标识符
     * @param height 要获得哪个高度的差异元素
     * @return 差异元素
     */
    DataSequenceElement getDSDiffContent(String id, long height);
}
