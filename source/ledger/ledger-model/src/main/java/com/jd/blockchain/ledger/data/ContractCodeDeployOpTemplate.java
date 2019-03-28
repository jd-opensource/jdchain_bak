package com.jd.blockchain.ledger.data;

import com.jd.blockchain.binaryproto.DConstructor;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.binaryproto.FieldSetter;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.DigitalSignature;

public class ContractCodeDeployOpTemplate implements ContractCodeDeployOperation {
	static {
		DataContractRegistry.register(ContractCodeDeployOperation.class);
	}
	
	private BlockchainIdentity contractID;
	
	private byte[] chainCode;

	public ContractCodeDeployOpTemplate() {
	}

	@DConstructor(name="ContractCodeDeployOpTemplate")
	public ContractCodeDeployOpTemplate(@FieldSetter(name="getContractID", type="BlockchainIdentity") BlockchainIdentity contractID, @FieldSetter(name="getChainCode", type="byte[]") byte[] chainCode) {
		this.contractID = contractID;
		this.chainCode= chainCode;
	}

	@Override
	public BlockchainIdentity getContractID() {
		return contractID;
	}

	@Override
	public byte[] getChainCode() {
		return chainCode;
	}

	@Override
	public DigitalSignature getAddressSignature() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
