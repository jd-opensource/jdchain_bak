package com.jd.blockchain.consensus.bftsmart;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.net.NetworkAddress;

public class BftsmartNodeConfig implements BftsmartNodeSettings {

	private int id;

	private String address;

	private PubKey pubKey;

	private NetworkAddress networkAddress;
	
	public BftsmartNodeConfig() {
	}

	static {
		DataContractRegistry.register(BftsmartNodeSettings.class);
	}

	public BftsmartNodeConfig(PubKey pubKey, int id, NetworkAddress networkAddress) {
		this.address = AddressEncoding.generateAddress(pubKey).toBase58();
		this.pubKey = pubKey;
		this.id = id;
		this.networkAddress = networkAddress;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public NetworkAddress getNetworkAddress() {
		return networkAddress;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setNetworkAddress(NetworkAddress networkAddress) {
		this.networkAddress = networkAddress;
	}

	public PubKey getPubKey() {
		return pubKey;
	}

	public void setPubKey(PubKey pubKey) {
		this.pubKey = pubKey;
	}
}
