package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;

/**
 * 账本的事务；
 * 
 * TODO: refactor: replace {@link Transaction} to {@link LedgerTransaction}
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code=TypeCodes.TX_LEDGER)
public interface LedgerTransaction extends Transaction, LedgerDataSnapshot {
	
}
