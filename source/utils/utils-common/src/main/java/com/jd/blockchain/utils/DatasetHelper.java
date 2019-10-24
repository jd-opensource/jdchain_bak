package com.jd.blockchain.utils;

/**
 * Helper for {@link Dataset};
 * 
 * @author huanghaiquan
 *
 */
public class DatasetHelper {

	public static final TypeMapper<Bytes, String> UTF8_STRING_BYTES_MAPPER = new TypeMapper<Bytes, String>() {

		@Override
		public Bytes encode(String t2) {
			return Bytes.fromString(t2);
		}

		@Override
		public String decode(Bytes t1) {
			return t1.toUTF8String();
		}
	};

	public static final TypeMapper<String, Bytes> BYTES_UTF8_STRING_MAPPER = new TypeMapper<String, Bytes>() {

		@Override
		public String encode(Bytes t1) {
			return t1.toUTF8String();
		}

		@Override
		public Bytes decode(String t2) {
			return Bytes.fromString(t2);
		}
	};

	/**
	 * 适配两个不同类型参数的数据集；
	 * 
	 * @param <K1>        适配输入的 键 类型；
	 * @param <K2>        适配输出的 键 类型；
	 * @param <V1>        适配输入的 值 类型；
	 * @param <V2>        适配输出的 值 类型；
	 * @param dataset     数据集；
	 * @param keyMapper   键的映射配置；
	 * @param valueMapper 值的映射配置；
	 * @return
	 */
	public static <V> Dataset<String, V> map(Dataset<Bytes, V> dataset) {
		return new TypeAdapter<Bytes, String, V, V>(dataset, UTF8_STRING_BYTES_MAPPER, new EmptyMapper<V>());
	}

	/**
	 * 适配两个不同类型参数的数据集；
	 * 
	 * @param <K1>        适配输入的 键 类型；
	 * @param <K2>        适配输出的 键 类型；
	 * @param <V1>        适配输入的 值 类型；
	 * @param <V2>        适配输出的 值 类型；
	 * @param dataset     数据集；
	 * @param keyMapper   键的映射配置；
	 * @param valueMapper 值的映射配置；
	 * @return
	 */
	public static <V1, V2> Dataset<String, V2> map(Dataset<Bytes, V1> dataset, TypeMapper<V1, V2> valueMapper) {
		return new TypeAdapter<Bytes, String, V1, V2>(dataset, UTF8_STRING_BYTES_MAPPER, valueMapper);
	}

	/**
	 * 适配两个不同类型参数的数据集；
	 * 
	 * @param <K1>        适配输入的 键 类型；
	 * @param <K2>        适配输出的 键 类型；
	 * @param <V1>        适配输入的 值 类型；
	 * @param <V2>        适配输出的 值 类型；
	 * @param dataset     数据集；
	 * @param keyMapper   键的映射配置；
	 * @param valueMapper 值的映射配置；
	 * @return
	 */
	public static <K1, K2, V1, V2> Dataset<K2, V2> map(Dataset<K1, V1> dataset, TypeMapper<K1, K2> keyMapper,
			TypeMapper<V1, V2> valueMapper) {
		return new TypeAdapter<K1, K2, V1, V2>(dataset, keyMapper, valueMapper);
	}

	/**
	 * 监听对数据集的变更；
	 * 
	 * @param <K>      键 类型；
	 * @param <V>      值 类型；
	 * @param dataset  要监听的数据集；
	 * @param listener 要植入的监听器；
	 * @return 植入监听器的数据集实例；
	 */
	public static <K, V> Dataset<K, V> listen(Dataset<K, V> dataset, DataChangedListener<K, V> listener) {
		return new DatasetUpdatingMonitor<K, V>(dataset, listener);
	}

	/**
	 * 数据修改监听器；
	 * 
	 * @author huanghaiquan
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static interface DataChangedListener<K, V> {

		void onChanged(K key, V value, long expectedVersion, long newVersion);

	}

	/**
	 * 类型映射接口；
	 * 
	 * @author huanghaiquan
	 *
	 * @param <T1>
	 * @param <T2>
	 */
	public static interface TypeMapper<T1, T2> {

		T1 encode(T2 t2);

		T2 decode(T1 t1);

	}

	private static class EmptyMapper<T> implements TypeMapper<T, T> {

		@Override
		public T encode(T t) {
			return t;
		}

		@Override
		public T decode(T t) {
			return t;
		}

	}

	private static class DatasetUpdatingMonitor<K, V> implements Dataset<K, V> {

		private Dataset<K, V> dataset;

		private DataChangedListener<K, V> listener;

		public DatasetUpdatingMonitor(Dataset<K, V> dataset, DataChangedListener<K, V> listener) {
			this.dataset = dataset;
			this.listener = listener;
		}

		@Override
		public long getDataCount() {
			return dataset.getDataCount();
		}

		@Override
		public long setValue(K key, V value, long version) {
			long newVersion = dataset.setValue(key, value, version);
			if (newVersion > -1) {
				listener.onChanged(key, value, version, newVersion);
			}
			return newVersion;
		}

		@Override
		public V getValue(K key, long version) {
			return dataset.getValue(key, version);
		}

		@Override
		public V getValue(K key) {
			return dataset.getValue(key);
		}

		@Override
		public long getVersion(K key) {
			return dataset.getVersion(key);
		}

		@Override
		public DataEntry<K, V> getDataEntry(K key) {
			return dataset.getDataEntry(key);
		}

		@Override
		public DataEntry<K, V> getDataEntry(K key, long version) {
			return dataset.getDataEntry(key, version);
		}

		@Override
		public DataIterator<K, V> iterator() {
			return dataset.iterator();
		}

		@Override
		public DataIterator<K, V> iteratorDesc() {
			return dataset.iteratorDesc();
		}

	}

	/**
	 * 类型适配器；
	 * 
	 * @author huanghaiquan
	 *
	 * @param <K1>
	 * @param <K2>
	 * @param <V1>
	 * @param <V2>
	 */
	private static class TypeAdapter<K1, K2, V1, V2> implements Dataset<K2, V2> {
		private Dataset<K1, V1> dataset;
		private TypeMapper<K1, K2> keyMapper;
		private TypeMapper<V1, V2> valueMapper;

		public TypeAdapter(Dataset<K1, V1> dataset, TypeMapper<K1, K2> keyMapper, TypeMapper<V1, V2> valueMapper) {
			this.dataset = dataset;
			this.keyMapper = keyMapper;
			this.valueMapper = valueMapper;
		}

		@Override
		public long getDataCount() {
			return dataset.getDataCount();
		}

		@Override
		public long setValue(K2 key, V2 value, long version) {
			K1 key1 = keyMapper.encode(key);
			V1 value1 = valueMapper.encode(value);
			return dataset.setValue(key1, value1, version);
		}

		@Override
		public V2 getValue(K2 key, long version) {
			K1 k = keyMapper.encode(key);
			V1 v = dataset.getValue(k, version);
			if (v == null) {
				return null;
			}
			return valueMapper.decode(v);
		}

		@Override
		public V2 getValue(K2 key) {
			K1 k = keyMapper.encode(key);
			V1 v = dataset.getValue(k);
			if (v == null) {
				return null;
			}
			return valueMapper.decode(v);
		}

		@Override
		public long getVersion(K2 key) {
			K1 k = keyMapper.encode(key);
			return dataset.getVersion(k);
		}

		@Override
		public DataEntry<K2, V2> getDataEntry(K2 key) {
			K1 k = keyMapper.encode(key);
			DataEntry<K1, V1> entry = dataset.getDataEntry(k);
			if (entry == null) {
				return null;
			}
			V2 v = valueMapper.decode(entry.getValue());
			return new KeyValueEntry<K2, V2>(key, v, entry.getVersion());
		}

		@Override
		public DataEntry<K2, V2> getDataEntry(K2 key, long version) {
			K1 k = keyMapper.encode(key);
			DataEntry<K1, V1> entry = dataset.getDataEntry(k, version);
			if (entry == null) {
				return null;
			}
			V2 v = valueMapper.decode(entry.getValue());
			return new KeyValueEntry<K2, V2>(key, v, entry.getVersion());
		}

		@Override
		public DataIterator<K2, V2> iterator() {
			DataIterator<K1, V1> it = dataset.iterator();
			return new DataIteratorAdapter<K1, K2, V1, V2>(it, keyMapper, valueMapper);
		}

		@Override
		public DataIterator<K2, V2> iteratorDesc() {
			DataIterator<K1, V1> it = dataset.iteratorDesc();
			return new DataIteratorAdapter<K1, K2, V1, V2>(it, keyMapper, valueMapper);
		}

	}

	private static class DataIteratorAdapter<K1, K2, V1, V2> implements DataIterator<K2, V2> {

		private DataIterator<K1, V1> iterator;

		private TypeMapper<K1, K2> keyMapper;
		private TypeMapper<V1, V2> valueMapper;

		public DataIteratorAdapter(DataIterator<K1, V1> iterator, TypeMapper<K1, K2> keyMapper,
				TypeMapper<V1, V2> valueMapper) {
			this.iterator = iterator;
			this.keyMapper = keyMapper;
			this.valueMapper = valueMapper;
		}

		@Override
		public void skip(long count) {
			iterator.skip(count);
		}

		@Override
		public DataEntry<K2, V2> next() {
			DataEntry<K1, V1> entry = iterator.next();
			return cast(entry);
		}

		private DataEntry<K2, V2> cast(DataEntry<K1, V1> entry) {
			if (entry == null) {
				return null;
			}

			K2 k = keyMapper.decode(entry.getKey());
			V2 v = valueMapper.decode(entry.getValue());
			return new KeyValueEntry<K2, V2>(k, v, entry.getVersion());
		}

		@SuppressWarnings("unchecked")
		@Override
		public DataEntry<K2, V2>[] next(int count) {
			DataEntry<K1, V1>[] entries = iterator.next(count);
			if (entries == null) {
				return null;
			}
			if (entries.length == 0) {
				return (DataEntry<K2, V2>[]) entries;
			}
			return ArrayUtils.castTo(entries, DataEntry.class, e -> cast(e));
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

	}

	private static class KeyValueEntry<K, V> implements DataEntry<K, V> {

		private K key;

		private V value;

		private long version;

		public KeyValueEntry(K key, V value, long version) {
			this.key = key;
			this.value = value;
			this.version = version;
		}

		public K getKey() {
			return key;
		}

		public long getVersion() {
			return version;
		}

		public V getValue() {
			return value;
		}

	}
}
