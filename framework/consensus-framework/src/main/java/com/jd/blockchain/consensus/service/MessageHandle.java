package com.jd.blockchain.consensus.service;

import com.jd.blockchain.utils.concurrent.AsyncFuture;

/**
 * 消息处理器；
 * 
 * @author huanghaiquan
 *
 */
public interface MessageHandle {

	/**
	 * 开始一个新批次来处理有序的消息；
	 * 
	 * @return 返回新批次的 ID ；
	 */
	String beginBatch(String realmName);

	/**
	 * 处理有序的消息；
	 * 
	 * @param messageId
	 *            消息ID；
	 * @param message
	 *            消息内容；
	 * @param batchId
	 *            批次ID；
	 */
	AsyncFuture<byte[]> processOrdered(int messageId, byte[] message, String realmName, String batchId);

	/**
	 * 完成处理批次，返回要进行一致性校验的状态快照；
	 * 
	 * @param batchId
	 * @return 
	 */
	StateSnapshot completeBatch(String realmName, String batchId);

	/**
	 * 提交处理批次；
	 * 
	 * @param batchId
	 */
	void commitBatch(String realmName, String batchId);
	
	/**
	 * 回滚处理批次；
	 * 
	 * @param batchId
	 */
	void rollbackBatch(String realmName, String batchId, int reasonCode);

	/**
	 * 处理无序消息；
	 * 
	 * @param message
	 * @return
	 */
	AsyncFuture<byte[]> processUnordered(byte[] message);

	/**
	 * 获得当前最新区块的状态快照
	 *
	 * @param realmName
	 * @return 最新区块的状态快照
	 */
	StateSnapshot getStateSnapshot(String realmName);

	/**
	 * 获得创世区块的状态快照
	 * @param realmName
	 * @return 创世区块的状态快照
	 */
	StateSnapshot getGenisStateSnapshot(String realmName);

}
