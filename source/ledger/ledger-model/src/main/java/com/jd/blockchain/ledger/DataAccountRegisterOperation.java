package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.DataCodes;

@DataContract(code= DataCodes.TX_OP_DATA_ACC_REG)
public interface DataAccountRegisterOperation extends Operation {
	
    @DataField(order=1, refContract = true)
	BlockchainIdentity getAccountID();
    
    /**
     * 地址签名；
     * 
     * <br>
     * 这是账户身份 ({@link #getAccountID()}) 使用对应的私钥对地址做出的签名；
     * <br>
     * 在注册时将校验此签名与账户地址、公钥是否相匹配，以此保证只有私钥的持有者才能注册数据账户，确保数据账户的唯一性；
     * 
     * @return
     */
    @DataField(order=2, refContract = true)
    DigitalSignature getAddressSignature();

}
