package com.jd.blockchain.tools.initializer.web;

import java.io.IOException;
import java.io.OutputStream;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.utils.http.RequestBodyConverter;

public class DecisionRequestBodyConverter implements RequestBodyConverter {

	@Override
	public void write(Object param, OutputStream out) throws IOException {
		if (param instanceof LedgerInitDecision) {
			BinaryEncodingUtils.encode(param, LedgerInitDecision.class, out);
			return;
		}
	}

}