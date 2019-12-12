package com.jd.blockchain.utils.serialize.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

public class JSONBeanSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
			throws IOException {
		SerializeWriter out = serializer.out;
		if (object == null) {
			out.writeNull();
			return;
		}
		JSONBean jsonBean = (JSONBean) object;
		out.write(jsonBean.toString());
	}

}
