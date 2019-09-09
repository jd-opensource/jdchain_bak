//package com.jd.blockchain.ledger.core;
//
//public class SettingContext {
//
//	private static final TxSettingContext txSettings = new TxSettingContext();
//	
//	private static final QueryingSettingContext queryingSettings = new QueryingSettingContext();
//
//	public static TxSettingContext txSettings() {
//		return txSettings;
//	}
//	
//	public static QueryingSettingContext queryingSettings() {
//		return queryingSettings;
//	}
//
//	/**
//	 * 与交易处理相关的设置；
//	 * @author huanghaiquan
//	 *
//	 */
//	public static class TxSettingContext {
//
//		public boolean verifyLedger() {
//			return true;
//		}
//
//		public boolean verifySignature() {
//			return true;
//		}
//
//	}
//	
//	/**
//	 * 与账本查询相关的设置；
//	 * @author huanghaiquan
//	 *
//	 */
//	public static class QueryingSettingContext {
//		
//		/**
//		 * 查询区块等具有 hash 标识符的对象时是否重新校验哈希；
//		 * @return
//		 */
//		public boolean verifyHash() {
//			return false;
//		}
//		
//	}
//
//}
