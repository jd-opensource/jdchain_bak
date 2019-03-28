//package com.jd.blockchain.ledger.core;
//
//public class GenericAccountDataset<T> {
//
//	private Class<T> dataClazz;
//	
//	private AccountDataSet dataset;
//
//	protected GenericAccountDataset(AccountDataSet dataset, Class<T> dataClazz) {
//		this.dataClazz = dataClazz;
//		this.dataset = dataset;
//	}
//
//	protected T getData(String key) {
//		byte[] value = dataset.getBytes(key);
//		return deserialize(value);
//	}
//
//	protected T getData(String key, long version) {
//		byte[] value = dataset.getBytes(key, version);
//		return deserialize(value);
//	}
//
//	protected long setData(String key, T data, long version) {
//		byte[] value = serialize(data);
//		return dataset.setBytes(key, value, version);
//	}
//
//	private byte[] serialize(T data) {
//		throw new IllegalStateException("Not implemented!");
//	}
//
//	private T deserialize(byte[] value) {
//		throw new IllegalStateException("Not implemented!");
//	}
//}
