package com.jd.blockchain.ledger.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoKey;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.MagicNumber;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesEncoding;
import com.jd.blockchain.utils.io.NumberMask;
import com.jd.blockchain.utils.io.RuntimeIOException;

public class CryptoKeyEncoding {

	/**
	 * @param key
	 * @param out
	 * @return 写入的字节数；
	 */
	public static int writeKey(CryptoKey key, OutputStream out) {
		try {
			byte magicNum;
			if (key instanceof PubKey) {
				magicNum = MagicNumber.PUB_KEY;
			} else if (key instanceof PrivKey) {
				magicNum = MagicNumber.PRIV_KEY;
			} else {
				throw new IllegalArgumentException("Unsupported key type[" + key.getClass().getName() + "]!");
			}

			out.write(magicNum);
			out.write(key.getAlgorithm().code());

			int size = 2;// 已经写入 2 字节；
			size += BytesEncoding.write(key.getRawKeyBytes(), NumberMask.SHORT, out);
			return size;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static CryptoKey readKey(InputStream in) {
		try {
			byte magicNum = (byte) in.read();
			if (magicNum != MagicNumber.PUB_KEY && magicNum != MagicNumber.PRIV_KEY) {
				throw new IllegalArgumentException("The CryptoKey MagicNumber read from the InputStream is Illegal!");
			}
			byte code = (byte) in.read();
			CryptoAlgorithm algorithm = CryptoAlgorithm.valueOf(code);
			ByteArray value = BytesEncoding.readAsByteArray(NumberMask.SHORT, in);

			if (magicNum == MagicNumber.PUB_KEY) {
				return new PubKey(algorithm, value.bytes());
			} else {
				return new PrivKey(algorithm, value.bytes());
			}
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static PubKey readPubKey(InputStream in) {
		try {
			byte magicNum = (byte) in.read();
			if (magicNum != MagicNumber.PUB_KEY) {
				throw new IllegalArgumentException("The PubKey MagicNumber read from the InputStream is Illegal!");
			}
			byte code = (byte) in.read();
			CryptoAlgorithm algorithm = CryptoAlgorithm.valueOf(code);
			ByteArray value = BytesEncoding.readAsByteArray(NumberMask.SHORT, in);
			return new PubKey(algorithm, value.bytes());
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static PrivKey readPrivKey(InputStream in) {
		try {
			byte magicNum = (byte) in.read();
			if (magicNum != MagicNumber.PRIV_KEY) {
				throw new IllegalArgumentException("The PrivKey MagicNumber read from the InputStream is Illegal!");
			}
			byte code = (byte) in.read();
			CryptoAlgorithm algorithm = CryptoAlgorithm.valueOf(code);
			ByteArray value = BytesEncoding.readAsByteArray(NumberMask.SHORT, in);
			return new PrivKey(algorithm, value.bytes());
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	public static ByteArray toBytes(CryptoKey key) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeKey(key, out);
		byte[] keyBytes = out.toByteArray();
		return ByteArray.wrap(keyBytes);
	}

	public static String toBase64(CryptoKey key) {
		return toBytes(key).toBase64();
	}

	public static CryptoKey fromBase64(String hexKeyString) {
		return readKey(ByteArray.parseBase64(hexKeyString).asInputStream());
	}
	
	public static CryptoKey fromBase58(String base58String) {
		return readKey(ByteArray.parseBase58(base58String).asInputStream());
	}

}
