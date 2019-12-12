/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: EventEntity
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/11/5 下午11:18
 * Description:
 */
package com.jd.blockchain.consensus.event;

/**
 *
 * @author shaozhuguang
 * @create 2018/11/5
 * @since 1.0.0
 */

public class EventEntity<T> {

    private T entity;

    public T getEntity() {
        return this.entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}