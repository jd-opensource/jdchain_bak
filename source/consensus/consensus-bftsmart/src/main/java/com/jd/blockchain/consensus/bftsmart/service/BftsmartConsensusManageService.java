package com.jd.blockchain.consensus.bftsmart.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jd.blockchain.consensus.ClientIdentification;
import com.jd.blockchain.consensus.ConsensusManageService;
import com.jd.blockchain.consensus.bftsmart.BftsmartClientIncomingConfig;
import com.jd.blockchain.consensus.bftsmart.BftsmartClientIncomingSettings;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

public class BftsmartConsensusManageService implements ConsensusManageService {

	public static final int CLIENT_RANGE = 100 * 1000;

	private BftsmartNodeServer nodeServer;
	private int clientId;
	private static final Lock authLock = new ReentrantLock();

	public BftsmartConsensusManageService(BftsmartNodeServer nodeServer) {
		this.nodeServer = nodeServer;
		// Assume that each peer node corresponds to up to 100 gateways
		clientId = nodeServer.getServerId() * CLIENT_RANGE;
	}

	@Override
	public BftsmartClientIncomingSettings authClientIncoming(ClientIdentification authId) {
		if (verify(authId)) {
			BftsmartClientIncomingSettings clientIncomingSettings = new BftsmartClientIncomingConfig();

			((BftsmartClientIncomingConfig) clientIncomingSettings)
					.setTopology(BinarySerializeUtils.serialize(nodeServer.getTopology()));
			((BftsmartClientIncomingConfig) clientIncomingSettings)
					.setTomConfig(BinarySerializeUtils.serialize(nodeServer.getTomConfig()));
			((BftsmartClientIncomingConfig) clientIncomingSettings)
					.setConsensusSettings(nodeServer.getConsensusSetting());
			((BftsmartClientIncomingConfig) clientIncomingSettings).setPubKey(authId.getPubKey());
			// compute gateway id
			try {
				authLock.lock();
				((BftsmartClientIncomingConfig) clientIncomingSettings).setClientId(clientId++);
			} finally {
				authLock.unlock();
			}

			return clientIncomingSettings;

		}

		return null;
	}

	public boolean verify(ClientIdentification authId) {

		SignatureFunction signatureFunction = Crypto
				.getSignatureFunction(authId.getPubKey().getAlgorithm());

		return signatureFunction.verify(authId.getSignature(), authId.getPubKey(), authId.getIdentityInfo());
	}
}
