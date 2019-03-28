package com.jd.blockchain.utils.http.converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.StreamUtils;

import com.jd.blockchain.utils.http.HttpServiceContext;
import com.jd.blockchain.utils.http.ResponseConverter;
import com.jd.blockchain.utils.http.agent.ServiceRequest;

public class ByteArrayResponseConverter implements ResponseConverter {

	public static final ByteArrayResponseConverter INSTANCE = new ByteArrayResponseConverter();

	private ByteArrayResponseConverter() {
	}

	@Override
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			StreamUtils.copy(responseStream, out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
