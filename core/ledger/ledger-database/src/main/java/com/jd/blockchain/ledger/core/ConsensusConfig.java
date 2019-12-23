//package com.jd.blockchain.ledger.core;
//
//import com.jd.blockchain.ledger.ConsensusSetting;
//
//public class ConsensusConfig implements ConsensusSetting {
//
//	private byte[] value;
//	
//	public ConsensusConfig() {
//	}
//	
//	public ConsensusConfig(ConsensusSetting setting) {
//		if (setting != null) {
//			this.value = setting.getValue();
//		}
//	}
//
//
//	@Override
//	public byte[] getValue() {
//		return value;
//	}
//
//
//	public void setValue(byte[] value) { this.value = value;}
//
//
//}