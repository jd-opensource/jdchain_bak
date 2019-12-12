/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.gateway.service.DataRetrievalService
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/15 下午3:08
 * Description:
 */
package com.jd.blockchain.gateway.service;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/15
 * @since 1.0.0
 */

public interface DataRetrievalService {

    String retrieval(String url) throws Exception;
    String retrievalPost(String url, String queryString) throws Exception;
}