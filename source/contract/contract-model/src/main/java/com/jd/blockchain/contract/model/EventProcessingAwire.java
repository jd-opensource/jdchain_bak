package com.jd.blockchain.contract.model;

/**
 * @author huanghaiquan
 *
 */
public interface EventProcessingAwire extends ContractRuntimeAwire {

	/**
	 * Called before the event handling method is executed;
	 * 
	 * @param eventContext
	 */
	void beforeEvent(ContractEventContext eventContext);

	/**
	 * Called after the event handling method is successfully executed;
	 * 
	 * @param eventContext
	 *            evenet；
	 * @param error
	 *            Error; if the event processing ends normally, this parameter is null;
	 *            if an event processing error occurs, this parameter is not empty;
	 */
	void postEvent(ContractEventContext eventContext, ContractException error);


	/**
	 * Called after the event handling method is successfully executed;
	 *
	 * @param error
	 *            Error; if the event processing ends normally, this parameter is null;
	 *            if an event processing error occurs, this parameter is not empty;
	 */
	void postEvent(ContractException error);

	/**
	 * Called after the event handling method is successfully executed;
	 */
	void postEvent();
}
