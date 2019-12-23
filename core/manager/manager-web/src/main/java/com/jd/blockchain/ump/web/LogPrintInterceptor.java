package com.jd.blockchain.ump.web;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class LogPrintInterceptor implements HandlerInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录日志
        // 请求的参数：
        String parameters = "";
        Map<String, String[]> requestParameters = request.getParameterMap();
        if (requestParameters != null && !requestParameters.isEmpty()) {
            parameters = JSON.toJSONString(requestParameters);
        }
        LOGGER.info("Request[{}][{}], parameters=[{}]",
                request.getRequestURL().toString(), // 请求URL
                request.getMethod(), // 请求的方法
                parameters); // 请求的参数

        return true;
    }
}
