package com.jd.blockchain.utils.http.converters;

import java.io.IOException;
import java.io.OutputStream;

import com.jd.blockchain.utils.http.RequestBodyConverter;

public class EmptyBodyConverter implements RequestBodyConverter{

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		//Do nothing;
	}

}
