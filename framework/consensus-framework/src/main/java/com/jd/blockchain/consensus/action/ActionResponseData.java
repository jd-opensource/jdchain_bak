package com.jd.blockchain.consensus.action;

public class ActionResponseData implements ActionResponse {
	
	private byte[] message;
	
	private boolean error = false;
	
	private String errorMessage;
	
	private String errorType;

	@Override
	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	@Override
	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
}
