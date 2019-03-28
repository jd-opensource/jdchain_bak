package com.jd.blockchain.ledger.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jd.blockchain.ledger.HashAlgorithm;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesEncoding;
import com.jd.blockchain.utils.io.NumberMask;
import com.jd.blockchain.utils.security.RipeMD160Utils;
import com.jd.blockchain.utils.security.ShaUtils;

public class HashEncoding {
	public static int write(byte[] hash, OutputStream out) {
		BytesEncoding.write(hash, NumberMask.TINY, out);
		return hash.length;
	}
	public static int write(ByteArray hash, OutputStream out) {
		 BytesEncoding.write(hash, NumberMask.TINY, out);
		 return hash.size();
	}
	
	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static ByteArray read(InputStream in) throws IOException {
		return BytesEncoding.readAsByteArray(NumberMask.TINY, in);
	}
	
	
	
	public static ByteArray computeHash(ByteArray content, HashAlgorithm algorithm) {
		return ByteArray.wrap(hash(content.bytes(), algorithm));
	}

	public static ByteArray computeHash(byte[] contentBytes, HashAlgorithm algorithm) {
		return ByteArray.wrap(hash(contentBytes, algorithm));
	}
	
	public static byte[] hash(byte[] contentBytes, HashAlgorithm algorithm) {
		byte[] hashBytes;
		switch (algorithm) {
		case RIPE160:
			hashBytes = RipeMD160Utils.hash(contentBytes);
			break;
		case SHA256:
			hashBytes = ShaUtils.hash_256(contentBytes);
			break;
		default:
			throw new IllegalArgumentException("Unsupported hash algorithm [" + algorithm + "]!");
		}
		return hashBytes;
	}

}
