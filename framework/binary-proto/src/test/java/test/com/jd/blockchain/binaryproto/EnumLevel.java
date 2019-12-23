package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.binaryproto.EnumContract;
import com.jd.blockchain.binaryproto.EnumField;

/**
 * Created by zhangshuang3 on 2018/11/29.
 */
@EnumContract(code=0x0100, name = "EnumLevel", decription = "")
public enum EnumLevel {

    V1((byte) 1),

    V2((byte) 2);

    @EnumField(type= PrimitiveType.INT8)
    public final byte CODE;
    public byte getCode() {
        return CODE;
    }
    private EnumLevel(byte code) {
        this.CODE = code;
    }

}

