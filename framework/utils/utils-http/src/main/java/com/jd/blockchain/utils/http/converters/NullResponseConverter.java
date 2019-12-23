package com.jd.blockchain.utils.http.converters;

import java.io.InputStream;

import com.jd.blockchain.utils.http.HttpServiceContext;
import com.jd.blockchain.utils.http.ResponseConverter;
import com.jd.blockchain.utils.http.agent.ServiceRequest;

public class NullResponseConverter implements ResponseConverter {
	
	public static final ResponseConverter INSTANCE = new NullResponseConverter();
	
	private NullResponseConverter() {
	}

	@Override
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) {
		return null;
	}

}
