package com.jd.blockchain.ump.model;

/**
 * @author zhaogw
 * date 2019/7/10 15:32
 */
public enum  ErrorCode {
    SUCCESS(1,"成功"),
    NO_PERMISSION(2,"权限不足"),
    SERVER_ERROR(3,"服务器异常"),
    AUTH_ERROR(4,"认证失败"),
    PARAMS_ERROR(5,"参数错误"),
    JSON_PARSE_ERROR(6,"Json解析错误"),
    ILLEAGAL_STRING(7,"非法字符串"),
    GEN_KEY_INPUT_LACK(8,"缺少必要的输入参数：name/randomSeed/basePath/password"),
    UNKNOW_ERROR(10000,"未知错误");

    private int code;
    private String msg;

    ErrorCode(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
