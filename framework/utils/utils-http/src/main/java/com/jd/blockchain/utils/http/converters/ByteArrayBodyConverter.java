package com.jd.blockchain.utils.http.converters;

import java.io.IOException;
import java.io.OutputStream;

import com.jd.blockchain.utils.http.RequestBodyConverter;

public class ByteArrayBodyConverter implements RequestBodyConverter{

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		out.write((byte[])param);
	}

}
