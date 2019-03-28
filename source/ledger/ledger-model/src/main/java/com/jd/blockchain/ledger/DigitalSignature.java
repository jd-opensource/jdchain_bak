package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.TypeCodes;

/**
 * 数字签名；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.DIGITALSIGNATURE)
public interface DigitalSignature extends DigitalSignatureBody {

}
