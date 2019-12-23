package com.jd.blockchain.utils.web.model;

import com.jd.blockchain.utils.serialize.json.JSONString;

public class WebResponse {

	private boolean success;

	private Object data;

	private ErrorMessage error;
	
	private WebResponse(){
		
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ErrorMessage getError() {
		return error;
	}

	public void setError(ErrorMessage error) {
		this.error = error;
	}
	
	public static WebResponse createSuccessResult(Object data){
		WebResponse resonseResult = new WebResponse();
		resonseResult.setSuccess(true);
//		if (data != null) {
//			JSONString jsonData = JSONString.toJSONString(data);
//			resonseResult.setData(jsonData);
//		}
		resonseResult.setData(data);
		return resonseResult;
	}
	
	public static WebResponse createFailureResult(int code, String message){
		ErrorMessage errorMessage = new ErrorMessage(code, message);
		return createFailureResult(errorMessage);
	}
	
	public static WebResponse createFailureResult(ErrorMessage errorMessage){
		WebResponse resonseResult = new WebResponse();
		resonseResult.setSuccess(false);
		resonseResult.setError(errorMessage);
		return resonseResult;
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
