package com.jd.blockchain.sdk.proxy;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainEventHandle;
import com.jd.blockchain.sdk.BlockchainEventListener;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.converters.ClientResolveUtil;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.TransactionService;
import com.jd.blockchain.transaction.TxTemplate;

public abstract class BlockchainServiceProxy implements BlockchainService {


	protected abstract TransactionService getTransactionService(HashDigest ledgerHash);

	protected abstract BlockchainQueryService getQueryService(HashDigest ledgerHash);

	@Override
	public TransactionTemplate newTransaction(HashDigest ledgerHash) {
		return new TxTemplate(ledgerHash, getTransactionService(ledgerHash));
	}

	@Override
	public BlockchainEventHandle addBlockchainEventListener(int filteredEventTypes, String filteredTxHash,
			String filteredAccountAddress, BlockchainEventListener listener) {
		throw new IllegalStateException("Not implemented!");
	}

	@Override
	public LedgerInfo getLedger(HashDigest ledgerHash) {
		return getQueryService(ledgerHash).getLedger(ledgerHash);
	}

    @Override
    public ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash) {
        return getQueryService(ledgerHash).getConsensusParticipants(ledgerHash);
    }

	@Override
	public LedgerMetadata getLedgerMetadata(HashDigest ledgerHash) {
		return getQueryService(ledgerHash).getLedgerMetadata(ledgerHash);
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, long height) {
		return getQueryService(ledgerHash).getBlock(ledgerHash, height);
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, HashDigest blockHash) {
		return getQueryService(ledgerHash).getBlock(ledgerHash, blockHash);
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, long height) {
		return getQueryService(ledgerHash).getTransactionCount(ledgerHash, height);
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, HashDigest blockHash) {
		return getQueryService(ledgerHash).getTransactionCount(ledgerHash, blockHash);
	}

    @Override
    public long getTransactionTotalCount(HashDigest ledgerHash) {
        return getQueryService(ledgerHash).getTransactionTotalCount(ledgerHash);
    }

    @Override
    public long getDataAccountCount(HashDigest ledgerHash, long height) {
        return getQueryService(ledgerHash).getDataAccountCount(ledgerHash, height);
    }

    @Override
    public long getDataAccountCount(HashDigest ledgerHash, HashDigest blockHash) {
        return getQueryService(ledgerHash).getDataAccountCount(ledgerHash, blockHash);
    }

    @Override
    public long getDataAccountTotalCount(HashDigest ledgerHash) {
        return getQueryService(ledgerHash).getDataAccountTotalCount(ledgerHash);
    }

    @Override
    public long getUserCount(HashDigest ledgerHash, long height) {
        return getQueryService(ledgerHash).getUserCount(ledgerHash, height);
    }

    @Override
    public long getUserCount(HashDigest ledgerHash, HashDigest blockHash) {
        return getQueryService(ledgerHash).getUserCount(ledgerHash, blockHash);
    }

    @Override
    public long getUserTotalCount(HashDigest ledgerHash) {
        return getQueryService(ledgerHash).getUserTotalCount(ledgerHash);
    }

    @Override
    public long getContractCount(HashDigest ledgerHash, long height) {
        return getQueryService(ledgerHash).getContractCount(ledgerHash, height);
    }

    @Override
    public long getContractCount(HashDigest ledgerHash, HashDigest blockHash) {
        return getQueryService(ledgerHash).getContractCount(ledgerHash, blockHash);
    }

    @Override
    public long getContractTotalCount(HashDigest ledgerHash) {
        return getQueryService(ledgerHash).getContractTotalCount(ledgerHash);
    }

    @Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, long height, int fromIndex, int count) {
		return getQueryService(ledgerHash).getTransactions(ledgerHash, height, fromIndex, count);
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, HashDigest blockHash, int fromIndex, int count) {
		return getQueryService(ledgerHash).getTransactions(ledgerHash, blockHash, fromIndex, count);
	}

	@Override
	public LedgerTransaction getTransactionByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		return getQueryService(ledgerHash).getTransactionByContentHash(ledgerHash, contentHash);
	}

	@Override
	public TransactionState getTransactionStateByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		return getQueryService(ledgerHash).getTransactionStateByContentHash(ledgerHash, contentHash);
	}

	@Override
	public UserInfo getUser(HashDigest ledgerHash, String address) {
		return getQueryService(ledgerHash).getUser(ledgerHash, address);
	}

	@Override
	public AccountHeader getDataAccount(HashDigest ledgerHash, String address) {
		return getQueryService(ledgerHash).getDataAccount(ledgerHash, address);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, String... keys) {
		KVDataEntry[] kvDataEntries = getQueryService(ledgerHash).getDataEntries(ledgerHash, address, keys);
		return ClientResolveUtil.read(kvDataEntries);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, KVInfoVO kvInfoVO) {
		KVDataEntry[] kvDataEntries = getQueryService(ledgerHash).getDataEntries(ledgerHash, address, kvInfoVO);
		return ClientResolveUtil.read(kvDataEntries);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, int fromIndex, int count) {
		KVDataEntry[] kvDataEntries = getQueryService(ledgerHash).getDataEntries(ledgerHash, address, fromIndex, count);
		return ClientResolveUtil.read(kvDataEntries);
	}

	@Override
	public long getDataEntriesTotalCount(HashDigest ledgerHash, String address) {
		return getQueryService(ledgerHash).getDataEntriesTotalCount(ledgerHash, address);
	}

	@Override
	public ContractInfo getContract(HashDigest ledgerHash, String address) {
		return getQueryService(ledgerHash).getContract(ledgerHash, address);
	}

	@Override
	public AccountHeader[] getUsers(HashDigest ledgerHash, int fromIndex, int count) {
		return getQueryService(ledgerHash).getUsers(ledgerHash, fromIndex, count);
	}

	@Override
	public AccountHeader[] getDataAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		return getQueryService(ledgerHash).getDataAccounts(ledgerHash, fromIndex, count);
	}

	@Override
	public AccountHeader[] getContractAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		return getQueryService(ledgerHash).getContractAccounts(ledgerHash, fromIndex, count);
	}
}
