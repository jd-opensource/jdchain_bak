package com.jd.blockchain.ledger.core;

import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.ContractInfo;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.ledger.TypedKVData;
import com.jd.blockchain.ledger.KVDataVO;
import com.jd.blockchain.ledger.KVInfoVO;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.LedgerInfo;
import com.jd.blockchain.ledger.LedgerMetadata;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.TransactionState;
import com.jd.blockchain.ledger.UserInfo;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.utils.ArrayUtils;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.DataEntry;
import com.jd.blockchain.utils.DataIterator;
import com.jd.blockchain.utils.QueryUtil;

public class LedgerQueryService implements BlockchainQueryService {

	private static final TypedKVEntry[] EMPTY_ENTRIES = new TypedKVEntry[0];

	private HashDigest[] ledgerHashs;

	private LedgerQuery ledger;

	public LedgerQueryService(LedgerQuery ledger) {
		this.ledger = ledger;
		this.ledgerHashs = new HashDigest[] { ledger.getHash() };
	}

	private void checkLedgerHash(HashDigest ledgerHash) {
		if (!ledgerHashs[0].equals(ledgerHash)) {
			throw new LedgerException("Unsupport cross chain query!");
		}
	}

	@Override
	public HashDigest[] getLedgerHashs() {
		return ledgerHashs;
	}

	@Override
	public LedgerInfo getLedger(HashDigest ledgerHash) {
		checkLedgerHash(ledgerHash);
		LedgerInfo ledgerInfo = new LedgerInfo();
		ledgerInfo.setHash(ledger.getHash());
		ledgerInfo.setLatestBlockHash(ledger.getLatestBlockHash());
		ledgerInfo.setLatestBlockHeight(ledger.getLatestBlockHeight());
		return ledgerInfo;
	}

	@Override
	public LedgerAdminInfo getLedgerAdminInfo(HashDigest ledgerHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		LedgerAdminInfo administration = ledger.getAdminInfo(block);
		return administration;
	}

	@Override
	public ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash) {
		return getLedgerAdminInfo(ledgerHash).getParticipants();
	}

	@Override
	public LedgerMetadata getLedgerMetadata(HashDigest ledgerHash) {
		return getLedgerAdminInfo(ledgerHash).getMetadata();
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, long height) {
		checkLedgerHash(ledgerHash);
		return ledger.getBlock(height);
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, HashDigest blockHash) {
		checkLedgerHash(ledgerHash);
		return ledger.getBlock(blockHash);
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, long height) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getBlock(height);
		TransactionQuery txset = ledger.getTransactionSet(block);
		return txset.getTotalCount();
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, HashDigest blockHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getBlock(blockHash);
		TransactionQuery txset = ledger.getTransactionSet(block);
		return txset.getTotalCount();
	}

	@Override
	public long getTransactionTotalCount(HashDigest ledgerHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		TransactionQuery txset = ledger.getTransactionSet(block);
		return txset.getTotalCount();
	}

	@Override
	public long getDataAccountCount(HashDigest ledgerHash, long height) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getBlock(height);
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		return dataAccountSet.getTotal();
	}

	@Override
	public long getDataAccountCount(HashDigest ledgerHash, HashDigest blockHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getBlock(blockHash);
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		return dataAccountSet.getTotal();
	}

	@Override
	public long getDataAccountTotalCount(HashDigest ledgerHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		return dataAccountSet.getTotal();
	}

	@Override
	public long getUserCount(HashDigest ledgerHash, long height) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getBlock(height);
		UserAccountQuery userAccountSet = ledger.getUserAccountSet(block);
		return userAccountSet.getTotal();
	}

	@Override
	public long getUserCount(HashDigest ledgerHash, HashDigest blockHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getBlock(blockHash);
		UserAccountQuery userAccountSet = ledger.getUserAccountSet(block);
		return userAccountSet.getTotal();
	}

	@Override
	public long getUserTotalCount(HashDigest ledgerHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		UserAccountQuery userAccountSet = ledger.getUserAccountSet(block);
		return userAccountSet.getTotal();
	}

	@Override
	public long getContractCount(HashDigest ledgerHash, long height) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getBlock(height);
		ContractAccountQuery contractAccountSet = ledger.getContractAccountSet(block);
		return contractAccountSet.getTotal();
	}

	@Override
	public long getContractCount(HashDigest ledgerHash, HashDigest blockHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getBlock(blockHash);
		ContractAccountQuery contractAccountSet = ledger.getContractAccountSet(block);
		return contractAccountSet.getTotal();
	}

	@Override
	public long getContractTotalCount(HashDigest ledgerHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		ContractAccountQuery contractAccountSet = ledger.getContractAccountSet(block);
		return contractAccountSet.getTotal();
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, long height, int fromIndex, int count) {
		checkLedgerHash(ledgerHash);
		LedgerBlock ledgerBlock = ledger.getBlock(height);
		TransactionQuery transactionSet = ledger.getTransactionSet(ledgerBlock);
		int lastHeightTxTotalNums = 0;

		if (height > 0) {
			lastHeightTxTotalNums = (int) ledger.getTransactionSet(ledger.getBlock(height - 1)).getTotalCount();
		}

		int currentHeightTxTotalNums = (int) ledger.getTransactionSet(ledger.getBlock(height)).getTotalCount();
		// 取当前高度的增量交易数，在增量交易里进行查找
		int currentHeightTxNums = currentHeightTxTotalNums - lastHeightTxTotalNums;
//
//		if (fromIndex < 0 || fromIndex >= currentHeightTxNums) {
//			fromIndex = 0;
//		}
//		if (count == -1) {
//			fromIndex = 0;
//			count = currentHeightTxNums;
//		}
//		if (count > currentHeightTxNums) {
//			count = currentHeightTxNums - fromIndex;
//		}
		int indexAndCount[] = QueryUtil.calFromIndexAndCount(fromIndex, count, currentHeightTxNums);
		return transactionSet.getTxs(lastHeightTxTotalNums + indexAndCount[0], indexAndCount[1]);
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, HashDigest blockHash, int fromIndex, int count) {
		checkLedgerHash(ledgerHash);
		LedgerBlock ledgerBlock = ledger.getBlock(blockHash);
		long height = ledgerBlock.getHeight();
		TransactionQuery transactionSet = ledger.getTransactionSet(ledgerBlock);
		int lastHeightTxTotalNums = 0;

		if (height > 0) {
			lastHeightTxTotalNums = (int) ledger.getTransactionSet(ledger.getBlock(height - 1)).getTotalCount();
		}

		int currentHeightTxTotalNums = (int) ledger.getTransactionSet(ledger.getBlock(height)).getTotalCount();
		// 取当前块hash的增量交易数，在增量交易里进行查找
		int currentHeightTxNums = currentHeightTxTotalNums - lastHeightTxTotalNums;

//		if (fromIndex < 0 || fromIndex >= currentHeightTxNums) {
//			fromIndex = 0;
//		}
//		if (count == -1) {
//			fromIndex = 0;
//			count = currentHeightTxNums;
//		}
//		if (count > currentHeightTxNums) {
//			count = currentHeightTxNums - fromIndex;
//		}
		int indexAndCount[] = QueryUtil.calFromIndexAndCount(fromIndex, count, currentHeightTxNums);
		return transactionSet.getTxs(lastHeightTxTotalNums + indexAndCount[0], indexAndCount[1]);
	}

	@Override
	public LedgerTransaction getTransactionByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		TransactionQuery txset = ledger.getTransactionSet(block);
		return txset.get(contentHash);
	}

	@Override
	public TransactionState getTransactionStateByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		TransactionQuery txset = ledger.getTransactionSet(block);
		return txset.getState(contentHash);
	}

	@Override
	public UserInfo getUser(HashDigest ledgerHash, String address) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		UserAccountQuery userAccountSet = ledger.getUserAccountSet(block);
		return userAccountSet.getAccount(address);

	}

	@Override
	public BlockchainIdentity getDataAccount(HashDigest ledgerHash, String address) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		return dataAccountSet.getAccount(Bytes.fromBase58(address)).getID();
	}

	@Override
	public TypedKVEntry[] getDataEntries(HashDigest ledgerHash, String address, String... keys) {
		if (keys == null || keys.length == 0) {
			return EMPTY_ENTRIES;
		}
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		DataAccount dataAccount = dataAccountSet.getAccount(Bytes.fromBase58(address));

		
		TypedKVEntry[] entries = new TypedKVEntry[keys.length];
		long ver;
		for (int i = 0; i < entries.length; i++) {
			final String currKey = keys[i];

			ver = dataAccount == null ? -1 : dataAccount.getDataset().getVersion(currKey);

			if (ver < 0) {
				entries[i] = new TypedKVData(currKey, -1, null);
			} else {
				BytesValue value = dataAccount.getDataset().getValue(currKey, ver);
				entries[i] = new TypedKVData(currKey, ver, value);
			}
		}

		return entries;
	}

	public TypedKVEntry[] getDataEntries(HashDigest ledgerHash, String address, KVInfoVO kvInfoVO) {
		// parse kvInfoVO;
		List<String> keyList = new ArrayList<>();
		List<Long> versionList = new ArrayList<>();
		if (kvInfoVO != null) {
			for (KVDataVO kvDataVO : kvInfoVO.getData()) {
				for (Long version : kvDataVO.getVersion()) {
					keyList.add(kvDataVO.getKey());
					versionList.add(version);
				}
			}
		}
		String[] keys = keyList.toArray(new String[keyList.size()]);
		Long[] versions = versionList.toArray(new Long[versionList.size()]);

		if (keys == null || keys.length == 0) {
			return null;
		}
		if (versions == null || versions.length == 0) {
			return null;
		}
		if (keys.length != versions.length) {
			throw new ContractException("keys.length!=versions.length!");
		}

		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		DataAccount dataAccount = dataAccountSet.getAccount(Bytes.fromBase58(address));

		TypedKVEntry[] entries = new TypedKVEntry[keys.length];
		long ver = -1;
		for (int i = 0; i < entries.length; i++) {
//			ver = dataAccount.getDataVersion(Bytes.fromString(keys[i]));
//			dataAccount.getBytes(Bytes.fromString(keys[i]),1);
			ver = versions[i];
			if (ver < 0) {
				entries[i] = new TypedKVData(keys[i], -1, null);
			} else {
				if (dataAccount.getDataset().getDataCount() == 0
						|| dataAccount.getDataset().getValue(keys[i], ver) == null) {
					// is the address is not exist; the result is null;
					entries[i] = new TypedKVData(keys[i], -1, null);
				} else {
					BytesValue value = dataAccount.getDataset().getValue(keys[i], ver);
					entries[i] = new TypedKVData(keys[i], ver, value);
				}
			}
		}

		return entries;
	}

	@Override
	public TypedKVEntry[] getDataEntries(HashDigest ledgerHash, String address, int fromIndex, int count) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		DataAccount dataAccount = dataAccountSet.getAccount(Bytes.fromBase58(address));

//		int pages[] = QueryUtil.calFromIndexAndCount(fromIndex, count, (int) dataAccount.getDataset().getDataCount());
//		return dataAccount.getDataset().getDataEntry(key, version).getDataEntries(pages[0], pages[1]);
		
		DataIterator<String, TypedValue> iterator = dataAccount.getDataset().iterator();
		iterator.skip(fromIndex);
		DataEntry<String, TypedValue>[] dataEntries = iterator.next(count);
		
		TypedKVEntry[] typedKVEntries = ArrayUtils.castTo(dataEntries, TypedKVEntry.class,
				e -> e == null ? null : new TypedKVData(e.getKey(), e.getVersion(), e.getValue()));
		return typedKVEntries;
	}

	@Override
	public long getDataEntriesTotalCount(HashDigest ledgerHash, String address) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		DataAccount dataAccount = dataAccountSet.getAccount(Bytes.fromBase58(address));

		return dataAccount.getDataset().getDataCount();
	}

	@Override
	public ContractInfo getContract(HashDigest ledgerHash, String address) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		ContractAccountQuery contractAccountSet = ledger.getContractAccountSet(block);
		return contractAccountSet.getAccount(Bytes.fromBase58(address));
	}

	@Override
	public BlockchainIdentity[] getUsers(HashDigest ledgerHash, int fromIndex, int count) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		UserAccountQuery userAccountSet = ledger.getUserAccountSet(block);
		int pages[] = QueryUtil.calFromIndexAndCountDescend(fromIndex, count, (int) userAccountSet.getTotal());
		return userAccountSet.getHeaders(pages[0], pages[1]);
	}

	@Override
	public BlockchainIdentity[] getDataAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		DataAccountQuery dataAccountSet = ledger.getDataAccountSet(block);
		int pages[] = QueryUtil.calFromIndexAndCountDescend(fromIndex, count, (int) dataAccountSet.getTotal());
		return dataAccountSet.getHeaders(pages[0], pages[1]);
	}

	@Override
	public BlockchainIdentity[] getContractAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		checkLedgerHash(ledgerHash);
		LedgerBlock block = ledger.getLatestBlock();
		ContractAccountQuery contractAccountSet = ledger.getContractAccountSet(block);
		int pages[] = QueryUtil.calFromIndexAndCountDescend(fromIndex, count, (int) contractAccountSet.getTotal());
		return contractAccountSet.getHeaders(pages[0], pages[1]);
	}

}
