package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
@DataContract(code = 0x05, name = "Primitive", description = "")
public interface PrimitiveDatas {

	@DataField(order = 2, primitiveType = DataType.BOOLEAN)
	boolean isEnable();

	@DataField(order = 3, primitiveType = DataType.INT8)
	byte isBoy();

	@DataField(order = 4, primitiveType = DataType.INT16)
	short getAge();

	@DataField(order = -1, primitiveType = DataType.INT32)
	int getId();

	@DataField(order = 6, primitiveType = DataType.TEXT)
	String getName();
	
	@DataField(order = 7, primitiveType = DataType.INT64)
	long getValue();
	
	@DataField(order = 12, primitiveType = DataType.BYTES)
	byte[] getImage();
	
	@DataField(order = 100, primitiveType = DataType.INT16)
	char getFlag();

	@DataField(order = 200, primitiveType = DataType.BYTES)
	Bytes getConfig();
	
	@DataField(order = 201, primitiveType = DataType.BYTES)
	Bytes getSetting();

	@DataField(order = 202, primitiveType = DataType.BYTES)
	NetworkAddress getNetworkAddr();

}
