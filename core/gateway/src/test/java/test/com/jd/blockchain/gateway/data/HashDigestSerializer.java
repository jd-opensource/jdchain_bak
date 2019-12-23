package test.com.jd.blockchain.gateway.data;

import java.io.IOException;
import java.lang.reflect.Type;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.jd.blockchain.crypto.HashDigest;

public class HashDigestSerializer implements ObjectSerializer {
	
	public static HashDigestSerializer INSTANCE = new HashDigestSerializer();

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
			throws IOException {
		SerializeWriter out = serializer.out;
		if (object == null) {
			out.writeNull();
			return;
		}
		HashDigest hash = (HashDigest) object;
		out.writeString(hash.toBase58());
	}

}
