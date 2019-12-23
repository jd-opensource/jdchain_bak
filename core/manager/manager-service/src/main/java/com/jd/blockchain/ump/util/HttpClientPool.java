/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.utils.http.agent.HttpClientPool
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/14 下午3:20
 * Description:
 */
package com.jd.blockchain.ump.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/14
 * @since 1.0.0
 */

public class HttpClientPool {

    private static final int TEN_MINUTE = 600 * 1000;

    private static final int TIME_OUT = TEN_MINUTE;

    private static final int CONNECT_TIME_OUT = TEN_MINUTE;

    private static final int SOCKET_TIME_OUT = TEN_MINUTE;

    private static final int MAX_TOTAL = 200;

    private static final int MAX_PER_ROUTE = 40;

    private static final int MAX_ROUTE = 100;

    private static final int RETRY_COUNT = 5;

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final Map<String, CloseableHttpClient> httpClients = new ConcurrentHashMap<>();

    private final static Lock lock = new ReentrantLock();

    private static void config(HttpRequestBase httpRequestBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(TIME_OUT)
                .setConnectTimeout(CONNECT_TIME_OUT)
                .setSocketTimeout(SOCKET_TIME_OUT)
                .build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient对象
     *
     * @param url
     * @return
     */
    public static CloseableHttpClient getHttpClient(String url) {
        String hostName = url.split("/")[2];
        int port = 80;
        if (hostName.contains(":")) {
            String[] arr = hostName.split(":");
            hostName = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        return getHttpClient(hostName, port);
    }

    /**
     * 获取HttpClient对象
     *
     * @param hostName
     * @param port
     * @return
     */
    public static CloseableHttpClient getHttpClient(String hostName, int port) {
        String key = hostName + ":" + port;
        CloseableHttpClient httpClient = httpClients.get(key);
        if (httpClient == null) {
            try {
                lock.lock();
                if (httpClient == null) {
                    httpClient = createHttpClient(MAX_TOTAL, MAX_PER_ROUTE, MAX_ROUTE, hostName, port);
                    httpClients.put(key, httpClient);
                }
            } finally {
                lock.unlock();
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient
     *
     * @param maxTotal
     * @param maxPerRoute
     * @param maxRoute
     * @param hostname
     * @param port
     * @return
     */
    public static CloseableHttpClient createHttpClient(int maxTotal,
                                                       int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);
        HttpRequestRetryHandler httpRequestRetryHandler = (exception, executionCount, context) -> {
            if (executionCount >= RETRY_COUNT) {// 最多重试5次
                return false;
            }else if (exception instanceof NoHttpResponseException) {
                return true;
            }else if (exception instanceof SSLException) {
                return false;
            }else if (exception instanceof InterruptedIOException) {
                return false;
            }else if (exception instanceof SSLHandshakeException) {
                return false;
            }else if (exception instanceof UnknownHostException) {
                return false;
            }else if (exception instanceof ConnectTimeoutException) {
                return false;
            }

            HttpClientContext clientContext = HttpClientContext
                    .adapt(context);
            HttpRequest request = clientContext.getRequest();
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();

        return httpClient;
    }

    private static void setPostParams(HttpPost httpPost,
                                      Map<String, Object> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nameValuePairs.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void setJsonPostParams(HttpPost httpPost, String json) {
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
    }

    /**
     * POST请求
     *
     * @param url
     * @param params
     * @return String
     * @throws IOException
     */
    public static String post(String url, Map<String, Object> params) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        config(httpPost);
        setPostParams(httpPost, params);
        try (CloseableHttpResponse response = httpPost(url, httpPost)) {
            return parseResponse(response);
        }
    }

    public static String jsonPost(String url, String json) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        config(httpPost);
        setJsonPostParams(httpPost, json);
        try (CloseableHttpResponse response = httpPost(url, httpPost)) {
            return parseResponse(response);
        }
    }

    /**
     * GET请求
     *
     * @param url
     * @return String
     */
    public static String get(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        config(httpGet);
        try (CloseableHttpResponse response = httpGet(url, httpGet)) {
            return parseResponse(response);
        }
    }

    /**
     * Get请求的真实执行
     *
     * @param url
     * @param httpGet
     * @return
     * @throws IOException
     */
    private static CloseableHttpResponse httpGet(String url, HttpGet httpGet) throws IOException {
        return getHttpClient(url)
                .execute(httpGet, HttpClientContext.create());
    }

    /**
     * POST请求的真实执行
     *
     * @param url
     * @param httpPost
     * @return
     * @throws IOException
     */
    private static CloseableHttpResponse httpPost(String url, HttpPost httpPost) throws IOException {
        return getHttpClient(url)
                .execute(httpPost, HttpClientContext.create());
    }

    /**
     * 解析response
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static String parseResponse(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, DEFAULT_CHARSET);
        EntityUtils.consume(entity);
        return result;
    }

    public static String delete(String url) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        config(httpDelete);
        try (CloseableHttpResponse response = httpDelete(url, httpDelete)) {
            return parseResponse(response);
        }
    }

    private static CloseableHttpResponse httpDelete(String url, HttpDelete httpDelete) throws IOException {
        return getHttpClient(url)
                .execute(httpDelete, HttpClientContext.create());
    }


}