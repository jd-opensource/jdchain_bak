package com.jd.blockchain.ledger.core;

import com.jd.blockchain.utils.Transactional;

public class LedgerDataset implements LedgerDataQuery, Transactional {

	private LedgerAdminDataset adminDataset;

	private UserAccountSet userAccountSet;

	private DataAccountSet dataAccountSet;

	private ContractAccountSet contractAccountSet;

	private boolean readonly;

	/**
	 * Create new block;
	 * 
	 * @param adminAccount
	 * @param userAccountSet
	 * @param dataAccountSet
	 * @param contractAccountSet
	 * @param readonly
	 */
	public LedgerDataset(LedgerAdminDataset adminAccount, UserAccountSet userAccountSet,
			DataAccountSet dataAccountSet, ContractAccountSet contractAccountSet, boolean readonly) {
		this.adminDataset = adminAccount;
		this.userAccountSet = userAccountSet;
		this.dataAccountSet = dataAccountSet;
		this.contractAccountSet = contractAccountSet;

		this.readonly = readonly;
	}

	@Override
	public LedgerAdminDataset getAdminDataset() {
		return adminDataset;
	}

	@Override
	public UserAccountSet getUserAccountSet() {
		return userAccountSet;
	}

	@Override
	public DataAccountSet getDataAccountSet() {
		return dataAccountSet;
	}

	@Override
	public ContractAccountSet getContractAccountset() {
		return contractAccountSet;
	}

	@Override
	public boolean isUpdated() {
		return adminDataset.isUpdated() || userAccountSet.isUpdated() || dataAccountSet.isUpdated()
				|| contractAccountSet.isUpdated();
	}

	@Override
	public void commit() {
		if (readonly) {
			throw new IllegalStateException("Readonly ledger dataset which cann't been committed!");
		}
		if (!isUpdated()) {
			return;
		}

		adminDataset.commit();
		userAccountSet.commit();
		dataAccountSet.commit();
		contractAccountSet.commit();
	}

	@Override
	public void cancel() {
		adminDataset.cancel();
		userAccountSet.cancel();
		dataAccountSet.cancel();
		contractAccountSet.cancel();
	}

	public boolean isReadonly() {
		return readonly;
	}

	void setReadonly() {
		this.readonly = true;
		this.adminDataset.setReadonly();
		this.userAccountSet.setReadonly();
		this.dataAccountSet.setReadonly();
		this.contractAccountSet.setReadonly();
	}

}