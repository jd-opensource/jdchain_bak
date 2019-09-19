package com.jd.blockchain.ump.util;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.ump.model.MasterAddr;
import com.jd.blockchain.ump.model.web.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpJsonClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpJsonClientUtils.class);

    public static <T> T httpPost(MasterAddr masterAddr, String url, Object body, Class<T> returnType, boolean isWrapper) {

        try {
            String responseJson = HttpClientPool.jsonPost(masterAddr.toHttpUrl() + url, JSON.toJSONString(body));

            LOGGER.info("Http Post Receive info =[ {} ] from {} ", responseJson, masterAddr.toHttpUrl() + url);

            return response(responseJson, returnType, isWrapper);
        } catch (Exception e) {

            LOGGER.error("HttpPostRequestException {}", e.getMessage());

            throw new IllegalStateException(e);
        }
    }

    public static <T> T httpGet(String url, Class<T> returnType, boolean isWrapper) {
        try {
            String responseJson = HttpClientPool.get(url);

            LOGGER.info("Http Get Receive info =[ {} ] from {} ", responseJson, url);

            return response(responseJson, returnType, isWrapper);

        } catch (Exception e) {

            LOGGER.error("HttpGetRequestException {}", e.toString());

            throw new IllegalStateException(e);
        }
    }

    private static <T> T response(String responseJson, Class<T> returnType, boolean isWrapper) {
        if (isWrapper) {
            // 封装类型的情况下使用的是WebResponse
            WebResponse<T> webResponse = JSON.parseObject(responseJson, WebResponse.class);
            LOGGER.info("Wrapper JSON Data = {}", JSON.toJSONString(webResponse));
            return webResponse.getData();
        }

        if (!JSON.isValid(responseJson)) {
            return (T)responseJson;
        }
        // 对responseJson进行转换
        T data = JSON.parseObject(responseJson, returnType);
        LOGGER.info("UnWrapper JSON Data = {}", JSON.toJSONString(data));
        return data;
    }
}
