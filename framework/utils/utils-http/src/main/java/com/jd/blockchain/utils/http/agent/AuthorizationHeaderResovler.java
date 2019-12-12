package com.jd.blockchain.utils.http.agent;

/**
 * AuthorizationHeaderResovler 是一个根据实际的请求生成认证头部的策略接口；
 * 
 * @author haiq
 *
 */
public interface AuthorizationHeaderResovler {
	
	public AuthorizationHeader generateHeader(ServiceRequest request);
	
}
