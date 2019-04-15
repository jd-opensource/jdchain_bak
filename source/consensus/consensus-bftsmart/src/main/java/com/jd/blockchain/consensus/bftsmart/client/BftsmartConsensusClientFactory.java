package com.jd.blockchain.consensus.bftsmart.client;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jd.blockchain.consensus.ClientIncomingSettings;
import com.jd.blockchain.consensus.ConsensusManageService;
import com.jd.blockchain.consensus.bftsmart.BftsmartClientIncomingSettings;
import com.jd.blockchain.consensus.client.ClientFactory;
import com.jd.blockchain.consensus.client.ClientSettings;
import com.jd.blockchain.consensus.client.ConsensusClient;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.utils.net.NetworkAddress;

public class BftsmartConsensusClientFactory implements ClientFactory {


	private AtomicInteger addId = new AtomicInteger();

	private String localDomain = "localhost";
	private String localIp = "127.0.0.1";

	public BftsmartConsensusClientFactory() {

	}


    @Override
	public BftsmartClientIdentification buildAuthId(AsymmetricKeypair clientKeyPair) {

		PubKey pubKey = clientKeyPair.getPubKey();
		PrivKey privKey = clientKeyPair.getPrivKey();

		SignatureFunction signatureFunction =Crypto.getSignatureFunction(pubKey.getAlgorithm());
		SignatureDigest signatureDigest = signatureFunction.sign(privKey, pubKey.toBytes());

		BftsmartClientIdentification bftsmartClientIdentification = new BftsmartClientIdentification();
		bftsmartClientIdentification.setIdentityInfo(pubKey.toBytes());
		bftsmartClientIdentification.setPubKey(pubKey);
		bftsmartClientIdentification.setSignatureDigest(signatureDigest);

		return bftsmartClientIdentification;
	}

	@Override
	public ClientSettings buildClientSettings(ClientIncomingSettings incomingSettings) {

		BftsmartClientIncomingSettings clientIncomingSettings = (BftsmartClientIncomingSettings) incomingSettings;

		BftsmartClientSettings clientSettings = new BftsmartClientConfig(clientIncomingSettings);

        return clientSettings;
	}

	@Override
	public ConsensusClient setupClient(ClientSettings settings) {

		return new BftsmartConsensusClient(settings);
	}

	@Override
	public ConsensusManageService createManageServiceClient(String[] serviceNodes) {
//		BftsmartConsensusManageService consensusManageService = null;
//		BftsmartClientIncomingSettings clientIncomingSettings;
//
//
//		try {
//			if (serviceNodes == null) {
//				throw new ConsensusSecurityException("createManageServiceClient param error!");
//			}
//
//			for (int i = 0; i < serviceNodes.length; i++) {
//
//                NetworkAddress networkAddress = getIpPortFromUrl(serviceNodes[i]);
//                if (networkAddress == null) {
//                	continue;
//				}
//				ServiceEndpoint peerServer = new ServiceEndpoint(networkAddress.getHost(), networkAddress.getPort(), false);
//				consensusManageService = HttpServiceAgent.createService(BftsmartConsensusManageService.class, peerServer);
//				clientIncomingSettings = consensusManageService.authClientIncoming(clientIdentification);
//
//				if (clientIncomingSettings == null) {
//					consensusManageService = null;
//				} else {
//					//认证成功
//					break;
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		return consensusManageService;
		return null;
	}

	private NetworkAddress getIpPortFromUrl(String url) {

		// 1.check null
		if (url == null || url.trim().equals("")) {
			return null;
		}

		// 2. localhost replace to 127.0.0.1
		if(url.startsWith("http://" + localDomain) ){
			url = url.replace("http://" + localDomain, "http://" + localIp) ;
		}

		String host = "";
		Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+(:\\d{0,5})?");
		Matcher matcher = p.matcher(url);
		if (matcher.find()) {
			host = matcher.group() ;
		}

		if(host.contains(":") == false){
			//default port :80
			return new NetworkAddress(host, 80);
		}
		else {
			String[] ipPortArr = host.split(":");
			return new NetworkAddress(ipPortArr[0], Integer.parseInt(ipPortArr[1]));
		}
	}

}
