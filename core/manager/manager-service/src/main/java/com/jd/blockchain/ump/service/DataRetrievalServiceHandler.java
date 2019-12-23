/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.gateway.service.DeepQueryServiceImpl
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/15 下午3:09
 * Description:
 */
package com.jd.blockchain.ump.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.blockchain.ump.util.HttpClientPool;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/15
 * @since 1.0.0
 */
@Component
public class DataRetrievalServiceHandler implements DataRetrievalService {

    @Override
    public String retrieval(String url) throws Exception {
        return HttpClientPool.get(url);
    }

    @Override
    public String retrievalPost(String url, JSONObject jsonObject) throws Exception {
        return HttpClientPool.jsonPost(url,jsonObject.toJSONString());
    }

    @Override
    public String delete(String url) throws Exception {
        return HttpClientPool.delete(url);
    }

    @Override
    public String retrievalPost(String url, String queryString) throws Exception {
        return HttpClientPool.jsonPost(url,queryString);
    }
}