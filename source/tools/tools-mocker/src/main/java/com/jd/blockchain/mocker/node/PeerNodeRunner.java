package com.jd.blockchain.mocker.node;

import com.jd.blockchain.peer.PeerServerBooter;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.concurrent.ThreadInvoker.AsyncCallback;
import com.jd.blockchain.utils.net.NetworkAddress;

public class PeerNodeRunner {

	private NetworkAddress serviceAddress;

	private volatile PeerServerBooter peerServer;

	private LedgerBindingConfig ledgerBindingConfig;

	public DbConnectionFactory getDBConnectionFactory() {
		return peerServer.getDBConnectionFactory();
	}

	public NetworkAddress getServiceAddress() {
		return serviceAddress;
	}

	public LedgerBindingConfig getLedgerBindingConfig() {
		return ledgerBindingConfig;
	}

	public PeerNodeRunner(NetworkAddress serviceAddress, LedgerBindingConfig ledgerBindingConfig) {
		this(serviceAddress, ledgerBindingConfig, null);
	}

	public PeerNodeRunner(NetworkAddress serviceAddress, LedgerBindingConfig ledgerBindingConfig,
						  DbConnectionFactory dbConnectionFactory) {
		this.serviceAddress = serviceAddress;
		this.ledgerBindingConfig = ledgerBindingConfig;
		if (dbConnectionFactory == null) {
			this.peerServer = new PeerServerBooter(ledgerBindingConfig, serviceAddress.getHost(), serviceAddress.getPort(),null);
		}else {
			this.peerServer = new PeerServerBooter(ledgerBindingConfig, serviceAddress.getHost(), serviceAddress.getPort(),null,
					dbConnectionFactory);
		}
	}

	public AsyncCallback<Object> start() {
		ThreadInvoker<Object> invoker = new ThreadInvoker<Object>() {
			@Override
			protected Object invoke() throws Exception {
				peerServer.start();

				return null;
			}
		};

		return invoker.start();
	}

	public void stop() {
		peerServer.close();
	}
}
