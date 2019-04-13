package com.jd.blockchain.tools.initializer.web;

import java.io.IOException;
import java.io.OutputStream;

import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.utils.http.RequestBodyConverter;

public class SignatureDigestRequestBodyConverter implements RequestBodyConverter {

	@Override
	public void write(Object param, OutputStream out) throws IOException {
		if (param instanceof SignatureDigest) {
			out.write(((SignatureDigest)param).toBytes());
			return;
		}
	}

}