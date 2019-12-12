package test.my.utils.http.agent;

public class DataResponse {

	private boolean success;

	private String error;

	private String content;
	
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public DataResponse() {
	}

	public DataResponse(boolean success, String error, String content) {
		this.success = success;
		this.error = error;
		this.content = content;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getError() {
		return error;
	}

	public String getContent() {
		return content;
	}

}
