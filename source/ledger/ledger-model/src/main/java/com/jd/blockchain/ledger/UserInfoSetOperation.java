package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;

/**
 * @author huanghaiquan
 *
 */
@DataContract(code=DataCodes.TX_OP_USER_INFO_SET)
public interface UserInfoSetOperation extends Operation {
	
	String getUserAddress();
	
	KVEntry[] getPropertiesWriteSet();
	

	@DataContract(code=DataCodes.TX_OP_USER_INFO_SET_KV)
	public static interface KVEntry{
		
		String getKey();
		
		String getValue();
		
		long getExpectedVersion();
	}

	
}
