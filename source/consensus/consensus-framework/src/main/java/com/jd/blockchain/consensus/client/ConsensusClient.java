package com.jd.blockchain.consensus.client;

import java.io.Closeable;

import com.jd.blockchain.consensus.MessageService;

public interface ConsensusClient extends Closeable {

	/**
	 * 消息服务；
	 * 
	 * @return
	 */
	MessageService getMessageService();

	/**
	 * 共识客户端的配置信息；
	 * 
	 * @return
	 */
	ClientSettings getSettings();

	/**
	 * 是否已连接；
	 * 
	 * @return
	 */
	boolean isConnected();

	/**
	 * 接入共识网络；
	 */
	void connect();

	/**
	 * 断开与共识网络的连接；
	 */
	@Override
	void close();

}
