package com.jd.blockchain.utils.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 摘要工具类
 * 
 * @author haiq
 *
 */
public class ShaUtils {

	/**
	 * 对指定的字节数组进行 SHA128 哈希；
	 * @param bytes bytes
	 * @return 返回长度为 16 的字节数组；
	 */
	public static byte[] hash_128(byte[] bytes) {
		byte[] hash256Bytes = hash_256(bytes);
		return Arrays.copyOf(hash256Bytes, 16);
	}

	/**
	 * 对指定的字节数组进行 SHA256 哈希；
	 * @param bytes bytes
	 * @return 返回长度为 32 的字节数组；
	 */
	public static byte[] hash_256(byte[] bytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(bytes);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 对指定的字节数组进行 SHA256 哈希；
	 * @param bytes bytes
	 * @param outputBuffer outputBuffer
	 * @return 返回长度为 32 的字节数组；
	 */
	public static int hash_256(byte[] bytes, byte[] outputBuffer) {
		return hash_256(bytes, outputBuffer, 0, outputBuffer.length);
	}

	/**
	 * 对指定的字节数组进行 SHA256 哈希；
	 * @param bytes bytes
	 * @param outputBuffer outputBuffer
	 * @param offset offset
	 * @param length length
	 * @return 返回长度为 32 的字节数组；
	 */
	public static int hash_256(byte[] bytes, byte[] outputBuffer, int offset, int length) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(bytes);
			return md.digest(outputBuffer, offset, length);
		} catch (NoSuchAlgorithmException | DigestException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static byte[] hash_256(InputStream input) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			byte[] buff = new byte[64];
			int len = 0;
			while ((len = input.read(buff)) > 0) {
				md.update(buff, 0, len);
			}
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static SHA256Hash hash_256() {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return new SHA256HashImpl(md);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	// /**
	// * 组装待签名数据
	// *
	// * @param userAgent
	// * 客户端
	// * @param url
	// * 请求路径
	// * @param requestBody
	// * 请求体 可为空
	// * @return
	// */
	// @Deprecated
	// public static byte[] getData(byte[] userAgent, byte[] url, byte[]
	// requestBody) {
	//
	// byte[] bytes = new byte[userAgent.length + url.length + (requestBody == null
	// ? 0 : requestBody.length)];
	// System.arraycopy(userAgent, 0, bytes, 0, userAgent.length);
	// System.arraycopy(url, 0, bytes, userAgent.length, url.length);
	// if (requestBody != null) {
	// System.arraycopy(requestBody, 0, bytes, userAgent.length + url.length,
	// requestBody.length);
	// }
	// return bytes;
	// }

	private static class SHA256HashImpl implements SHA256Hash {

		private MessageDigest md;

		public SHA256HashImpl(MessageDigest md) {
			this.md = md;
		}

		@Override
		public void update(byte[] bytes) {
			md.update(bytes, 0, bytes.length);
		}

		@Override
		public void update(byte[] bytes, int offset, int len) {
			md.update(bytes, offset, len);
		}

		@Override
		public byte[] complete() {
			return md.digest();
		}

	}

}
