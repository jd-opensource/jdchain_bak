package test.com.jd.blockchain.binaryproto.contract;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;

/**
 * Created by zhangshuang3 on 2018/7/9.
 */
@DataContract(code=0x02, name="Address" , description="")
public interface AddressCodeDuplicate {

    @DataField(order=1, primitiveType= PrimitiveType.TEXT)
    String getStreet();

    @DataField(order=2, primitiveType=PrimitiveType.INT32)
    int getNumber();

}