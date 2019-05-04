package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;

/**
 * Created by zhangshuang3 on 2018/11/30.
 */
@DataContract(code = 0xc, name = "CompositeDatas", description = "")
public interface CompositeDatas {

    @DataField(order = 1, primitiveType = DataType.BOOLEAN)
    boolean isEnable();

    @DataField(order = 2, refEnum = true)
    EnumLevel getLevel();

    @DataField(order = 3, refContract = true)
    PrimitiveDatas getPrimitive();

    @DataField(order=4, list = true, refContract=true, genericContract = true)
    Operation[] getOperations();

    @DataField(order = 5, primitiveType = DataType.INT16)
    short getAge();

}
