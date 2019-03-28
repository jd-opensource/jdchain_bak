package test.com.jd.blockchain.binaryproto.contract;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
@DataContract(code=0x06, name="RefEnum" , description="")
public interface RefEnum {

    @DataField(order=1, refEnum=true)
    Level getLevel();

}