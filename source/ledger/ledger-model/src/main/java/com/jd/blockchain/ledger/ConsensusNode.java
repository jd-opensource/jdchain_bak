//package com.jd.blockchain.ledger;
//
//import com.jd.blockchain.base.data.TypeCodes;
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataField;
//
//import my.utils.ValueType;
//
///**
// * 共识节点； <br>
// * 以地址和端口一起作为唯一标识；
// * 
// * @author huanghaiquan
// *
// */
//@DataContract(code = TypeCodes.METADATA_CONSENSUS_NODE)
//public interface ConsensusNode {
//
//	/**
//	 * 参与者ID；
//	 * 
//	 * @return
//	 */
//	@DataField(order = 1, primitiveType = ValueType.INT32)
//	int getId();
//
////	/**
////	 * 共识节点的主机地址；
////	 * 
////	 * <br>
////	 * 该节点以此地址参与共识服务；
////	 * 
////	 * @return
////	 */
////	@DataField(order = 2, primitiveType = ValueType.BYTES)
////	NetworkAddress getConsensusAddress();
//
//}
