package com.jd.blockchain.utils.transfer;

import com.jd.blockchain.utils.concurrent.AsyncFuture;

/**
 * AsyncMessageSendable 是对异步发送操作的抽象；
 * 
 * @author haiq
 *
 * @param <TData>
 */
public interface AsyncSendable<TSender, TData> {

	/**
	 * 异步发送消息；
	 * 
	 * @param message
	 * @return
	 */
	public AsyncFuture<TSender> asyncSend(TData message);

}
