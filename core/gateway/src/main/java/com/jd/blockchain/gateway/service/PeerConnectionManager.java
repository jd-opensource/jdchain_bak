package com.jd.blockchain.gateway.service;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.gateway.PeerConnector;
import com.jd.blockchain.gateway.PeerService;
import com.jd.blockchain.sdk.service.PeerBlockchainServiceFactory;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TransactionService;
import com.jd.blockchain.utils.net.NetworkAddress;

import java.util.List;

@Component
public class PeerConnectionManager implements PeerService, PeerConnector {

	private volatile PeerBlockchainServiceFactory peerServiceFactory;

	private volatile NetworkAddress peerAddress;

	private volatile AsymmetricKeypair gateWayKeyPair;

	private volatile List<String> peerProviders;

	@Override
	public NetworkAddress getPeerAddress() {
		return peerAddress;
	}

	@Override
	public boolean isConnected() {
		return peerServiceFactory != null;
	}

	@Override
	public synchronized void connect(NetworkAddress peerAddress, AsymmetricKeypair defaultKeyPair, List<String> peerProviders) {
		if (isConnected()) {
			if (this.peerAddress.equals(peerAddress)) {
				return;
			}
			throw new IllegalArgumentException(
					"This gateway has been connected to a peer, cann't be connected to another peer before closing it!");
		}
		setPeerAddress(peerAddress);
		setGateWayKeyPair(defaultKeyPair);
		setPeerProviders(peerProviders);
		// TODO: 未实现运行时出错时动态重连；
		peerServiceFactory = PeerBlockchainServiceFactory.connect(defaultKeyPair, peerAddress, peerProviders);
	}

	@Override
	public synchronized void reconnect() {
		if (!isConnected()) {
			throw new IllegalArgumentException(
					"This gateway has not connected to a peer, please connect it first!!!");
		}
		peerServiceFactory = PeerBlockchainServiceFactory.connect(gateWayKeyPair, peerAddress, peerProviders);
	}

	@Override
	public void close() {
		PeerBlockchainServiceFactory serviceFactory = this.peerServiceFactory;
		if (serviceFactory != null) {
			this.peerServiceFactory = null;
			this.peerAddress = null;
			serviceFactory.close();
		}
	}

	@Override
	public BlockchainQueryService getQueryService() {
		PeerBlockchainServiceFactory serviceFactory = this.peerServiceFactory;
		if (serviceFactory == null) {
			throw new IllegalStateException("Peer connection was closed!");
		}
		return serviceFactory.getBlockchainService();
	}

	@Override
	public TransactionService getTransactionService() {
		PeerBlockchainServiceFactory serviceFactory = this.peerServiceFactory;
		if (serviceFactory == null) {
			throw new IllegalStateException("Peer connection was closed!");
		}

		return serviceFactory.getTransactionService();
	}

	@PreDestroy
	private void destroy() {
		close();
	}

	public void setPeerAddress(NetworkAddress peerAddress) {
		this.peerAddress = peerAddress;
	}

	public void setGateWayKeyPair(AsymmetricKeypair gateWayKeyPair) {
		this.gateWayKeyPair = gateWayKeyPair;
	}

	public void setPeerProviders(List<String> peerProviders) {
		this.peerProviders = peerProviders;
	}
}
