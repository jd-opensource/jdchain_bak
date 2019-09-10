package com.jd.blockchain.ump.model.web;

public class WebResponse<T> {

    private boolean success;

    private T data;

    private ErrorMessage error;

    private WebResponse(){

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorMessage getError() {
        return error;
    }

    public void setError(ErrorMessage error) {
        this.error = error;
    }

    public static WebResponse createSuccessResult(Object data){
        WebResponse responseResult = new WebResponse();
        responseResult.setSuccess(true);
        responseResult.setData(data);
        return responseResult;
    }

    public static WebResponse createFailureResult(int code, String message){
        ErrorMessage errorMessage = new ErrorMessage(code, message);
        return createFailureResult(errorMessage);
    }

    public static WebResponse createFailureResult(ErrorMessage errorMessage){
        WebResponse responseResult = new WebResponse();
        responseResult.setSuccess(false);
        responseResult.setError(errorMessage);
        return responseResult;
    }



    /**
     * 错误消息实体
     *
     * @author liuxrb
     *
     */
    public static class ErrorMessage {

        private int errorCode;

        private String errorMessage;

        public ErrorMessage() {

        }

        public ErrorMessage(int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
