package com.jd.blockchain.contract.model;

/**
 * 给每个错误编码，编译快速定位;
 * @Author zhaogw
 * @Date 2018/11/8 15:32
 */
public enum  ErrorCodeEnum {
    //<100为致命错误;
    GATEWAY_CONNECT_ERROR(1,ErrorType.ERROR,"GatewayServiceFactory connect error.!"),
    CONTRACT_CLASSPATH_NOT_SET(2,ErrorType.ERROR,"in private contract classLoader,no jar in the contract folder!"),
    //其它错误从101开始计数;
    AMOUNT_NEGATIVE(101,ErrorType.ALARM,"The amount is negative!");


    private int code;
    private int type;
    private String message;

    ErrorCodeEnum(int code, int type, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    public int getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public String toString(){
        return "code:"+code+", type:"+type+", message:"+message;
    }
}

/**
 * 给错误分个类，便于汇总;
 */
class ErrorType {
    public static final int ALARM = 0;
    public static final int ERROR = 1;
    public static final int CONTRACT_EXE = 2;
    public static final int CONTRACT_COMPILE = 3;
}


