package com.jd.blockchain.ledger.core.impl.handles;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.contract.LedgerContext;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;
import com.jd.blockchain.transaction.BlockchainQueryService;
import com.jd.blockchain.transaction.DataAccountKVSetOperationBuilder;
import com.jd.blockchain.transaction.DataAccountRegisterOperationBuilder;
import com.jd.blockchain.transaction.DataAccountRegisterOperationBuilderImpl;
import com.jd.blockchain.transaction.KVData;
import com.jd.blockchain.transaction.UserRegisterOperationBuilder;
import com.jd.blockchain.transaction.UserRegisterOperationBuilderImpl;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.BytesUtils;

public class ContractLedgerContext implements LedgerContext {

	private BlockchainQueryService innerQueryService;

	private OperationHandleContext opHandleContext;

	private List<Operation> generatedOpList = new ArrayList<>();

	public ContractLedgerContext(BlockchainQueryService innerQueryService, OperationHandleContext opHandleContext) {
		this.innerQueryService = innerQueryService;
		this.opHandleContext = opHandleContext;
	}

	@Override
	public HashDigest[] getLedgerHashs() {
		return innerQueryService.getLedgerHashs();
	}

	@Override
	public LedgerInfo getLedger(HashDigest ledgerHash) {
		return innerQueryService.getLedger(ledgerHash);
	}

	@Override
	public ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash) {
		return innerQueryService.getConsensusParticipants(ledgerHash);
	}

	@Override
	public LedgerMetadata getLedgerMetadata(HashDigest ledgerHash) {
		return innerQueryService.getLedgerMetadata(ledgerHash);
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, long height) {
		return innerQueryService.getBlock(ledgerHash, height);
	}

	@Override
	public LedgerBlock getBlock(HashDigest ledgerHash, HashDigest blockHash) {
		return innerQueryService.getBlock(ledgerHash, blockHash);
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, long height) {
		return innerQueryService.getTransactionCount(ledgerHash, height);
	}

	@Override
	public long getTransactionCount(HashDigest ledgerHash, HashDigest blockHash) {
		return innerQueryService.getTransactionCount(ledgerHash, blockHash);
	}

	@Override
	public long getTransactionTotalCount(HashDigest ledgerHash) {
		return innerQueryService.getTransactionTotalCount(ledgerHash);
	}

	@Override
	public long getDataAccountCount(HashDigest ledgerHash, long height) {
		return innerQueryService.getDataAccountCount(ledgerHash, height);
	}

	@Override
	public long getDataAccountCount(HashDigest ledgerHash, HashDigest blockHash) {
		return innerQueryService.getDataAccountCount(ledgerHash, blockHash);
	}

	@Override
	public long getDataAccountTotalCount(HashDigest ledgerHash) {
		return innerQueryService.getDataAccountTotalCount(ledgerHash);
	}

	@Override
	public long getUserCount(HashDigest ledgerHash, long height) {
		return innerQueryService.getUserCount(ledgerHash, height);
	}

	@Override
	public long getUserCount(HashDigest ledgerHash, HashDigest blockHash) {
		return innerQueryService.getUserCount(ledgerHash, blockHash);
	}

	@Override
	public long getUserTotalCount(HashDigest ledgerHash) {
		return innerQueryService.getUserTotalCount(ledgerHash);
	}

	@Override
	public long getContractCount(HashDigest ledgerHash, long height) {
		return innerQueryService.getContractCount(ledgerHash, height);
	}

	@Override
	public long getContractCount(HashDigest ledgerHash, HashDigest blockHash) {
		return innerQueryService.getContractCount(ledgerHash, blockHash);
	}

	@Override
	public long getContractTotalCount(HashDigest ledgerHash) {
		return innerQueryService.getContractTotalCount(ledgerHash);
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, long height, int fromIndex, int count) {
		return innerQueryService.getTransactions(ledgerHash, height, fromIndex, count);
	}

	@Override
	public LedgerTransaction[] getTransactions(HashDigest ledgerHash, HashDigest blockHash, int fromIndex, int count) {
		return innerQueryService.getTransactions(ledgerHash, blockHash, fromIndex, count);
	}

	@Override
	public LedgerTransaction getTransactionByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		return innerQueryService.getTransactionByContentHash(ledgerHash, contentHash);
	}

	@Override
	public TransactionState getTransactionStateByContentHash(HashDigest ledgerHash, HashDigest contentHash) {
		return innerQueryService.getTransactionStateByContentHash(ledgerHash, contentHash);
	}

	@Override
	public UserInfo getUser(HashDigest ledgerHash, String address) {
		return innerQueryService.getUser(ledgerHash, address);
	}

	@Override
	public AccountHeader getDataAccount(HashDigest ledgerHash, String address) {
		return innerQueryService.getDataAccount(ledgerHash, address);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, String... keys) {
		return innerQueryService.getDataEntries(ledgerHash, address, keys);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, String[] keys, String[] versions) {
		return innerQueryService.getDataEntries(ledgerHash, address, keys, versions);
	}

	@Override
	public KVDataEntry[] getDataEntries(HashDigest ledgerHash, String address, int fromIndex, int count) {
		return innerQueryService.getDataEntries(ledgerHash, address, fromIndex, count);
	}

	@Override
	public long getDataEntriesTotalCount(HashDigest ledgerHash, String address) {
		return innerQueryService.getDataEntriesTotalCount(ledgerHash, address);
	}

	@Override
	public AccountHeader getContract(HashDigest ledgerHash, String address) {
		return innerQueryService.getContract(ledgerHash, address);
	}

	// ---------------------------user()----------------------------

	@Override
	public AccountHeader[] getUsers(HashDigest ledgerHash, int fromIndex, int count) {
		return innerQueryService.getUsers(ledgerHash, fromIndex, count);
	}

	@Override
	public AccountHeader[] getDataAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		return innerQueryService.getDataAccounts(ledgerHash, fromIndex, count);
	}

	@Override
	public AccountHeader[] getContractAccounts(HashDigest ledgerHash, int fromIndex, int count) {
		return innerQueryService.getContractAccounts(ledgerHash, fromIndex, count);
	}

	@Override
	public UserRegisterOperationBuilder users() {
		return new UserRegisterOperationBuilder1();
	}

	@Override
	public DataAccountRegisterOperationBuilder dataAccounts() {
		return new DataAccountRegisterOperationBuilder1();
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(String accountAddress) {
		return new DataAccountKVSetOperationExecBuilder(Bytes.fromBase58(accountAddress));
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(Bytes accountAddress) {
		return new DataAccountKVSetOperationExecBuilder(accountAddress);
	}

	// ========end=============

	private class DataAccountRegisterOperationBuilder1 implements DataAccountRegisterOperationBuilder {
		@Override
		public DataAccountRegisterOperation register(BlockchainIdentity accountID) {
			final DataAccountRegisterOperationBuilderImpl DATA_ACC_REG_OP_BUILDER = new DataAccountRegisterOperationBuilderImpl();
			DataAccountRegisterOperation op = DATA_ACC_REG_OP_BUILDER.register(accountID);
			generatedOpList.add(op);
			opHandleContext.handle(op);
			return op;
		}
	}

	private class UserRegisterOperationBuilder1 implements UserRegisterOperationBuilder {
		private final UserRegisterOperationBuilderImpl USER_REG_OP_BUILDER = new UserRegisterOperationBuilderImpl();

		@Override
		public UserRegisterOperation register(BlockchainIdentity userID) {
			UserRegisterOperation op = USER_REG_OP_BUILDER.register(userID);
			generatedOpList.add(op);
			opHandleContext.handle(op);
			return op;
		}
	}

	// --------------------------------

	private class DataAccountKVSetOperationExecBuilder implements DataAccountKVSetOperationBuilder {

		private Bytes accountAddress;

		private SingleKVSetOpTemplate op;

		public DataAccountKVSetOperationExecBuilder(Bytes accountAddress) {
			this.accountAddress = accountAddress;
		}

		public boolean isJson(String str) {
			boolean result = false;
			try {
				Object obj=JSON.parse(str);
				result = true;
			} catch (Exception e) {
				result=false;
			}
			return result;
		}

		@Override
		public DataAccountKVSetOperation getOperation() {
			return op;
		}

		@Override
		public DataAccountKVSetOperationBuilder set(String key, byte[] value, long expVersion) {
			BytesValue bytesValue = new BytesValueEntry(BytesValueType.BYTES, value);
			this.op = new SingleKVSetOpTemplate(key, bytesValue, expVersion);
			generatedOpList.add(op);
			opHandleContext.handle(op);
			return this;
		}
		@Override
		public DataAccountKVSetOperationBuilder set(String key, String value, long expVersion) {
			BytesValue bytesValue;
			if (isJson(value)) {
				bytesValue = new BytesValueEntry(BytesValueType.JSON, value.getBytes());
			}
			else {
				bytesValue = new BytesValueEntry(BytesValueType.TEXT, value.getBytes());
			}
			this.op = new SingleKVSetOpTemplate(key, bytesValue, expVersion);
			generatedOpList.add(op);
			opHandleContext.handle(op);
			return this;
		}
		@Override
		public DataAccountKVSetOperationBuilder set(String key, Bytes value, long expVersion) {
			BytesValue bytesValue = new BytesValueEntry(BytesValueType.BYTES, value.toBytes());
			this.op = new SingleKVSetOpTemplate(key, bytesValue, expVersion);
			generatedOpList.add(op);
			opHandleContext.handle(op);
			return this;
		}
		@Override
		public DataAccountKVSetOperationBuilder set(String key, long value, long expVersion) {
			BytesValue bytesValue = new BytesValueEntry(BytesValueType.INT64, BytesUtils.toBytes(value));
			this.op = new SingleKVSetOpTemplate(key, bytesValue, expVersion);
			generatedOpList.add(op);
			opHandleContext.handle(op);
			return this;
		}

		/**
		 * 单个KV写入操作；
		 * 
		 * @author huanghaiquan
		 *
		 */
		private class SingleKVSetOpTemplate implements DataAccountKVSetOperation {

			private KVWriteEntry[] writeset = new KVWriteEntry[1];

			private SingleKVSetOpTemplate(String key, BytesValue value, long expVersion) {
				writeset[0] = new KVData(key, value, expVersion);
			}

			@Override
			public Bytes getAccountAddress() {
				return accountAddress;
			}

			@Override
			public KVWriteEntry[] getWriteSet() {
				return writeset;
			}

		}
	}
}
