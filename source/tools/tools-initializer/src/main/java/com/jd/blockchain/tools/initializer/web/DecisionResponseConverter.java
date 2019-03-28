package com.jd.blockchain.tools.initializer.web;

import java.io.InputStream;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.tools.initializer.LedgerInitException;
import com.jd.blockchain.utils.http.HttpServiceContext;
import com.jd.blockchain.utils.http.ResponseConverter;
import com.jd.blockchain.utils.http.agent.ServiceRequest;

public class DecisionResponseConverter implements ResponseConverter {

	@Override
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext)
			throws Exception {
		LedgerInitResponse resp = LedgerInitResponse.resolve(responseStream);
		if (resp.isError()) {
			throw new LedgerInitException("Error occurred at remote participant! --" + resp.getErrorMessage());
		}
		return BinaryEncodingUtils.decode(resp.getData());
	}

}
