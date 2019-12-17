package com.jd.blockchain.ledger.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.utils.Bytes;

/**
 * 交易请求的扩展信息；
 * 
 * @author huanghaiquan
 *
 */
public class TransactionRequestExtensionImpl implements TransactionRequestExtension {

	private TransactionRequest request;

	private Map<Bytes, Credential> endpointSignatures = new HashMap<>();

	private Map<Bytes, Credential> nodeSignatures = new HashMap<>();

	public TransactionRequestExtensionImpl(TransactionRequest request) {
		this.request = request;
		resolveSigners();
	}

	private void resolveSigners() {
		if (request.getEndpointSignatures() != null) {
			for (DigitalSignature signature : request.getEndpointSignatures()) {
				Credential cred = new Credential(signature);
				endpointSignatures.put(cred.getIdentity().getAddress(), cred);
			}
		}
		if (request.getEndpointSignatures() != null) {
			for (DigitalSignature signature : request.getNodeSignatures()) {
				Credential cred = new Credential(signature);
				nodeSignatures.put(cred.getIdentity().getAddress(), cred);
			}
		}
	}

	@Override
	public Set<Bytes> getEndpointAddresses() {
		return endpointSignatures.keySet();
	}

	@Override
	public Set<Bytes> getNodeAddresses() {
		return nodeSignatures.keySet();
	}

	@Override
	public Collection<Credential> getEndpoints() {
		return endpointSignatures.values();
	}

	@Override
	public Collection<Credential> getNodes() {
		return nodeSignatures.values();
	}

	@Override
	public boolean containsEndpoint(Bytes address) {
		return endpointSignatures.containsKey(address);
	}

	@Override
	public boolean containsNode(Bytes address) {
		return nodeSignatures.containsKey(address);
	}

	@Override
	public DigitalSignature getEndpointSignature(Bytes address) {
		return endpointSignatures.get(address).getSignature();
	}

	@Override
	public DigitalSignature getNodeSignature(Bytes address) {
		return nodeSignatures.get(address).getSignature();
	}

	@Override
	public HashDigest getHash() {
		return request.getHash();
	}

	@Override
	public DigitalSignature[] getNodeSignatures() {
		return request.getNodeSignatures();
	}

	@Override
	public DigitalSignature[] getEndpointSignatures() {
		return request.getEndpointSignatures();
	}

	@Override
	public TransactionContent getTransactionContent() {
		return request.getTransactionContent();
	}

}
