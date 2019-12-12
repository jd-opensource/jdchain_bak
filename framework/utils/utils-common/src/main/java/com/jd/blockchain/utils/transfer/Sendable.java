package com.jd.blockchain.utils.transfer;

/**
 * Sendable 是发送操作的抽象；
 * 
 * @author haiq
 *
 */
public interface Sendable<TData> {

	/**
	 * 发送消息；
	 * 
	 * @param message
	 */
	public void send(TData message);

}
