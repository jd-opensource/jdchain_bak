package test.com.jd.blockchain.binaryproto.contract;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
@DataContract(code=0x08, name="Array" , description="")
public interface Array {

    @DataField(order=1, primitiveType= PrimitiveType.INT32, list=true)
    int[] getScores();

    @DataField(order=2, primitiveType=PrimitiveType.TEXT, list=true)
    String[] getFeatures();

    @DataField(order=3, primitiveType=PrimitiveType.BYTES)
    byte[] getFamilyMemberAges();

    @DataField(order=4, primitiveType=PrimitiveType.INT64, list=true)
    long[] getFamilyMemberIds();

}