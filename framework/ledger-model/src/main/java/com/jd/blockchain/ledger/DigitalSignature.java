package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.consts.DataCodes;

/**
 * 数字签名；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= DataCodes.DIGITALSIGNATURE)
public interface DigitalSignature extends DigitalSignatureBody {

}
