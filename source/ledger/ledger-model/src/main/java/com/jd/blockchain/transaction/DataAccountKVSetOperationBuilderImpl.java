package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueEntry;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.utils.Bytes;

public class DataAccountKVSetOperationBuilderImpl implements DataAccountKVSetOperationBuilder {

	private DataAccountKVSetOpTemplate operation;

	public DataAccountKVSetOperationBuilderImpl(Bytes accountAddress) {
		operation = new DataAccountKVSetOpTemplate(accountAddress);
	}

	@Override
	public DataAccountKVSetOperation getOperation() {
		return operation;
	}

//	@Deprecated
//	@Override
//	public DataAccountKVSetOperationBuilder set(String key, byte[] value, long expVersion) {
//		return setBytes(key, value, expVersion);
//	}

	@Override
	public DataAccountKVSetOperationBuilder setBytes(String key, byte[] value, long expVersion) {
		BytesValue bytesValue = BytesValueEntry.fromBytes(value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder setImage(String key, byte[] value, long expVersion) {
		BytesValue bytesValue = BytesValueEntry.fromImage(value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

//	@Override
//	public DataAccountKVSetOperationBuilder set(String key, String value, long expVersion) {
//		return setText(key, value, expVersion);
//	}

	@Override
	public DataAccountKVSetOperationBuilder setText(String key, String value, long expVersion) {
		BytesValue bytesValue = BytesValueEntry.fromText(value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder setBytes(String key, Bytes value, long expVersion) {
		BytesValue bytesValue = BytesValueEntry.fromBytes(value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder setInt64(String key, long value, long expVersion) {
		BytesValue bytesValue = BytesValueEntry.fromInt64(value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder setJSON(String key, String value, long expVersion) {
		BytesValue bytesValue = BytesValueEntry.fromJSON(value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder setXML(String key, String value, long expVersion) {
		BytesValue bytesValue = BytesValueEntry.fromXML(value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

	@Override
	public DataAccountKVSetOperationBuilder setTimestamp(String key, long value, long expVersion) {
		BytesValue bytesValue = BytesValueEntry.fromTimestamp(value);
		operation.set(key, bytesValue, expVersion);
		return this;
	}

}
