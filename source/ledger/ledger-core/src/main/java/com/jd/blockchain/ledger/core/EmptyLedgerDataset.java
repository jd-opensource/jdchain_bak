package com.jd.blockchain.ledger.core;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.LedgerAdminSettings;
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
		public LedgerAdminSettings getAdminInfo() {
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
	
	private static class EmptyUserAccountSet extends EmptyAccountSet<UserAccount> implements UserAccountQuery{

	}
	
	private static class EmptyDataAccountSet extends EmptyAccountSet<DataAccount> implements DataAccountQuery{

	}
	
	private static class EmptyContractAccountSet extends EmptyAccountSet<ContractAccount> implements ContractAccountQuery{
		
	}

	
}
