/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.exchange.EventProducer
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午11:15
 * Description:
 */
package com.jd.blockchain.consensus.event;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public interface EventProducer<T> {

    public void publish(T t);
}