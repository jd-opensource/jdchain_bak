//package com.jd.blockchain.ledger.core;
//
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataField;
//import com.jd.blockchain.binaryproto.PrimitiveType;
//import com.jd.blockchain.consts.DataCodes;
//import com.jd.blockchain.crypto.HashDigest;
//
//@DataContract(code = DataCodes.METADATA)
//public interface LedgerMetadata {
//
//	/**
//	 * 账本的初始化种子；
//	 *
//	 * @return
//	 */
//	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
//	byte[] getSeed();
//
//	/**
//	 * 共识参与方的默克尔树的根；
//	 *
//	 * @return
//	 */
//	@DataField(order = 2, primitiveType = PrimitiveType.BYTES)
//	HashDigest getParticipantsHash();
//
//	/**
//	 * 账本配置；
//	 *
//	 * @return
//	 */
//	@DataField(order = 3, refContract = true)
//	LedgerSetting getSetting();
//
//}
