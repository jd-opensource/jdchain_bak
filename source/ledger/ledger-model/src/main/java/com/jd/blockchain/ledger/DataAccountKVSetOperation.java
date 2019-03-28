package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.TypeCodes;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ValueType;

@DataContract(code= TypeCodes.TX_OP_DATA_ACC_SET)
public interface DataAccountKVSetOperation extends Operation{
	
	@DataField(order=2, primitiveType=ValueType.BYTES)
	Bytes getAccountAddress();
	
	@DataField(order=3, list=true, refContract=true)
	KVWriteEntry[] getWriteSet();
	
	
	@DataContract(code=TypeCodes.TX_OP_DATA_ACC_SET_KV)
	public static interface KVWriteEntry{

		@DataField(order=1, primitiveType=ValueType.TEXT)
		String getKey();

		@DataField(order=2, refContract = true)
		BytesValue getValue();

		@DataField(order=3, primitiveType=ValueType.INT64)
		long getExpectedVersion();
	}

}
