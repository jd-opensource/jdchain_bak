//package com.jd.blockchain.ledger.core;
//
//import com.jd.blockchain.storage.service.ExPolicyKVStorage;
//import com.jd.blockchain.storage.service.VersioningKVEntry;
//import com.jd.blockchain.storage.service.VersioningKVStorage;
//
//public class PrefixAppender {
//
//	private PrefixAppender() {
//	}
//
//	/**
//	 * Wrapper the specified {@link VersioningKVStorage} and auto prefix it's key
//	 * with the specified String;
//	 * 
//	 * @param prefix
//	 * @param storage
//	 * @return
//	 */
//	public static VersioningKVStorage prefix(String prefix, VersioningKVStorage storage) {
//		return new VersioningKVStoragePrefixAppender(prefix, storage);
//	}
//	
////	public static SimpleKVStorage prefix(String prefix, SimpleKVStorage storage) {
////		return new SimpleKVStoragePrefixAppender(prefix, storage);
////	}
//	
//	public static ExPolicyKVStorage prefix(String prefix, ExPolicyKVStorage storage) {
//		return new ExistancePolicyKVStoragePrefixAppender(prefix, storage);
//	}
//	
//	private static String encodePrefixKey(String prefix, String key) {
//		return prefix.concat(key);
//	}
//
////	private static class SimpleKVStoragePrefixAppender implements SimpleKVStorage {
////
////		private SimpleKVStorage storage;
////
////		private String prefix;
////
////		private SimpleKVStoragePrefixAppender(String prefix, SimpleKVStorage dataStorage) {
////			this.prefix = prefix;
////			this.storage = dataStorage;
////		}
////
////		@Override
////		public byte[] get(String key) {
////			return storage.get(encodePrefixKey(prefix, key));
////		}
////
////		@Override
////		public boolean set(String key, byte[] value) {
////			return storage.set(encodePrefixKey(prefix, key), value);
////		}
////	}
//	
//	private static class ExistancePolicyKVStoragePrefixAppender implements ExPolicyKVStorage {
//		
//		private ExPolicyKVStorage storage;
//		
//		private String prefix;
//		
//		private ExistancePolicyKVStoragePrefixAppender(String prefix, ExPolicyKVStorage dataStorage) {
//			this.prefix = prefix;
//			this.storage = dataStorage;
//		}
//		
//		@Override
//		public byte[] get(String key) {
//			return storage.get(encodePrefixKey(prefix, key));
//		}
//		
//		@Override
//		public boolean exist(String key) {
//		return storage.exist(key);
//		}
//		
//		@Override
//		public boolean set(String key, byte[] value, ExPolicy ex) {
//			return storage.set(encodePrefixKey(prefix, key), value, ex);
//		}
//	}
//
//	private static class VersioningKVStoragePrefixAppender implements VersioningKVStorage {
//
//		private VersioningKVStorage storage;
//
//		private String prefix;
//
//		private VersioningKVStoragePrefixAppender(String prefix, VersioningKVStorage dataStorage) {
//			this.prefix = prefix;
//			this.storage = dataStorage;
//		}
//
//
//		@Override
//		public long getVersion(String key) {
//			return storage.getVersion(encodePrefixKey(prefix, key));
//		}
//
//		@Override
//		public VersioningKVEntry getEntry(String key, long version) {
//			return storage.getEntry(encodePrefixKey(prefix, key), version);
//		}
//
//		@Override
//		public byte[] get(String key, long version) {
//			return storage.get(encodePrefixKey(prefix, key), version);
//		}
//
//		@Override
//		public long set(String key, byte[] value, long version) {
//			return storage.set(encodePrefixKey(prefix, key), value, version);
//		}
//
//	}
//
//}
