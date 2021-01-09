package com.jdchain.samples.sdk.testnet;

import com.jd.blockchain.gateway.GatewayConfigProperties;
import com.jd.blockchain.gateway.GatewayConfigProperties.KeyPairConfig;

import utils.concurrent.ThreadInvoker;
import utils.concurrent.ThreadInvoker.AsyncCallback;
import utils.net.NetworkAddress;

import com.jd.blockchain.gateway.GatewayServerBooter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.util.Map;

public class GatewayRunner {

    private NetworkAddress serviceAddress;

    private GatewayServerBooter gatewayServer;

    public GatewayRunner(String host, int port, KeyPairConfig gatewayDefaultKey, NetworkAddress... masterPeerAddresses) {
        this(host, port, gatewayDefaultKey, null, null, masterPeerAddresses);
    }

    public GatewayRunner(String host, int port, KeyPairConfig gatewayDefaultKey, String[] providers,
                         Map<String, Object> otherMap, NetworkAddress... masterPeerAddresses) {
        this.serviceAddress = new NetworkAddress(host, port);
        GatewayConfigProperties config = new GatewayConfigProperties();

        config.http().setHost(host);
        config.http().setPort(port);

        if (providers != null) {
            for (String provider : providers) {
                config.providerConfig().add(provider);
            }
        }

        for (NetworkAddress address : masterPeerAddresses) {
            config.setMasterPeerAddress(address);
        }

        config.keys().getDefault().setPubKeyValue(gatewayDefaultKey.getPubKeyValue());
        config.keys().getDefault().setPrivKeyValue(gatewayDefaultKey.getPrivKeyValue());
        config.keys().getDefault().setPrivKeyPassword(gatewayDefaultKey.getPrivKeyPassword());

        if (!CollectionUtils.isEmpty(otherMap)) {
            config.setDataRetrievalUrl(otherMap.get("DATA_RETRIEVAL_URL").toString());
        }


        //get the springConfigLocation;
        ClassPathResource configResource = new ClassPathResource("application-gw.properties");
        String springConfigLocation = "classPath:" + configResource.getPath();

        this.gatewayServer = new GatewayServerBooter(config, springConfigLocation);
    }

    public AsyncCallback<Object> start() {
        ThreadInvoker<Object> invoker = new ThreadInvoker<Object>() {
            @Override
            protected Object invoke() throws Exception {
                gatewayServer.start();
                return null;
            }
        };

        return invoker.start();
    }

    public void stop() {
        gatewayServer.close();
    }

    public NetworkAddress getServiceAddress() {
        return serviceAddress;
    }
}
