package com.jd.blockchain.utils.http;

import java.io.InputStream;

import com.jd.blockchain.utils.http.agent.ServiceRequest;

/**
 * 回复结果转换器；
 * 
 * 用于定义如何从 http 回复的文本结果转换为一个特定的对象；
 * 
 * 当 ResponseConvert 抛出的异常的类型存在于服务接口的操作方法声明 的异常列表中，则异常将被直接返回给调用者；
 * 
 * @author haiq
 *
 */
public interface ResponseConverter {
	
	// TODO 支持按 HTTP 状态进行解析；
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception;

}
