package com.jd.blockchain.ump.controller;

import com.alibaba.fastjson.JSONObject;
import com.jd.blockchain.ump.model.UmpConstant;
import com.jd.blockchain.ump.service.DataRetrievalService;
import com.jd.blockchain.ump.web.RetrievalConfig;
import com.jd.blockchain.utils.ConsoleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author zhaogw
 * date 2019/07/18 17:01
 */
@RestController
@RequestMapping(path = "/tasks")
public class TaskRetrievalController {
    private static final Log log = LogFactory.getLog(TaskRetrievalController.class);

//    @Value("${task.retrieval.url}")
//    private String taskRetrievalUrl;

    @Autowired
    private DataRetrievalService dataRetrievalService;

    /**
     * get the nums of all tasks;
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "")
    public Object listSchema(HttpServletRequest request) {
        String result;
        if (RetrievalConfig.getTaskUrl() == null || RetrievalConfig.getTaskUrl().length() <= 0) {
            result = "{'message':'OK','data':'" + "schema.retrieval.url is empty" + "'}";
        } else {
            String queryParams = request.getQueryString() == null ? "": request.getQueryString();
            String fullQueryUrl = new StringBuffer(RetrievalConfig.getTaskUrl())
                    .append(request.getRequestURI())
                    .append(UmpConstant.DELIMETER_QUESTION)
                    .append(queryParams)
                    .toString();
            try {
                result = dataRetrievalService.retrieval(fullQueryUrl);
                ConsoleUtils.info("request = {%s} \r\n result = {%s} \r\n", fullQueryUrl, result);
            } catch (Exception e) {
                result = "{'message':'error','data':'" + e.getMessage() + "'}";
            }
        }
        return JSONObject.parse(result);
    }

}