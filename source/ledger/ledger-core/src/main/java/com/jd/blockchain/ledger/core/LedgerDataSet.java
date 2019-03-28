package com.jd.blockchain.ledger.core;

/**
 * {@link LedgerDataSet} 表示账本在某一个区块上的数据集合；
 * 
 * @author huanghaiquan
 *
 */
public interface LedgerDataSet{
	
	boolean isReadonly();

	LedgerAdminAccount getAdminAccount();

	UserAccountSet getUserAccountSet();

	DataAccountSet getDataAccountSet();

	ContractAccountSet getContractAccountSet();

}
