package test.com.jd.blockchain.binaryproto.contract;

import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;

@EnumContract(code=0x0100)
public enum Level {

	V1((byte) 1),

	V2((byte) 2);

	@EnumField(type=PrimitiveType.INT8)
	public final byte CODE;
	public byte getCode() {
		return CODE;
	}
	private Level(byte code) {
		this.CODE = code;
	}

}


