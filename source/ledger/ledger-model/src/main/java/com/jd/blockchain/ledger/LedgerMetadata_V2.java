package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

/**
 * {@link LedgerMetadata_V2} 是 {@link LedgerMetadata} 的升级版本，新增加了
 * {@link #getPrivilegeHash()} 属性；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.METADATA, name = "LEDGER-METADATA-V2")
public interface LedgerMetadata_V2 extends LedgerMetadata {

	/**
	 * 加入新的版本；
	 * 
	 * @return
	 */
	@DataField(order = 4, primitiveType = PrimitiveType.BYTES)
	HashDigest getPrivilegeHash();

}
