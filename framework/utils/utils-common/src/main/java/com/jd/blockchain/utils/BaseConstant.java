package com.jd.blockchain.utils;

/**
 *
 * @author zhaogw
 * date 2018/5/4 14:20
 */
public class BaseConstant {
    public static final String DELIMETER_COMMA = ",";   //逗号分隔符;
    public static final String DELIMETER_SEMICOLON = ";";   //分号分隔符;
    public static final String DELIMETER_SLASH = "/";   //斜线分隔符;
    public static final String DELIMETER_DOT = ".";   //点号分隔符;
    public static final String DELIMETER_UNDERLINE = "_";   //斜线分隔符;
    public static final String DELIMITER_EQUAL = "=";
    public static final String DELIMETER_QUESTION = "?";   //逗号分隔符;
    public static final String DELIMETER_DOUBLE_ALARM = "##";   //双警号分隔符;
    public static final String CHARSET_UTF_8 = "utf-8"; //utf-8编码;
    //合约系统使用的配置信息;
    public static final String SYS_CONTRACT_CONF = "SYS_CONTRACT_CONF";
    public static final String SYS_CONTRACT_PROPS_NAME = "sys-contract.properties";
    public static final String CONTRACT_MAIN_CLASS_KEY = "contract";

//    public static final String CONTRACT_EVENT_PREFIX="@com.jd.blockchain.contract.ContractEvent(name=";

    // 编译时引用包黑名单
    public static final String PACKAGE_BLACKLIST = "BLACKLIST";

    //根据列表读取时，每次允许的最大获取数量;
    public static final int QUERY_LIST_MAX=100;

    public static final String CONTRACT_SERVICE_PROVIDER = "com.jd.blockchain.contract.jvm.JVMContractServiceProvider";
    public static final String SPRING_CF_LOCATION = "-sp";
}
