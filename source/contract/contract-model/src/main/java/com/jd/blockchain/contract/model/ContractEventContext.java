package com.jd.blockchain.contract.model;

import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.TransactionRequest;

import java.util.Set;


public interface ContractEventContext  {

	/**
	 * current ledger hash；
	 *
	 * @return
	 */
	HashDigest getCurrentLedgerHash();

	/**
	 * Transaction requests for execution of contract events;
	 *
	 * @return
	 */
	TransactionRequest getTransactionRequest();

	/**
	 * Collection of signatories of the transaction;
	 *
	 * @return
	 */
	Set<BlockchainIdentity> getTxSigners();

	/**
	 * event name;
	 *
	 * @return
	 */
	String getEvent();

	/**
	 * param list；
	 *
	 * @return
	 */
	byte[] getArgs();

	/**
	 * ledger operation context；
	 *
	 * @return
	 */
	LedgerContext getLedger();

	/**
	 * Collection of Contracts Owners；
	 *
	 * <br>
	 * The owner of the contract is the signer when the contract is deployed.
	 *
	 * @return
	 */
	Set<BlockchainIdentity> getContracOwners();

}
