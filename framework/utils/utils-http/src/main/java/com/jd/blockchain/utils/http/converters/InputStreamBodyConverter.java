package com.jd.blockchain.utils.http.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jd.blockchain.utils.http.RequestBodyConverter;
import com.jd.blockchain.utils.io.BytesUtils;

public class InputStreamBodyConverter implements RequestBodyConverter{

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		BytesUtils.copy((InputStream)param, out);
	}

}
