package test.my.utils.http.agent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesUtils;

public class HttpRequestCollector extends HttpServlet{

	private static final long serialVersionUID = -8014615357825392276L;
	
	private List<HttpRequestInfo> reqRecords = new LinkedList<HttpRequestInfo>();
	
	private String requestPath = null;
	
	private String authorization = null;
	
	private String responseText = null;
	
	private byte[] responseBytes = null;
	
	private String requestMethod = null;
	
	private ByteArray requestBody = null;
	
	public HttpRequestCollector(String responseText) {
		this.responseText = responseText;
	}
	
	public HttpRequestCollector(byte[] responseBytes) {
		this.responseBytes = responseBytes;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		reqRecords.add(new HttpRequestInfo(HttpMethod.GET, req.getParameterMap()));
		requestPath = req.getRequestURI();
		authorization = req.getHeader("Authorization");
		requestMethod = req.getMethod();
		
		if (responseText != null) {
			resp.getWriter().print(responseText);
			resp.getWriter().flush();
		}else if(responseBytes != null) {
			resp.getOutputStream().write(responseBytes, 0, responseBytes.length);
			resp.getOutputStream().flush();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		reqRecords.add(new HttpRequestInfo(HttpMethod.POST, req.getParameterMap()));
		requestPath = req.getRequestURI();
		authorization = req.getHeader("Authorization");
		requestMethod = req.getMethod();
		requestBody = readRequestBody(req);
		
		if (responseText != null) {
			resp.getWriter().print(responseText);
			resp.getWriter().flush();
		}else if(responseBytes != null) {
			resp.getOutputStream().write(responseBytes, 0, responseBytes.length);
			resp.getOutputStream().flush();
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		reqRecords.add(new HttpRequestInfo(HttpMethod.PUT, req.getParameterMap()));
		requestPath = req.getRequestURI();
		authorization = req.getHeader("Authorization");
		requestMethod = req.getMethod();
		requestBody = readRequestBody(req);
		
		if (responseText != null) {
			resp.getWriter().print(responseText);
			resp.getWriter().flush();
		}else if(responseBytes != null) {
			resp.getOutputStream().write(responseBytes, 0, responseBytes.length);
			resp.getOutputStream().flush();
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		reqRecords.add(new HttpRequestInfo(HttpMethod.DELETE, req.getParameterMap()));
		requestPath = req.getRequestURI();
		authorization = req.getHeader("Authorization");
		requestMethod = req.getMethod();
		
		if (responseText != null) {
			resp.getWriter().print(responseText);
			resp.getWriter().flush();
		}else if(responseBytes != null) {
			resp.getOutputStream().write(responseBytes, 0, responseBytes.length);
			resp.getOutputStream().flush();
		}
	}
	
	private ByteArray readRequestBody(HttpServletRequest request) throws UnsupportedEncodingException, IOException{
		byte[] bodyBytes = BytesUtils.copyToBytes(request.getInputStream());
		return ByteArray.wrap(bodyBytes);
		
//		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
//		String line = null;
//		StringBuilder builder = new StringBuilder();
//		while((line = reader.readLine()) != null){
//			builder.append(line);
//		}
//		return builder.toString();
	}
	
	
	
	public Iterator<HttpRequestInfo> getRequestRecords(){
		return reqRecords.iterator();
	}
	
	public String getRequestPath(){
		return requestPath;
	}
	
	public String getAuthorization(){
		return authorization;
	}
	
	public String getRequestMethod(){
		return requestMethod;
	}
	
	public ByteArray getRequestBody(){
		return requestBody;
	}
	
	public void clearRequestRecords(){
		reqRecords.clear();
		requestPath = null;
	}
}
