package com.jd.blockchain.ledger;

public class BytesDataList implements BytesValueList {

	private BytesValue[] bytesValues;

	public BytesDataList(BytesValue... bytesValues) {
		this.bytesValues = bytesValues;
	}

	@Override
	public BytesValue[] getValues() {
		return bytesValues;
	}

	public static BytesValueList singleText(String value) {
		return new BytesDataList(TypedValue.fromText(value));
	}
	
	public static BytesValueList singleLong(long value) {
		return new BytesDataList(TypedValue.fromInt64(value));
	}
	
	public static BytesValueList singleInt(int value) {
		return new BytesDataList(TypedValue.fromInt32(value));
	}
	
	public static BytesValueList singleBoolean(boolean value) {
		return new BytesDataList(TypedValue.fromBoolean(value));
	}
}
