package com.jd.blockchain.utils;

import static com.jd.blockchain.utils.BaseConstant.QUERY_LIST_MAX;

/**
 * @author zhaogw
 * date 2019/2/22 17:00
 */
public class QueryUtil {

    /**
     * confirm the fromIndex and count by 3 factors;
     * @param fromIndex fromIndex
     * @param count count
     * @param maxNum maxNum
     * @return int[]
     */
    public static int[] calFromIndexAndCount(int fromIndex, int count, int maxNum){
        int [] rtn = new int[2];
        if (fromIndex < 0 || fromIndex >= maxNum) {
            fromIndex = 0;
        }

        //must < maxNum;
        if(count > maxNum){
            count = maxNum;
        }
        //must < threshold;
        if(count > QUERY_LIST_MAX){
            count = QUERY_LIST_MAX;
        }
        //if count is empty, get the small;
        if (count == -1) {
            fromIndex = 0;
            //count must <=100;
            count = maxNum > QUERY_LIST_MAX ? QUERY_LIST_MAX : maxNum;
        }
        //count is ok, then calculate the plus condition;
        if (fromIndex + count >= maxNum) {
            count = maxNum - fromIndex;
        }
        //now if count<-1, then deduce: make trouble;so set count=0;
        if(count < -1){
            count = 0;
        }
        rtn[0] = fromIndex;
        rtn[1] = count;
        return rtn;
    }

    /**
     * cal the data by descend;
     * @param fromIndex
     * @param count
     * @param maxNum
     * @return
     */
    public static int[] calFromIndexAndCountDescend(int fromIndex, int count, int maxNum){
        int rtn[] = new int[2];
        int results[] = calFromIndexAndCount(fromIndex,count,maxNum);

        //now use descend; first show the latest record;
        rtn[0] = maxNum - results[0] - results[1];
        rtn[1] = results[1];
        return rtn;
    }
}
