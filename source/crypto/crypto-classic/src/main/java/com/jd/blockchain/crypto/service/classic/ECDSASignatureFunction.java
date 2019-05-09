package com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.crypto.utils.classic.ECDSAUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.math.BigInteger;

import static com.jd.blockchain.crypto.BaseCryptoKey.KEY_TYPE_BYTES;
import static com.jd.blockchain.crypto.CryptoBytes.ALGORYTHM_CODE_SIZE;
import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;

public class ECDSASignatureFunction implements SignatureFunction {

	private static final CryptoAlgorithm ECDSA = ClassicAlgorithm.ECDSA;

	private static final int PUBKEY_SIZE = 65;
	private static final int PRIVKEY_SIZE = 32;
	private static final int SIGNATUREDIGEST_SIZE = 64;

	private static final int PUBKEY_LENGTH = ALGORYTHM_CODE_SIZE + KEY_TYPE_BYTES + PUBKEY_SIZE;
	private static final int PRIVKEY_LENGTH = ALGORYTHM_CODE_SIZE + KEY_TYPE_BYTES + PRIVKEY_SIZE;
	private static final int SIGNATUREDIGEST_LENGTH = ALGORYTHM_CODE_SIZE + SIGNATUREDIGEST_SIZE;

	ECDSASignatureFunction() {
	}

	@Override
	public SignatureDigest sign(PrivKey privKey, byte[] data) {

		byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();

		// 验证原始私钥长度为256比特，即32字节
		if (rawPrivKeyBytes.length != PRIVKEY_SIZE) {
			throw new CryptoException("This key has wrong format!");
		}

		// 验证密钥数据的算法标识对应ECDSA签名算法
		if (privKey.getAlgorithm() != ECDSA.code()) {
			throw new CryptoException("This key is not ECDSA private key!");
		}

		// 调用ECDSA签名算法计算签名结果
		return new SignatureDigest(ECDSA, ECDSAUtils.sign(data, rawPrivKeyBytes));
	}

	@Override
	public boolean verify(SignatureDigest digest, PubKey pubKey, byte[] data) {

		byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
		byte[] rawDigestBytes = digest.getRawDigest();

		// 验证原始公钥长度为256比特，即32字节
		if (rawPubKeyBytes.length != PUBKEY_SIZE) {
			throw new CryptoException("This key has wrong format!");
		}

		// 验证密钥数据的算法标识对应ECDSA签名算法
		if (pubKey.getAlgorithm() != ECDSA.code()) {
			throw new CryptoException("This key is not ECDSA public key!");
		}

		// 验证签名数据的算法标识对应ECDSA签名算法，并且原始摘要长度为64字节
		if (digest.getAlgorithm() != ECDSA.code() || rawDigestBytes.length != SIGNATUREDIGEST_SIZE) {
			throw new CryptoException("This is not ECDSA signature digest!");
		}

		// 调用ECDSA验签算法验证签名结果
		return ECDSAUtils.verify(data, rawPubKeyBytes, rawDigestBytes);
	}

	@Override
	public PubKey retrievePubKey(PrivKey privKey) {
		byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
		byte[] rawPubKeyBytes = ECDSAUtils.retrievePublicKey(rawPrivKeyBytes);
		return new PubKey(ECDSA, rawPubKeyBytes);
	}

	@Override
	public boolean supportPrivKey(byte[] privKeyBytes) {
		// 验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，密钥数据的算法标识对应ECDSA签名算法，并且密钥类型是私钥
		return privKeyBytes.length == PRIVKEY_LENGTH && CryptoAlgorithm.match(ECDSA, privKeyBytes)
				&& privKeyBytes[ALGORYTHM_CODE_SIZE] == PRIVATE.CODE;
	}

	@Override
	public PrivKey resolvePrivKey(byte[] privKeyBytes) {
		if (supportPrivKey(privKeyBytes)) {
			return new PrivKey(privKeyBytes);
		} else {
			throw new CryptoException("privKeyBytes are invalid!");
		}
	}

	@Override
	public boolean supportPubKey(byte[] pubKeyBytes) {
		// 验证输入字节数组长度=算法标识长度+密钥类型长度+密钥长度，密钥数据的算法标识对应ECDSA签名算法，并且密钥类型是公钥
		return pubKeyBytes.length == PUBKEY_LENGTH && CryptoAlgorithm.match(ECDSA, pubKeyBytes)
				&& pubKeyBytes[ALGORYTHM_CODE_SIZE] == PUBLIC.CODE;
	}

	@Override
	public PubKey resolvePubKey(byte[] pubKeyBytes) {
		if (supportPubKey(pubKeyBytes)) {
			return new PubKey(pubKeyBytes);
		} else {
			throw new CryptoException("pubKeyBytes are invalid!");
		}
	}

	@Override
	public boolean supportDigest(byte[] digestBytes) {
		// 验证输入字节数组长度=算法标识长度+摘要长度，字节数组的算法标识对应ECDSA算法
		return digestBytes.length == SIGNATUREDIGEST_LENGTH && CryptoAlgorithm.match(ECDSA, digestBytes);
	}

	@Override
	public SignatureDigest resolveDigest(byte[] digestBytes) {
		if (supportDigest(digestBytes)) {
			return new SignatureDigest(digestBytes);
		} else {
			throw new CryptoException("digestBytes are invalid!");
		}
	}

	@Override
	public CryptoAlgorithm getAlgorithm() {
		return ClassicAlgorithm.ECDSA;
	}

	@Override
	public AsymmetricKeypair generateKeypair() {

		// 调用ECDSA算法的密钥生成算法生成公私钥对priKey和pubKey，返回密钥对
		AsymmetricCipherKeyPair keyPair = ECDSAUtils.generateKeyPair();
		ECPrivateKeyParameters privKeyParams = (ECPrivateKeyParameters) keyPair.getPrivate();
		ECPublicKeyParameters pubKeyParams = (ECPublicKeyParameters) keyPair.getPublic();

		byte[] privKeyBytes = BigIntegerTo32Bytes(privKeyParams.getD());
		byte[] pubKeyBytes = pubKeyParams.getQ().getEncoded(false);

		return new AsymmetricKeypair(new PubKey(ECDSA, pubKeyBytes), new PrivKey(ECDSA, privKeyBytes));
	}

	// To convert BigInteger to byte[] whose length is 32
	private static byte[] BigIntegerTo32Bytes(BigInteger b){
		byte[] tmp = b.toByteArray();
		byte[] result = new byte[32];
		if (tmp.length > result.length) {
			System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
		}
		else {
			System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
		}
		return result;
	}
}
