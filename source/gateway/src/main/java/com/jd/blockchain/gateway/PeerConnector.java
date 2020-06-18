package com.jd.blockchain.gateway;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.utils.net.NetworkAddress;

import java.util.List;
import java.util.Set;

public interface PeerConnector {

	/**
	 * 获取Peer地址列表
	 *
	 * @return
	 */
	Set<NetworkAddress> getPeerAddresses();

	/**
	 * 是否连接成功
	 *
	 * @return
	 */
	boolean isConnected();

	/**
	 * 连接至指定Peer节点
	 *
	 * @param peerAddress
	 *             Peer地址
	 * @param defaultKeyPair
	 *             连接Peer所需公私钥信息
	 * @param peerProviders
	 *             支持的Provider解析列表
	 */
	void connect(NetworkAddress peerAddress, AsymmetricKeypair defaultKeyPair, List<String> peerProviders);

	/**
	 * 监控重连，判断是否需要更新账本信息，再进行重连操作
	 * Peer地址及其他信息见${@link PeerConnector#connect(com.jd.blockchain.utils.net.NetworkAddress, com.jd.blockchain.crypto.AsymmetricKeypair, java.util.List)}
	 *
	 */
	void monitorAndReconnect();

	/**
	 * 关闭连接
	 *
	 */
	void close();
	
}
