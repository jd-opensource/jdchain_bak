package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;

/**
 * Created by zhangshuang3 on 2018/11/29.
 */
@DataContract(code = 0xa, name = "SubOperation", description = "")
public interface SubOperation extends Operation {

    @DataField(order=1, primitiveType = DataType.TEXT)
    String getUserName();

}
