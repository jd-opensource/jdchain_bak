package com.jd.blockchain.ledger.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.utils.Bytes;

/**
 * @Author zhaogw
 * @Date 2018/9/5 14:52
 */
public class TransactionRequestContextImpl implements TransactionRequestContext {

    private TransactionRequest request;

    private Map<Bytes, DigitalSignature> endpointSignatures = new HashMap<>();

    private Map<Bytes, DigitalSignature> nodeSignatures = new HashMap<>();

    public TransactionRequestContextImpl(TransactionRequest request) {
        this.request = request;
        resolveSigners();
    }

    private void resolveSigners() {
        if (request.getEndpointSignatures() != null) {
            for (DigitalSignature signature : request.getEndpointSignatures()) {
            	Bytes address = AddressEncoding.generateAddress(signature.getPubKey());
                endpointSignatures.put(address, signature);
            }
        }
        if (request.getEndpointSignatures() != null) {
            for (DigitalSignature signature : request.getNodeSignatures()) {
            	Bytes address = AddressEncoding.generateAddress(signature.getPubKey());
                nodeSignatures.put(address, signature);
            }
        }
    }

    @Override
    public TransactionRequest getRequest() {
        return request;
    }

    @Override
    public Set<Bytes> getEndpoints() {
        return endpointSignatures.keySet();
    }

    @Override
    public Set<Bytes> getNodes() {
        return nodeSignatures.keySet();
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
        return endpointSignatures.get(address);
    }

    @Override
    public DigitalSignature getNodeSignature(Bytes address) {
        return nodeSignatures.get(address);
    }

}
