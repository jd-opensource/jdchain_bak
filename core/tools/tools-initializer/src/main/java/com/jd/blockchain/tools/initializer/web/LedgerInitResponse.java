package com.jd.blockchain.tools.initializer.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesEncoding;
import com.jd.blockchain.utils.io.BytesSerializable;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.NumberMask;

public class LedgerInitResponse implements BytesSerializable, Serializable {

	private static final long serialVersionUID = -475554045260920722L;

	private boolean error;

	private String errorMessage;

	private byte[] data;

	public boolean isError() {
		return error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public byte[] getData() {
		return data;
	}

	private LedgerInitResponse() {
	}

	public static LedgerInitResponse error(String messageFormat, Object... args) {
		LedgerInitResponse resp = new LedgerInitResponse();
		resp.error = true;
		resp.errorMessage = String.format(messageFormat, args);
		return resp;
	}

	public static LedgerInitResponse success(byte[] data) {
		LedgerInitResponse resp = new LedgerInitResponse();
		resp.error = false;
		resp.data = data;
		return resp;
	}

	public static LedgerInitResponse resolve(InputStream in) {
		LedgerInitResponse resp = new LedgerInitResponse();
		long uid = BytesUtils.readLong(in);
		if (uid != serialVersionUID) {
			throw new IllegalArgumentException("Illegal bytes of " + LedgerInitResponse.class.getName() + "!");
		}
		resp.error = BytesUtils.readByte(in) == 1;
		if (resp.error) {
			byte[] errorMsgBytes = BytesEncoding.read(NumberMask.SHORT, in);
			resp.errorMessage = BytesUtils.toString(errorMsgBytes, "UTF-8");
		}
		resp.data = BytesEncoding.read(NumberMask.NORMAL, in);
		return resp;
	}

	public static LedgerInitResponse resolve(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		return resolve(in);
	}

	@Override
	public byte[] toBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BytesUtils.writeLong(serialVersionUID, out);
		BytesUtils.writeByte(error ? (byte) 1 : (byte) 0, out);
		if (error) {
			byte[] errorBytes = ByteArray.fromString(errorMessage, "UTF-8");
			BytesEncoding.write(errorBytes, NumberMask.SHORT, out);
		}
		BytesEncoding.write(data, NumberMask.NORMAL, out);
		return out.toByteArray();
	}
}
