package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.utils.ValueType;
import com.jd.blockchain.utils.io.BytesSlice;

@DataContract(code = TypeCodes.BYTES_VALUE)
public interface BytesValue {

	/**
	 * 数据类型；
	 * 
	 * @return
	 */
	@DataField(order = 0, refEnum = true)
	DataType getType();

	/**
	 * 数据值的二进制序列；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = ValueType.BYTES)
	BytesSlice getValue();
	
}
