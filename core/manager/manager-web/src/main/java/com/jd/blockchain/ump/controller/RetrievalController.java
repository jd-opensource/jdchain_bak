package com.jd.blockchain.ump.controller;

import com.alibaba.fastjson.JSONObject;
import com.jd.blockchain.ump.model.ApiResult;
import com.jd.blockchain.ump.model.ErrorCode;
import com.jd.blockchain.ump.model.UmpConstant;
import com.jd.blockchain.ump.model.penetrate.DataAccountSchema;
import com.jd.blockchain.ump.model.penetrate.SchemaDomain;
import com.jd.blockchain.ump.service.DataAccountUmpService;
import com.jd.blockchain.ump.service.DataRetrievalService;
import com.jd.blockchain.ump.service.UmpStateService;
import com.jd.blockchain.ump.web.RetrievalConfig;
import com.jd.blockchain.utils.ConsoleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author zhaogw
 * date 2019/07/18 17:01
 */
@RestController
@RequestMapping(path = "/schema")
public class RetrievalController {
    private static final Log log = LogFactory.getLog(RetrievalController.class);

    @Autowired
    private DataRetrievalService dataRetrievalService;

    @Autowired
    private DataAccountUmpService dataAccountUmpService;

    @Autowired
    private UmpStateService umpStateService;

    /**
     * add schema by web;
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    public Object addSchema4Web(HttpServletRequest request, @RequestBody SchemaDomain schemaDomain) {
        String result;
        if (RetrievalConfig.getSchemaUrl() == null || RetrievalConfig.getSchemaUrl().length() <= 0) {
            result = "{'message':'OK','data':'" + "schema.retrieval.url is empty" + "'}";
        } else {
            String queryParams = request.getQueryString() == null ? "": request.getQueryString();
            String fullQueryUrl = new StringBuffer(RetrievalConfig.getSchemaUrl())
                    .append(request.getRequestURI())
                    .append(UmpConstant.DELIMETER_QUESTION)
                    .append(queryParams)
                    .toString();
            try {

                result = dataRetrievalService.retrievalPost(fullQueryUrl,JSONObject.toJSONString(schemaDomain));
                ConsoleUtils.info("request = {%s} \r\n result = {%s} \r\n", fullQueryUrl, result);
            } catch (Exception e) {
                result = "{'message':'error','data':'" + e.getMessage() + "'}";
            }
        }
        return JSONObject.parse(result);
    }

    /**
     * add schema;
     * @param request
     * @return
     */
//    @RequestMapping(method = RequestMethod.POST, value = "")
    @Deprecated
    public Object addSchema(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        String result;
        if (RetrievalConfig.getSchemaUrl() == null || RetrievalConfig.getSchemaUrl().length() <= 0) {
            result = "{'message':'OK','data':'" + "schema.retrieval.url is empty" + "'}";
        } else {
            String queryParams = request.getQueryString() == null ? "": request.getQueryString();
            String fullQueryUrl = new StringBuffer(RetrievalConfig.getSchemaUrl())
                    .append(request.getRequestURI())
                    .append(UmpConstant.DELIMETER_QUESTION)
                    .append(queryParams)
                    .toString();
            try {
                result = dataRetrievalService.retrievalPost(fullQueryUrl,jsonObject);
                ConsoleUtils.info("request = {%s} \r\n result = {%s} \r\n", fullQueryUrl, result);
            } catch (Exception e) {
                result = "{'message':'error','data':'" + e.getMessage() + "'}";
            }
        }
        return JSONObject.parse(result);
    }

    /**
     * list schema;
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public Object listSchema(HttpServletRequest request) {
        String result;
        if (RetrievalConfig.getSchemaUrl() == null || RetrievalConfig.getSchemaUrl().length() <= 0) {
            result = "{'message':'OK','data':'" + "schema.retrieval.url is empty" + "'}";
        } else {
            String queryParams = request.getQueryString() == null ? "": request.getQueryString();
            String fullQueryUrl = new StringBuffer(RetrievalConfig.getSchemaUrl())
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

    /**
     * delete schema;
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{schemaId}")
    public Object deleteSchema(HttpServletRequest request) {
        String result;
        if (RetrievalConfig.getSchemaUrl() == null || RetrievalConfig.getSchemaUrl().length() <= 0) {
            result = "{'message':'OK','data':'" + "schema.retrieval.url is empty" + "'}";
        } else {
            String queryParams = request.getQueryString() == null ? "": request.getQueryString();
            String fullQueryUrl = new StringBuffer(RetrievalConfig.getSchemaUrl())
                    .append(request.getRequestURI())
                    .append(UmpConstant.DELIMETER_QUESTION)
                    .append(queryParams)
                    .toString();
            try {
                result = dataRetrievalService.delete(fullQueryUrl);
                ConsoleUtils.info("request = {%s} \r\n result = {%s} \r\n", fullQueryUrl, result);
            } catch (Exception e) {
                result = "{'message':'error','data':'" + e.getMessage() + "'}";
            }
        }
        return JSONObject.parse(result);
    }

    /**
     * start schema;
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/start/{schemaId}")
    public Object startSchema(HttpServletRequest request) {
        String result;
        if (RetrievalConfig.getSchemaUrl()==null || RetrievalConfig.getSchemaUrl().length() <= 0) {
            result = "{'message':'OK','data':'" + "schema.retrieval.url is empty" + "'}";
        } else {
            String queryParams = request.getQueryString() == null ? "": request.getQueryString();
            String fullQueryUrl = new StringBuffer(RetrievalConfig.getSchemaUrl())
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

    /**
     * stop schema;
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/stop/{schemaId}")
    public Object stopSchema(HttpServletRequest request) {
        String result;
        if (RetrievalConfig.getSchemaUrl() == null || RetrievalConfig.getSchemaUrl().length() <= 0) {
            result = "{'message':'OK','data':'" + "schema.retrieval.url is empty" + "'}";
        } else {
            String queryParams = request.getQueryString() == null ? "": request.getQueryString();
            String fullQueryUrl = new StringBuffer(RetrievalConfig.getSchemaUrl())
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

    /**
     * querysql;
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/querysql")
    public Object queryBySql(HttpServletRequest request,@RequestBody String queryString) {
        String result;
        if (RetrievalConfig.getSchemaUrl() == null ||  RetrievalConfig.getSchemaUrl().length() <= 0) {
            result = "{'message':'OK','data':'" + "schema.retrieval.url is empty" + "'}";
        } else {
            String queryParams = request.getQueryString() == null ? "": request.getQueryString();
            String fullQueryUrl = new StringBuffer(RetrievalConfig.getSchemaUrl())
                    .append(request.getRequestURI())
                    .append(UmpConstant.DELIMETER_QUESTION)
                    .append(queryParams)
                    .toString();
            try {
                result = dataRetrievalService.retrievalPost(fullQueryUrl,queryString);
                ConsoleUtils.info("request = {%s} \r\n result = {%s} \r\n", fullQueryUrl, result);
            } catch (Exception e) {
                result = "{'message':'error','data':'" + e.getMessage() + "'}";
            }
        }
        return JSONObject.parse(result);
    }

    /**
     * add dataAccountSchema;
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/addDataAccountSchema")
    public ApiResult addDataAccountSchema( @RequestBody DataAccountSchema dataAccountSchema) {
        try {
            dataAccountUmpService.addDataAccountSchema(dataAccountSchema);
            return new ApiResult(ErrorCode.SUCCESS);
        } catch (Exception e) {
            return new ApiResult(ErrorCode.SERVER_ERROR,e.getMessage());
        }
    }

    /**
     * delete dataAccountSchema;
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/delDataAccountSchema/ledger/{ledgerHash}/account/{dataAccount}")
    public ApiResult deleteDataAccountSchema(@PathVariable(name = "ledgerHash") String ledgerHash,
                                           @PathVariable(name = "dataAccount") String dataAccount) {
        try {
            dataAccountUmpService.deleteDataAcccountSchema(ledgerHash, dataAccount);
            return new ApiResult(ErrorCode.SUCCESS);
        } catch (Exception e) {
            return new ApiResult(ErrorCode.SERVER_ERROR,e.getMessage());
        }
    }

    /**
     * find dataAccountSchema;
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/findDataAccountSchema/ledger/{ledgerHash}/account/{dataAccount}")
    public ApiResult findDataAccountSchema(@PathVariable(name = "ledgerHash") String ledgerHash,
                                           @PathVariable(name = "dataAccount") String dataAccount) {
        try {
            DataAccountSchema dataAccountSchema = dataAccountUmpService.findDataAccountSchema(ledgerHash, dataAccount);
            return new ApiResult(ErrorCode.SUCCESS,dataAccountSchema);
        } catch (Exception e) {
            return new ApiResult(ErrorCode.SERVER_ERROR,e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ledgers")
    public String getAllLedgers() {
        //generate the url;
        int peerPort = umpStateService.peerPort();
        String url = "http://localhost:"+peerPort+"/ledgers";
        try {
            return dataRetrievalService.retrieval(url);
        } catch (Exception e) {
            return  "{'success':false,'data':'" + e.getMessage() + "'}";
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ledger/{ledgerHash}")
    public String getAllDataAccounts(@PathVariable(name = "ledgerHash") String ledgerHash) {
        //generate the url;
        int peerPort = umpStateService.peerPort();
        String url = "http://localhost:"+peerPort+"/ledgers/"+ledgerHash+"/accounts";
        try {
            return dataRetrievalService.retrieval(url);
        } catch (Exception e) {
            return  "{'success':false,'data':'" + e.getMessage() + "'}";
        }
    }
}