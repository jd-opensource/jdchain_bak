//package com.jd.blockchain.ledger;
//
//import com.jd.blockchain.base.data.TypeCodes;
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataField;
//import com.jd.blockchain.crypto.asymmetric.PubKey;
//
//import my.utils.ValueType;
//
///**
// * 参与方信息；
// * 
// * @author huanghaiquan
// *
// */
//@DataContract(code = TypeCodes.METADATA_PARTICIPANT_INFO)
//public interface ParticipantInfo {
//
//	/**
//	 * 参与者名称；
//	 * 
//	 * @return
//	 */
//	@DataField(order = 1, primitiveType = ValueType.TEXT)
//	String getName();
//
//	/**
//	 * 公钥；
//	 * 
//	 * @return
//	 */
//	@DataField(order = 2, primitiveType = ValueType.BYTES)
//	PubKey getPubKey();
//
//}