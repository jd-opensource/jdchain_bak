package com.jd.blockchain.utils.http.converters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.jd.blockchain.utils.http.HttpServiceConsts;
import com.jd.blockchain.utils.http.RequestBodyConverter;

public class ObjectToStringBodyConverter implements RequestBodyConverter {

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		try {
			String text = param.toString();
			byte[] bytes = text.getBytes(HttpServiceConsts.CHARSET);
			out.write(bytes);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
