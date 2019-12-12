package com.jd.blockchain.utils.codec;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HexUtils {

	public static byte[] decode(String hexString) {
		try {
			return Hex.decodeHex(hexString.toCharArray());
		} catch (DecoderException e) {
			throw new DataDecodeException(e.getMessage(), e);
		}
	}

	public static String encode(byte[] bytes) {
		return Hex.encodeHexString(bytes);
	}

	public static String encode(ByteBuffer bytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[64];
		int len;
		while (bytes.remaining() > 0) {
			len = Math.min(buf.length, bytes.remaining());
			bytes.get(buf, 0, len);
			out.write(buf, 0, len);
		}
		return Hex.encodeHexString(out.toByteArray());
	}

	/**
	 * 判断是否16进制字符串
	 * 
	 * @param hexString hexString
	 * @return boolean
	 */
	public static boolean isHex(String hexString) {
		String regex = "^[A-Fa-f0-9]+$";
		if (hexString.matches(regex)) {
			return true;
		}
		return false;
	}

}
