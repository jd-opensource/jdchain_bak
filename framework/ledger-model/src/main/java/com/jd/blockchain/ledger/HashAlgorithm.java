//package com.jd.blockchain.ledger;
//
///**
// * Hash 算法的代码常量；
// * 
// * @author zhaoming9
// *
// */
//public enum HashAlgorithm {
//
//	RIPE160((byte) 1),
//
//	SHA256((byte) 2),
//	
//	SM3((byte) 4);
//
//	public final byte CODE;
//
//	private HashAlgorithm(byte algorithm) {
//		CODE = algorithm;
//	}
//
//	public byte getAlgorithm() {
//		return CODE;
//	}
//
//	public static HashAlgorithm valueOf(byte algorithm) {
//		for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
//			if (hashAlgorithm.CODE == algorithm) {
//				return hashAlgorithm;
//			}
//		}
//		throw new IllegalArgumentException("Unsupported hash algorithm [" + algorithm + "]!");
//	}
//
//	public static void checkHashAlgorithm(HashAlgorithm algorithm) {
//		switch (algorithm) {
//			case RIPE160:
//				break;
//			case SHA256:
//				break;
//			default:
//				throw new IllegalArgumentException("Unsupported hash algorithm [" + algorithm + "]!");
//		}
//	}
//}
