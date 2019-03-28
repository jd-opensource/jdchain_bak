package com.jd.blockchain.contract.model;

/**
 * ErrorCodeEnum
 * @author zhaogw
 * date 2018/11/8 15:32
 */
public enum  ErrorCodeEnum {
    //<100为fatal error;
    GATEWAY_CONNECT_ERROR(1,ErrorType.ERROR,"GatewayServiceFactory connect error.!"),
    CONTRACT_CLASSPATH_NOT_SET(2,ErrorType.ERROR,"in private contract classLoader,no jar in the contract folder!"),
    //Other errors are counted from 101.
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
 * Classify the errors so that they can be summarized easily.
 */
class ErrorType {
    public static final int ALARM = 0;
    public static final int ERROR = 1;
    public static final int CONTRACT_EXE = 2;
    public static final int CONTRACT_COMPILE = 3;
}


