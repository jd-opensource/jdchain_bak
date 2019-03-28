package com.jd.blockchain.utils.http;

/**
 * HttpServiceContext 定义了 HTTP 服务代理上下文的信息；
 * 
 * @author haiq
 *
 */
public interface HttpServiceContext {

	/**
	 * 服务接口的类型；
	 * @return
	 */
	Class<?> getServiceClasss();

	/**
	 * 创建服务代理实例时由调用者指定的绑定对象；
	 * @return
	 */
	Object getProxyBindingData();

}
