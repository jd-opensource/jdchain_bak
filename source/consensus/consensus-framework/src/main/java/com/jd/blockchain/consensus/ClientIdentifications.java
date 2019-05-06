/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.consensus.ClientIdentifications
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/12/19 下午3:58
 * Description:
 */
package com.jd.blockchain.consensus;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.consts.DataCodes;

/**
 *
 * @author shaozhuguang
 * @create 2018/12/19
 * @since 1.0.0
 */
@DataContract(code = DataCodes.CLIENT_IDENTIFICATIONS)
public interface ClientIdentifications {

    @DataField(order = 0, list = true, refContract = true, genericContract = true)
    ClientIdentification[] getClientIdentifications();
}