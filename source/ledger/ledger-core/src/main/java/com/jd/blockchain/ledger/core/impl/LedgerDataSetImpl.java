package com.jd.blockchain.ledger.core.impl;

import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.utils.Transactional;

public class LedgerDataSetImpl implements LedgerDataSet, Transactional {

	private LedgerAdminAccount adminAccount;

	private UserAccountSet userAccountSet;

	private DataAccountSet dataAccountSet;

	private ContractAccountSet contractAccountSet;
	
	private boolean readonly;


	/**
	 * Create new block;
	 * @param adminAccount
	 * @param userAccountSet
	 * @param dataAccountSet
	 * @param contractAccountSet
	 * @param readonly
	 */
	public LedgerDataSetImpl(LedgerAdminAccount adminAccount,
			UserAccountSet userAccountSet, DataAccountSet dataAccountSet, ContractAccountSet contractAccountSet,
			boolean readonly) {
		this.adminAccount = adminAccount;
		this.userAccountSet = userAccountSet;
		this.dataAccountSet = dataAccountSet;
		this.contractAccountSet = contractAccountSet;

		this.readonly = readonly;
	}

	@Override
	public LedgerAdminAccount getAdminAccount() {
		return adminAccount;
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
	public ContractAccountSet getContractAccountSet() {
		return contractAccountSet;
	}

	@Override
	public boolean isUpdated() {
		return adminAccount.isUpdated() || userAccountSet.isUpdated() || dataAccountSet.isUpdated()
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

		adminAccount.commit();
		userAccountSet.commit();
		dataAccountSet.commit();
		contractAccountSet.commit();
	}

	@Override
	public void cancel() {
		adminAccount.cancel();
		userAccountSet.cancel();
		dataAccountSet.cancel();
		contractAccountSet.cancel();
	}

	@Override
	public boolean isReadonly() {
		return readonly;
	}

}