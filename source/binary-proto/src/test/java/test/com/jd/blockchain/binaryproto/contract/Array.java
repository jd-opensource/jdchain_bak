package test.com.jd.blockchain.binaryproto.contract;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.DataType;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
@DataContract(code=0x08, name="Array" , description="")
public interface Array {

    @DataField(order=1, primitiveType= DataType.INT32, list=true)
    int[] getScores();

    @DataField(order=2, primitiveType=DataType.TEXT, list=true)
    String[] getFeatures();

    @DataField(order=3, primitiveType=DataType.BYTES)
    byte[] getFamilyMemberAges();

    @DataField(order=4, primitiveType=DataType.INT64, list=true)
    long[] getFamilyMemberIds();

}