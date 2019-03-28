package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;

/**
 * 数字签名；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.DIGITALSIGNATURE)
public interface DigitalSignature extends DigitalSignatureBody {

}
