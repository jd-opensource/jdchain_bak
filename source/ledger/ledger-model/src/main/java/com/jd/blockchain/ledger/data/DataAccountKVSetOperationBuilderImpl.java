package com.jd.blockchain.ledger.data;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueImpl;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;

import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

public class DataAccountKVSetOperationBuilderImpl implements DataAccountKVSetOperationBuilder{
	
	private DataAccountKVSetOpTemplate operation;
	
	public DataAccountKVSetOperationBuilderImpl(Bytes accountAddress) {
		operation = new DataAccountKVSetOpTemplate(accountAddress);
	}

	@Override
	public DataAccountKVSetOperation getOperation() {
		return operation;
	}

	public static boolean isJson(String str) {
		boolean result = false;
		try {
			Object obj=JSON.parse(str);
			result = true;
		} catch (Exception e) {
			result=false;
		}
		return result;
	}

	@Override
	public DataAccountKVSetOperationBuilder set(String key, byte[] value, long expVersion) {
		BytesValue bytesValue = new BytesValueImpl(DataType.BYTES, value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder set(String key, String value, long expVersion) {
		BytesValue bytesValue;
		if (isJson(value)) {
			bytesValue = new BytesValueImpl(DataType.JSON, value.getBytes());
		}
		else {
			bytesValue = new BytesValueImpl(DataType.TEXT, value.getBytes());
		}
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder set(String key, Bytes value, long expVersion) {
		BytesValue bytesValue = new BytesValueImpl(DataType.BYTES, value.toBytes());
		operation.set(key, bytesValue, expVersion);
		return this;
	}
	@Override
	public DataAccountKVSetOperationBuilder set(String key, long value, long expVersion) {
		BytesValue bytesValue = new BytesValueImpl(DataType.INT64, BytesUtils.toBytes(value));
		operation.set(key, bytesValue, expVersion);
		return this;
	}

}
