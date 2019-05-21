package com.jd.blockchain.contract;

import com.jd.blockchain.utils.BaseConstant;
import com.jd.blockchain.utils.ConsoleUtils;
import com.jd.blockchain.utils.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.*;

import static com.jd.blockchain.utils.BaseConstant.SYS_CONTRACT_CONF;
import static com.jd.blockchain.utils.BaseConstant.SYS_CONTRACT_PROPS_NAME;

/**
 *
 * @author zhaogw
 * date 2019/3/15 18:22
 */
public enum ContractConfigure {
    instance();
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractConfigure.class);
    static Properties pp;

    ContractConfigure(){
        init();
    }

    private void init(){
        String contractConfPath = System.getProperty(SYS_CONTRACT_CONF);
        System.out.println("contractConfPath="+contractConfPath);
        try {
            if (contractConfPath == null) {
                ConsoleUtils.info("Load build-in default contractConf in ContractConfigure ...");
                ClassPathResource contractConfigResource = new ClassPathResource(SYS_CONTRACT_PROPS_NAME);
                InputStream in = contractConfigResource.getInputStream();
                pp = FileUtils.readProperties(in, BaseConstant.CHARSET_UTF_8);
            } else {
                ConsoleUtils.info("Load configuration in ContractConfigure,contractConfPath="+contractConfPath);
                File file = new File(contractConfPath);
                pp = FileUtils.readProperties(file, BaseConstant.CHARSET_UTF_8);
            }
        } catch (Exception e) {
            LOGGER.info(SYS_CONTRACT_PROPS_NAME+"文件异常!"+e.getMessage());
        }
    }

    public String values(String key) {
        if(pp == null){
            init();
        }
        return pp.getProperty(key);
    }

    public String allValues() {
        if(pp == null){
            init();
        }
        Set<String> allKeys = pp.stringPropertyNames();
        List<String> propList = new ArrayList();
        for(String _key : allKeys){
            String value = pp.getProperty(_key);
            propList.add(_key+": "+value);
            LOGGER.info("key={}, value={}",_key,value);
        }
        return propList.toString();
    }

    //写入资源文件信息
    public static void writeProperties(String fileAllName, String comments, Map<String,String> map){
        Properties properties=new Properties();
        try {
            File file = new File(fileAllName);
            if (!file.getParentFile().exists()) {
                boolean result = file.getParentFile().mkdirs();
                if (!result) {
                    System.out.println("文件创建失败.");
                }
            }
            OutputStream outputStream=new FileOutputStream(file);
            for(Map.Entry<String,String> entry : map.entrySet()){
                properties.setProperty(entry.getKey(), entry.getValue());
            }
            properties.store(outputStream, comments);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
