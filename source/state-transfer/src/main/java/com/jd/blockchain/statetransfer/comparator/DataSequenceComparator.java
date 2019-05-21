package com.jd.blockchain.statetransfer.comparator;

import com.jd.blockchain.statetransfer.DataSequenceElement;

import java.util.Comparator;

/**
 * 数据序列差异元素的高度比较器
 * @author zhangshuang
 * @create 2019/4/18
 * @since 1.0.0
 */
public class DataSequenceComparator implements Comparator<DataSequenceElement> {

    // sort by data sequence height
    /**
     * 对差异元素根据高度大小排序
     * @param o1 差异元素1
     * @param o2 差异元素2
     * @return >0 or <0
     */
    @Override
    public int compare(DataSequenceElement o1, DataSequenceElement o2) {
        long height1;
        long height2;

        height1 = o1.getHeight();
        height2 = o2.getHeight();

        return (int) (height1 - height2);
    }
}
