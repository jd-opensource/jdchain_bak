package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ValueType;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
@DataContract(code = 0x06, name = "Primitive", description = "")
public interface FieldOrderConflictedDatas {

	@DataField(order = 2, primitiveType = ValueType.BOOLEAN)
	boolean isEnable();

	@DataField(order = 3, primitiveType = ValueType.INT8)
	byte isBoy();

	@DataField(order = 7, primitiveType = ValueType.INT16)
	short getAge();

	@DataField(order = -1, primitiveType = ValueType.INT32)
	int getId();

	@DataField(order = 6, primitiveType = ValueType.TEXT)
	String getName();
	
	@DataField(order = 7, primitiveType = ValueType.INT64)
	long getValue();
	

}
