package com.jd.blockchain.ledger.core.serialize;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.codec.Base58Utils;

import java.lang.reflect.Type;

public class LedgerBlockDeserializer implements ObjectDeserializer{
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		if (type instanceof Class && HashDigest.class.isAssignableFrom((Class<?>) type)) {
			String base58Str = parser.parseObject(String.class);
			byte[] hashBytes = Base58Utils.decode(base58Str);
			return (T) new HashDigest(hashBytes);
		}
		return (T) parser.parse(fieldName);
	}

	@Override
	public int getFastMatchToken() {
		return JSONToken.LBRACE;
	}

}
