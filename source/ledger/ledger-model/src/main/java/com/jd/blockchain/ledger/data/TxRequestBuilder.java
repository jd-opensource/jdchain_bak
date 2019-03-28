package com.jd.blockchain.ledger.data;

import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.PrivKey;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;

public class TxRequestBuilder implements TransactionRequestBuilder {

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
		return CryptoUtils.sign(privKey.getAlgorithm()).sign(privKey, txContent.getHash().toBytes());
	}

	public static boolean verifySignature(TransactionContent txContent, SignatureDigest signDigest, PubKey pubKey) {
		return CryptoUtils.sign(signDigest.getAlgorithm()).verify(signDigest, pubKey, txContent.getHash().toBytes());
	}

	@Override
	public TransactionRequest buildRequest() {
		TxRequestMessage txMessage = new TxRequestMessage(txContent);
		txMessage.addEndpointSignatures(endpointSignatures);
		txMessage.addNodeSignatures(nodeSignatures);

		byte[] reqBytes = BinaryEncodingUtils.encode(txMessage, NodeRequest.class);
		HashDigest reqHash = CryptoUtils.hash(CryptoAlgorithm.SHA256).hash(reqBytes);
		txMessage.setHash(reqHash);

		return txMessage;
	}

}
