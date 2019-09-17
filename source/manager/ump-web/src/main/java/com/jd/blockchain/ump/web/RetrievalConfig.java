package com.jd.blockchain.ump.web;

import org.springframework.beans.BeansException;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.jd.blockchain.ump.model.UmpConstant.SCHEMA_RETRIEVAL_URL;
import static com.jd.blockchain.ump.model.UmpConstant.TASK_RETRIEVAL_URL;

public class RetrievalConfig {

    private static Map<String,String> propertiesMap = new HashMap<>();

    private String schemaUrl;
    private String taskUrl;

    public static void processProperties(Properties props) throws BeansException {
        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            try {
                // PropertiesLoaderUtils的默认编码是ISO-8859-1,在这里转码一下
                propertiesMap.put(keyStr, new String(props.getProperty(keyStr).getBytes("ISO-8859-1"), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadAllProperties(String propertyFileName) {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties(propertyFileName);
            processProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        return propertiesMap.get(name).toString();
    }

    public static Map<String, String> getAllProperty() {
        return propertiesMap;
    }

    public static String getSchemaUrl() {
        return propertiesMap.get(SCHEMA_RETRIEVAL_URL);
    }

    public static String getTaskUrl() {
        return propertiesMap.get(TASK_RETRIEVAL_URL);
    }

}
