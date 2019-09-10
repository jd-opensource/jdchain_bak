package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.AccountHeader;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.MerkleProof;
import com.jd.blockchain.ledger.ParticipantDataQuery;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.utils.Bytes;

public class EmptyLedgerDataset implements LedgerDataQuery {
	
	private static final LedgerAdminDataQuery EMPTY_ADMIN_DATA = new EmptyAdminData();
	
	private static final UserAccountQuery EMPTY_USER_ACCOUNTS = new EmptyUserAccountSet();
	
	private static final DataAccountQuery EMPTY_DATA_ACCOUNTS = new EmptyDataAccountSet();
	
	private static final ContractAccountQuery EMPTY_CONTRACT_ACCOUNTS = new EmptyContractAccountSet();

	private static final ParticipantDataQuery EMPTY_PARTICIPANTS = new EmptyParticipantData();

	@Override
	public LedgerAdminDataQuery getAdminDataset() {
		return EMPTY_ADMIN_DATA;
	}

	@Override
	public UserAccountQuery getUserAccountSet() {
		return EMPTY_USER_ACCOUNTS;
	}

	@Override
	public DataAccountQuery getDataAccountSet() {
		return EMPTY_DATA_ACCOUNTS;
	}

	@Override
	public ContractAccountQuery getContractAccountset() {
		return EMPTY_CONTRACT_ACCOUNTS;
	}


	private static class EmptyAdminData implements LedgerAdminDataQuery{
		

		@Override
		public LedgerAdminInfo getAdminInfo() {
			return null;
		}

		@Override
		public ParticipantDataQuery getParticipantDataset() {
			return EMPTY_PARTICIPANTS;
		}
		
	}
	
	private static class EmptyParticipantData implements ParticipantDataQuery{

		@Override
		public HashDigest getRootHash() {
			return null;
		}

		@Override
		public MerkleProof getProof(Bytes key) {
			return null;
		}

		@Override
		public long getParticipantCount() {
			return 0;
		}

		@Override
		public boolean contains(Bytes address) {
			return false;
		}

		@Override
		public ParticipantNode getParticipant(Bytes address) {
			return null;
		}

		@Override
		public ParticipantNode[] getParticipants() {
			return null;
		}
		
	}
	
	private static class EmptyUserAccountSet implements UserAccountQuery{

		@Override
		public AccountHeader[] getAccounts(int fromIndex, int count) {
			return null;
		}

		@Override
		public long getTotalCount() {
			return 0;
		}

		@Override
		public HashDigest getRootHash() {
			return null;
		}

		@Override
		public MerkleProof getProof(Bytes key) {
			return null;
		}

		@Override
		public UserAccount getUser(String address) {
			return null;
		}

		@Override
		public UserAccount getUser(Bytes address) {
			return null;
		}

		@Override
		public boolean contains(Bytes address) {
			return false;
		}

		@Override
		public UserAccount getUser(Bytes address, long version) {
			return null;
		}
		
		
	}
	
	private static class EmptyDataAccountSet implements DataAccountQuery{

		@Override
		public AccountHeader[] getAccounts(int fromIndex, int count) {
			return null;
		}

		@Override
		public HashDigest getRootHash() {
			return null;
		}

		@Override
		public long getTotalCount() {
			return 0;
		}

		@Override
		public MerkleProof getProof(Bytes address) {
			return null;
		}

		@Override
		public DataAccount getDataAccount(Bytes address) {
			return null;
		}

		@Override
		public DataAccount getDataAccount(Bytes address, long version) {
			return null;
		}
		
	}

	private static class EmptyContractAccountSet implements ContractAccountQuery{

		@Override
		public AccountHeader[] getAccounts(int fromIndex, int count) {
			return null;
		}

		@Override
		public HashDigest getRootHash() {
			return null;
		}

		@Override
		public long getTotalCount() {
			return 0;
		}

		@Override
		public MerkleProof getProof(Bytes address) {
			return null;
		}

		@Override
		public boolean contains(Bytes address) {
			return false;
		}

		@Override
		public ContractAccount getContract(Bytes address) {
			return null;
		}

		@Override
		public ContractAccount getContract(Bytes address, long version) {
			return null;
		}
	}
}
