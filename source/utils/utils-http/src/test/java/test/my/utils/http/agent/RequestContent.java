package test.my.utils.http.agent;

public class RequestContent {

	private int requestId;
	
	private String requestContent;
	
	public RequestContent(){
		
	}
	
	public RequestContent(int requestId, String requestContent){
		this.requestId = requestId;
		this.requestContent = requestContent;
	}

	

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public String getRequestContent() {
		return requestContent;
	}

	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}
	
	
}
