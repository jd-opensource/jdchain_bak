package com.jd.blockchain.utils.serialize.json;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * JSONString 用于包装不需要执行 JSON 序列化而直接输出的字符串；
 * @author haiq
 *
 */
@JSONType(serializer=JSONStringSerializer.class,deserializer =JSONStringDeserializer.class)
public final class JSONString {
	
	private String jsonString;
	
	public JSONString(String jsonString) {
		if (jsonString == null) {
			throw new IllegalArgumentException("Null json string!");
		}
//		if (!SerializeUtils.isJSON(jsonString)) {
//			throw new IllegalArgumentException("The arg is not a JSON string!");
//		}
		this.jsonString = jsonString;
	}
	
	private JSONString() {
	}
	
	@Override
	public String toString() {
		return jsonString;
	}
	
	public static JSONString toJSONString(Object data){
		if (data == null) {
			return null;
		}
		String jsonString = JSONSerializeUtils.serializeToJSON(data);
		JSONString retn = new JSONString();
		retn.jsonString = jsonString;
		return retn;
	}
}
