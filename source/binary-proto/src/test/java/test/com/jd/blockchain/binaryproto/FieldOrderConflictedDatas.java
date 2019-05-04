package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
@DataContract(code = 0x06, name = "Primitive", description = "")
public interface FieldOrderConflictedDatas {

	@DataField(order = 2, primitiveType = DataType.BOOLEAN)
	boolean isEnable();

	@DataField(order = 3, primitiveType = DataType.INT8)
	byte isBoy();

	@DataField(order = 7, primitiveType = DataType.INT16)
	short getAge();

	@DataField(order = -1, primitiveType = DataType.INT32)
	int getId();

	@DataField(order = 6, primitiveType = DataType.TEXT)
	String getName();
	
	@DataField(order = 7, primitiveType = DataType.INT64)
	long getValue();
	

}
