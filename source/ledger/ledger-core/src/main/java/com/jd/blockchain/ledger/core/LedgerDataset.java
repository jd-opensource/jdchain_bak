package com.jd.blockchain.ledger.core;

/**
 * {@link LedgerDataset} 表示账本在某一个区块上的数据集合；
 * 
 * @author huanghaiquan
 *
 */
public interface LedgerDataset{
	
	boolean isReadonly();

	LedgerAdminDataset getAdminDataset();

	UserAccountSet getUserAccountSet();

	DataAccountSet getDataAccountSet();

	ContractAccountSet getContractAccountset();

}
