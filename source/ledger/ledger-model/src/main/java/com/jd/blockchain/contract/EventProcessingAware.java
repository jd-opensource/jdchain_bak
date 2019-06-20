package com.jd.blockchain.contract;

/**
 * @author huanghaiquan
 *
 */
public interface EventProcessingAware extends ContractAware {

	/**
	 * 在事件处理方法执行之前调用；
	 * 
	 * @param eventContext
	 */
	void beforeEvent(ContractEventContext eventContext);

	/**
	 * 在事件处理方法成功执行之后调用；
	 * 
	 * @param eventContext
	 *            事件上下文；
	 * @param error
	 *            错误；如果事件处理正常结束，则此参数为 null；如果事件处理发生了错误，此参数非空；
	 */
	void postEvent(ContractEventContext eventContext, Exception error);

}
