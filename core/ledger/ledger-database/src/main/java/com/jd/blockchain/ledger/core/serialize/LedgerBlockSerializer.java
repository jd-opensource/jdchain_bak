package com.jd.blockchain.ledger.core.serialize;

import java.lang.reflect.Type;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.jd.blockchain.ledger.LedgerBlock;

public class LedgerBlockSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
		SerializeWriter out = serializer.out;
		if (object == null) {
			out.writeNull();
			return;
		}
		if (object instanceof LedgerBlock) {
			LedgerBlock ledgerBlock = (LedgerBlock) object;
//			out.writeString(hash.toBase58());
		}
	}
}
