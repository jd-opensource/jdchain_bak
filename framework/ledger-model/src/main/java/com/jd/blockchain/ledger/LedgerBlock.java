package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.HashDigest;

@DataContract(code = DataCodes.BLOCK)
public interface LedgerBlock extends BlockBody {

	/**
	 * 区块哈希；
	 * 
	 * <br>
	 * 这是对 {@link BlockBody} 的哈希；<br>
	 * 对于创世区块而言，区块哈希是 {@link BlockBody#getPreviousHash()} 和
	 * {@link BlockBody#getLedgerHash()} 属性均为 null 的情况下进行哈希计算得到的结果；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.BYTES)
	HashDigest getHash();

}
