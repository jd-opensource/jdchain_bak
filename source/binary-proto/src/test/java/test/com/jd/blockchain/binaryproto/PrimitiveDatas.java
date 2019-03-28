package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.ValueType;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
@DataContract(code = 0x05, name = "Primitive", description = "")
public interface PrimitiveDatas {

	@DataField(order = 2, primitiveType = ValueType.BOOLEAN)
	boolean isEnable();

	@DataField(order = 3, primitiveType = ValueType.INT8)
	byte isBoy();

	@DataField(order = 4, primitiveType = ValueType.INT16)
	short getAge();

	@DataField(order = -1, primitiveType = ValueType.INT32)
	int getId();

	@DataField(order = 6, primitiveType = ValueType.TEXT)
	String getName();
	
	@DataField(order = 7, primitiveType = ValueType.INT64)
	long getValue();
	
	@DataField(order = 12, primitiveType = ValueType.BYTES)
	byte[] getImage();
	
	@DataField(order = 100, primitiveType = ValueType.INT16)
	char getFlag();

	@DataField(order = 200, primitiveType = ValueType.BYTES)
	Bytes getConfig();
	
	@DataField(order = 201, primitiveType = ValueType.BYTES)
	Bytes getSetting();

	@DataField(order = 202, primitiveType = ValueType.BYTES)
	NetworkAddress getNetworkAddr();

}
