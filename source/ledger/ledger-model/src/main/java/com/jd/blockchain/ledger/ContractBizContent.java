package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

/**
 * build complex param Object;
 */
@DataContract(code = DataCodes.CONTRACT_BIZ_CONTENT)
public interface ContractBizContent {

	/**
	 * 执行交易的账本地址；
	 * 注：除了账本的创世交易之外，任何交易的账本地址都不允许为 null;
	 *
	 * @return
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	HashDigest getLedgerHash();

	/**
	 * 操作列表；
	 * @return
	 */
	@DataField(order = 2, list = true, refContract = true, genericContract = true)
	Operation[] getOperations();
}
