package test.com.jd.blockchain.binaryproto;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;

/**
 * Created by zhangshuang3 on 2018/11/29.
 */
@DataContract(code = 0xb, name = "GenericRefContractDatas", description = "")
public interface GenericRefContractDatas {

    @DataField(order=1, list = true, refContract=true, genericContract = true)
    Operation[] getOperations();

}
