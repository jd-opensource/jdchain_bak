package com.jd.blockchain.utils.http.converters;

import com.jd.blockchain.utils.http.StringConverter;

public class ObjectToStringConverter implements StringConverter {

	@Override
	public String toString(Object param) {
		return param == null ? null : param.toString();
	}

}
