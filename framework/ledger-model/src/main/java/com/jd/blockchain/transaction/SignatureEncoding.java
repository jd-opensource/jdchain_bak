package com.jd.blockchain.transaction;

import org.springframework.util.Base64Utils;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.ledger.DigitalSignature;

public class SignatureEncoding {
	
	public static byte[] encode(DigitalSignature signature) {
		return BinaryProtocol.encode(signature, DigitalSignature.class);
	}
	
	public static DigitalSignature decode(byte[] bytesSignature) {
		return BinaryProtocol.decode(bytesSignature);
	}
	
	public static DigitalSignature decodeFromBase64(String base64Signature) {
		byte[] bytesSignature = Base64Utils.decodeFromUrlSafeString(base64Signature);
		return decode(bytesSignature);
	}

	public static String encodeToBase64(DigitalSignature signature) {
		byte[] bytesSignature = encode(signature);
		return Base64Utils.encodeToUrlSafeString(bytesSignature);
	}
}
