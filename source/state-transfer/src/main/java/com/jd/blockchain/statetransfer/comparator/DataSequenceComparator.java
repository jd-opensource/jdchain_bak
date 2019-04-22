package com.jd.blockchain.statetransfer.comparator;

import com.jd.blockchain.statetransfer.DataSequenceElement;

import java.util.Comparator;

/**
 *
 *
 */
public class DataSequenceComparator implements Comparator<DataSequenceElement> {

    // sort by data sequence height
    @Override
    public int compare(DataSequenceElement o1, DataSequenceElement o2) {
        long height1;
        long height2;

        height1 = o1.getHeight();
        height2 = o2.getHeight();

        return (int) (height1 - height2);
    }
}
