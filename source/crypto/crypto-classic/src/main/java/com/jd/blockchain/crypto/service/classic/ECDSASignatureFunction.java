package com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.asymmetric.*;

public class ECDSASignatureFunction implements SignatureFunction {

	ECDSASignatureFunction() {
	}

	@Override
	public SignatureDigest sign(PrivKey privKey, byte[] data) {
		return null;
	}

	@Override
	public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {
		return false;
	}

	@Override
	public PubKey retrievePubKey(PrivKey privKey) {
		return null;
	}

	@Override
	public boolean supportPrivKey(byte[] privKeyBytes) {
		return false;
	}

	@Override
	public PrivKey resolvePrivKey(byte[] privKeyBytes) {
		return null;
	}

	@Override
	public boolean supportPubKey(byte[] pubKeyBytes) {
		return false;
	}

	@Override
	public PubKey resolvePubKey(byte[] pubKeyBytes) {
		return null;
	}

	@Override
	public boolean supportDigest(byte[] digestBytes) {
		return false;
	}

	@Override
	public SignatureDigest resolveDigest(byte[] digestBytes) {
		return null;
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return ClassicCryptoService.ECDSA_ALGORITHM;
	}

	@Override
	public CryptoKeyPair generateKeyPair() {
		return null;
	}
}
