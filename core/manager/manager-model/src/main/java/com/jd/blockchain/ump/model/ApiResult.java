package com.jd.blockchain.ump.model;

/**
 * @author zhaogw
 * date 2019/7/10 15:31
 */
public class ApiResult {
    /**
     * 错误码，对应{@link ErrorCode}，表示一种错误类型
     * 如果是成功，则code为1
     */
    private int code;
    /**
     * 具体解释
     */
    private String message;
    /**
     * 返回的结果包装在value中，value可以是单个对象
     */
    private Object value;

    public ApiResult (){  }

    public ApiResult (Object obj){
        this.value = obj;
    }

    public ApiResult (int code, String message){
        this.code = code;
        this.message = message;
    }

    public ApiResult(ErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public ApiResult(ErrorCode errorCode, Object obj){
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
        this.value = obj;
    }

    public ApiResult (int code, String message, Object value){
        this.code = code;
        this.message = message;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
