package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.*;
import com.jd.blockchain.utils.io.BytesEncoder;

public class BftsmartSettingsFactory implements SettingsFactory {
	
	private static ConsensusSettingsEncoder CS_ENCODER = new ConsensusSettingsEncoder();
	
	private static ClientIncomingSettingsEncoder CI_ENCODER =new ClientIncomingSettingsEncoder();

	static {
		DataContractRegistry.register(BftsmartConsensusSettings.class);

		DataContractRegistry.register(BftsmartClientIncomingSettings.class);
	}

	@Override
	public BftsmartConsensusSettingsBuilder getConsensusSettingsBuilder() {

		return new BftsmartConsensusSettingsBuilder();
	}

	@Override
	public BytesEncoder<ConsensusSettings> getConsensusSettingsEncoder() {
		return CS_ENCODER;
	}

	@Override
	public BytesEncoder<ClientIncomingSettings> getIncomingSettingsEncoder() {
		return CI_ENCODER;
	}
	
	
	
	private static class ConsensusSettingsEncoder implements BytesEncoder<ConsensusSettings>{

		@Override
		public byte[] encode(ConsensusSettings data) {
			if (data instanceof BftsmartConsensusSettings) {
				return BinaryProtocol.encode(data, BftsmartConsensusSettings.class);
			}
			throw new IllegalArgumentException("Settings data isn't supported! Accept BftsmartConsensusSettings only!");
		}

		@Override
		public ConsensusSettings decode(byte[] bytes) {
			return BinaryProtocol.decodeAs(bytes, BftsmartConsensusSettings.class);
		}
		
	}
	
	private static class ClientIncomingSettingsEncoder implements BytesEncoder<ClientIncomingSettings>{

		@Override
		public byte[] encode(ClientIncomingSettings data) {
			if (data instanceof BftsmartClientIncomingSettings) {
				return BinaryProtocol.encode(data, BftsmartClientIncomingSettings.class);
			}
			throw new IllegalArgumentException("Settings data isn't supported! Accept BftsmartClientIncomingSettings only!");
		}

		@Override
		public ClientIncomingSettings decode(byte[] bytes) {
			return BinaryProtocol.decodeAs(bytes, BftsmartClientIncomingSettings.class);
		}
		
	}


}
