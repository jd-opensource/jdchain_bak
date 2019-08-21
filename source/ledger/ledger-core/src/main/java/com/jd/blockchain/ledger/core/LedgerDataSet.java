package com.jd.blockchain.ledger.core;

import com.jd.blockchain.ledger.LedgerAdminInfo;

/**
 * {@link LedgerDataSet} 表示账本在某一个区块上的数据集合；
 * 
 * @author huanghaiquan
 *
 */
public interface LedgerDataSet{
	
	boolean isReadonly();

	LedgerAdminInfo getAdminAccount();

	UserAccountSet getUserAccountSet();

	DataAccountSet getDataAccountSet();

	ContractAccountSet getContractAccountSet();

}
