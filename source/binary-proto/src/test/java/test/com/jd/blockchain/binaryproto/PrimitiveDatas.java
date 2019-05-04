package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
@DataContract(code = 0x05, name = "Primitive", description = "")
public interface PrimitiveDatas {

	@DataField(order = 2, primitiveType = PrimitiveType.BOOLEAN)
	boolean isEnable();

	@DataField(order = 3, primitiveType = PrimitiveType.INT8)
	byte isBoy();

	@DataField(order = 4, primitiveType = PrimitiveType.INT16)
	short getAge();

	@DataField(order = -1, primitiveType = PrimitiveType.INT32)
	int getId();

	@DataField(order = 6, primitiveType = PrimitiveType.TEXT)
	String getName();
	
	@DataField(order = 7, primitiveType = PrimitiveType.INT64)
	long getValue();
	
	@DataField(order = 12, primitiveType = PrimitiveType.BYTES)
	byte[] getImage();
	
	@DataField(order = 100, primitiveType = PrimitiveType.INT16)
	char getFlag();

	@DataField(order = 200, primitiveType = PrimitiveType.BYTES)
	Bytes getConfig();
	
	@DataField(order = 201, primitiveType = PrimitiveType.BYTES)
	Bytes getSetting();

	@DataField(order = 202, primitiveType = PrimitiveType.BYTES)
	NetworkAddress getNetworkAddr();

}
