package com.jd.blockchain.ledger.data;

import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.crypto.CryptoKeyPair;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;

public class TxRequestBuilder implements TransactionRequestBuilder {

	private static final String DEFAULT_HASH_ALGORITHM = "SHA256";

	private TransactionContent txContent;

	private List<DigitalSignature> endpointSignatures = new ArrayList<>();

	private List<DigitalSignature> nodeSignatures = new ArrayList<>();

	public TxRequestBuilder(TransactionContent txContent) {
		this.txContent = txContent;
	}

	@Override
	public HashDigest getHash() {
		return txContent.getHash();
	}

	@Override
	public TransactionContent getTransactionContent() {
		return txContent;
	}

	@Override
	public DigitalSignature signAsEndpoint(CryptoKeyPair keyPair) {
		DigitalSignature signature = sign(txContent, keyPair);
		addEndpointSignature(signature);
		return signature;
	}

	@Override
	public DigitalSignature signAsNode(CryptoKeyPair keyPair) {
		DigitalSignature signature = sign(txContent, keyPair);
		addNodeSignature(signature);
		return signature;
	}

	@Override
	public void addNodeSignature(DigitalSignature signature) {
		nodeSignatures.add(signature);
	}

	@Override
	public void addEndpointSignature(DigitalSignature signature) {
		endpointSignatures.add(signature);
	}

	public static DigitalSignature sign(TransactionContent txContent, CryptoKeyPair keyPair) {
		SignatureDigest signatureDigest = sign(txContent, keyPair.getPrivKey());
		DigitalSignature signature = new DigitalSignatureBlob(keyPair.getPubKey(), signatureDigest);
		return signature;
	}

	public static SignatureDigest sign(TransactionContent txContent, PrivKey privKey) {
		return CryptoServiceProviders.getSignatureFunction(privKey.getAlgorithm()).sign(privKey,
				txContent.getHash().toBytes());
	}

	public static boolean verifySignature(TransactionContent txContent, SignatureDigest signDigest, PubKey pubKey) {
		return CryptoServiceProviders.getSignatureFunction(pubKey.getAlgorithm()).verify(signDigest, pubKey,
				txContent.getHash().toBytes());
	}

	@Override
	public TransactionRequest buildRequest() {
		TxRequestMessage txMessage = new TxRequestMessage(txContent);
		txMessage.addEndpointSignatures(endpointSignatures);
		txMessage.addNodeSignatures(nodeSignatures);

		byte[] reqBytes = BinaryEncodingUtils.encode(txMessage, NodeRequest.class);
		HashDigest reqHash = CryptoServiceProviders.getHashFunction(DEFAULT_HASH_ALGORITHM).hash(reqBytes);
		txMessage.setHash(reqHash);

		return txMessage;
	}

}
