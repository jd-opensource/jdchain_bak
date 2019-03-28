//package com.jd.blockchain.ledger;
//
//import com.jd.blockchain.binaryproto.DataContract;
//
///**
// * @author huanghaiquan
// *
// */
//@DataContract(code=LedgerCodes.TX_OP_USER_INFO_SET)
//public interface UserInfoSetOperation extends Operation {
//	
//	@Override
//	default OperationType getType() {
//		return OperationType.SET_USER_INFO;
//	}
//	
//	String getUserAddress();
//	
//	KVEntry[] getPropertiesWriteSet();
//	
//
//	@DataContract(code=LedgerCodes.TX_OP_USER_INFO_SET_KV)
//	public static interface KVEntry{
//		
//		String getKey();
//		
//		String getValue();
//		
//		long getExpectedVersion();
//	}
//
//	
//}
