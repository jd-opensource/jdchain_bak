package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueEntry;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.BytesValueType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

public class DataAccountKVSetOperationBuilderImpl implements DataAccountKVSetOperationBuilder{
	
	private DataAccountKVSetOpTemplate operation;
	
	public DataAccountKVSetOperationBuilderImpl(Bytes accountAddress) {
		operation = new DataAccountKVSetOpTemplate(accountAddress);
	}

	@Override
	public DataAccountKVSetOperation getOperation() {
		return operation;
	}

	@Override
	public DataAccountKVSetOperationBuilder set(String key, byte[] value, long expVersion) {
		BytesValue bytesValue = new BytesValueEntry(BytesValueType.BYTES, value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder set(String key, String value, long expVersion) {
		BytesValue bytesValue;
		if (JSONSerializeUtils.isJSON(value)) {
			bytesValue = new BytesValueEntry(BytesValueType.JSON, value.getBytes());
		}
		else {
			bytesValue = new BytesValueEntry(BytesValueType.TEXT, value.getBytes());
		}
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder set(String key, Bytes value, long expVersion) {
		BytesValue bytesValue = new BytesValueEntry(BytesValueType.BYTES, value.toBytes());
		operation.set(key, bytesValue, expVersion);
		return this;
	}
	@Override
	public DataAccountKVSetOperationBuilder set(String key, long value, long expVersion) {
		BytesValue bytesValue = new BytesValueEntry(BytesValueType.INT64, BytesUtils.toBytes(value));
		operation.set(key, bytesValue, expVersion);
		return this;
	}

}
