package com.jdchain.samples.sdk.testnet;

import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.peer.PeerServerBooter;
import com.jd.blockchain.storage.service.DbConnectionFactory;
import com.jd.blockchain.tools.initializer.LedgerBindingConfig;

import utils.concurrent.ThreadInvoker;
import utils.net.NetworkAddress;

public class PeerServer {
    private NetworkAddress serviceAddress;

    private volatile PeerServerBooter booter;

    private LedgerBindingConfig ledgerBindingConfig;

    public DbConnectionFactory getDBConnectionFactory() {
        return booter.getDBConnectionFactory();
    }

    public NetworkAddress getServiceAddress() {
        return serviceAddress;
    }

    public LedgerBindingConfig getLedgerBindingConfig() {
        return ledgerBindingConfig;
    }

    public PeerServer(NetworkAddress serviceAddress, LedgerBindingConfig ledgerBindingConfig) {
        this(serviceAddress, ledgerBindingConfig, null, null);
    }

    public PeerServer(NetworkAddress serviceAddress, LedgerBindingConfig ledgerBindingConfig, DbConnectionFactory dbConnectionFactory) {
        this(serviceAddress, ledgerBindingConfig, dbConnectionFactory, null);
    }

    public PeerServer(NetworkAddress serviceAddress, LedgerBindingConfig ledgerBindingConfig,
                      DbConnectionFactory dbConnectionFactory, LedgerManager ledgerManager) {
        this.serviceAddress = serviceAddress;
        this.ledgerBindingConfig = ledgerBindingConfig;
        if (dbConnectionFactory == null) {
            this.booter = new PeerServerBooter(ledgerBindingConfig, serviceAddress.getHost(),
                    serviceAddress.getPort());
        } else {
            this.booter = new PeerServerBooter(ledgerBindingConfig, serviceAddress.getHost(),
                    serviceAddress.getPort(), dbConnectionFactory, ledgerManager);
        }
    }

    public ThreadInvoker.AsyncCallback<Object> start() {
        ThreadInvoker<Object> invoker = new ThreadInvoker<Object>() {
            @Override
            protected Object invoke() throws Exception {
                booter.start();

                return null;
            }
        };

        return invoker.start();
    }

    public void stop() {
        booter.close();
    }
}
