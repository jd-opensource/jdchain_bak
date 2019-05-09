package com.jd.blockchain.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.utils.Bytes;

/**
 * @author huanghaiquan
 *
 */
public class BlockchainOperationFactory implements ClientOperator, LedgerInitOperator {

	private static final LedgerInitOperationBuilderImpl LEDGER_INIT_OP_BUILDER = new LedgerInitOperationBuilderImpl();

	private static final UserRegisterOperationBuilderImpl USER_REG_OP_BUILDER = new UserRegisterOperationBuilderImpl();

	private static final DataAccountRegisterOperationBuilderImpl DATA_ACC_REG_OP_BUILDER = new DataAccountRegisterOperationBuilderImpl();

	private static final ContractCodeDeployOperationBuilderImpl CONTRACT_CODE_DEPLOY_OP_BUILDER = new ContractCodeDeployOperationBuilderImpl();

	private static final ContractEventSendOperationBuilderImpl CONTRACT_EVENT_SEND_OP_BUILDER = new ContractEventSendOperationBuilderImpl();

	
	private LedgerInitOperationBuilder ledgerInitOpBuilder = new LedgerInitOperationBuilderFilter();

	private UserRegisterOperationBuilder userRegOpBuilder = new UserRegisterOperationBuilderFilter();

	private DataAccountRegisterOperationBuilder dataAccRegOpBuilder = new DataAccountRegisterOperationBuilderFilter();

	private ContractCodeDeployOperationBuilder contractCodeDeployOpBuilder = new ContractCodeDeployOperationBuilderFilter();

	private ContractEventSendOperationBuilder contractEventSendOpBuilder = new ContractEventSendOperationBuilderFilter();

	private List<Operation> operationList = new ArrayList<>();

	@Override
	public LedgerInitOperationBuilder ledgers() {
		return ledgerInitOpBuilder;
	}

	@Override
	public UserRegisterOperationBuilder users() {
		return userRegOpBuilder;
	}

	@Override
	public DataAccountRegisterOperationBuilder dataAccounts() {
		return dataAccRegOpBuilder;
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(String accountAddress) {
		return new DataAccountKVSetOperationBuilderFilter(Bytes.fromBase58(accountAddress));
	}

	@Override
	public DataAccountKVSetOperationBuilder dataAccount(Bytes accountAddress) {
		return new DataAccountKVSetOperationBuilderFilter(accountAddress);
	}

	@Override
	public ContractCodeDeployOperationBuilder contracts() {
		return contractCodeDeployOpBuilder;
	}

	@Override
	public ContractEventSendOperationBuilder contractEvents() {
		return contractEventSendOpBuilder;
	}
	
	@Override
	public <T> T contract(String address, Class<T> contractIntf) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Operation> getOperations() {
		// TODO: 合并操作列表中可能的重复操作；
		return operationList;
	}

	public void clear() {
		operationList.clear();
	}

	// --------------------------------- 内部类型 -----------------------------------

	private class LedgerInitOperationBuilderFilter implements LedgerInitOperationBuilder {

		@Override
		public LedgerInitOperation create(LedgerInitSetting initSetting) {
			LedgerInitOperation op = LEDGER_INIT_OP_BUILDER.create(initSetting);
			operationList.add(op);
			return op;
		}

	}

	private class UserRegisterOperationBuilderFilter implements UserRegisterOperationBuilder {

		@Override
		public UserRegisterOperation register(BlockchainIdentity userID) {
			UserRegisterOperation op = USER_REG_OP_BUILDER.register(userID);
			operationList.add(op);
			return op;
		}

	}

	private class DataAccountRegisterOperationBuilderFilter implements DataAccountRegisterOperationBuilder {

		@Override
		public DataAccountRegisterOperation register(BlockchainIdentity accountID) {
			DataAccountRegisterOperation op = DATA_ACC_REG_OP_BUILDER.register(accountID);
			operationList.add(op);
			return op;
		}

	}

	private class DataAccountKVSetOperationBuilderFilter implements DataAccountKVSetOperationBuilder {

		private DataAccountKVSetOperationBuilder innerBuilder;

		private DataAccountKVSetOperation op;

		public DataAccountKVSetOperationBuilderFilter(Bytes accountAddress) {
			innerBuilder = new DataAccountKVSetOperationBuilderImpl(accountAddress);
		}

		@Override
		public DataAccountKVSetOperation getOperation() {
			return innerBuilder.getOperation();
		}

		@Override
		public DataAccountKVSetOperationBuilder set(String key, byte[] value, long expVersion) {
			innerBuilder.set(key, value, expVersion);
			if (op == null) {
				op = innerBuilder.getOperation();
				operationList.add(op);
			}
			return this;
		}
		@Override
		public DataAccountKVSetOperationBuilder set(String key, String value, long expVersion) {
			innerBuilder.set(key, value, expVersion);
			if (op == null) {
				op = innerBuilder.getOperation();
				operationList.add(op);
			}
			return this;
		}
		@Override
		public DataAccountKVSetOperationBuilder set(String key, long value, long expVersion) {
			innerBuilder.set(key, value, expVersion);
			if (op == null) {
				op = innerBuilder.getOperation();
				operationList.add(op);
			}
			return this;
		}
		@Override
		public DataAccountKVSetOperationBuilder set(String key, Bytes value, long expVersion) {
			innerBuilder.set(key, value, expVersion);
			if (op == null) {
				op = innerBuilder.getOperation();
				operationList.add(op);
			}
			return this;
		}

	}

	private class ContractCodeDeployOperationBuilderFilter implements ContractCodeDeployOperationBuilder {

		@Override
		public ContractCodeDeployOperation deploy(BlockchainIdentity id, byte[] chainCode) {
			ContractCodeDeployOperation op = CONTRACT_CODE_DEPLOY_OP_BUILDER.deploy(id, chainCode);
			operationList.add(op);
			return op;
		}

	}

	private class ContractEventSendOperationBuilderFilter implements ContractEventSendOperationBuilder {

		@Override
		public ContractEventSendOperation send(String address, String event, byte[] args) {
			ContractEventSendOperation op = CONTRACT_EVENT_SEND_OP_BUILDER.send(address, event, args);
			operationList.add(op);
			return op;
		}

		@Override
		public ContractEventSendOperation send(Bytes address, String event, byte[] args) {
			ContractEventSendOperation op = CONTRACT_EVENT_SEND_OP_BUILDER.send(address, event, args);
			operationList.add(op);
			return op;
		}

	}

}
