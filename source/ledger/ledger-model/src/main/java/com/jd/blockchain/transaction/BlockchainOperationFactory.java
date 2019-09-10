package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.Bytes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * @author huanghaiquan
 *
 */
public class BlockchainOperationFactory implements ClientOperator, LedgerInitOperator {
	
	private static final SecurityOperationBuilderImpl SECURITY_OP_BUILDER = new SecurityOperationBuilderImpl();

	private static final LedgerInitOperationBuilderImpl LEDGER_INIT_OP_BUILDER = new LedgerInitOperationBuilderImpl();

	private static final UserRegisterOperationBuilderImpl USER_REG_OP_BUILDER = new UserRegisterOperationBuilderImpl();

	private static final DataAccountRegisterOperationBuilderImpl DATA_ACC_REG_OP_BUILDER = new DataAccountRegisterOperationBuilderImpl();

	private static final ContractCodeDeployOperationBuilderImpl CONTRACT_CODE_DEPLOY_OP_BUILDER = new ContractCodeDeployOperationBuilderImpl();

//	private static final ContractEventSendOperationBuilderImpl CONTRACT_EVENT_SEND_OP_BUILDER = new ContractEventSendOperationBuilderImpl();
	
	private SecurityOperationBuilderFilter securityOpBuilder = new SecurityOperationBuilderFilter();

	private static final ParticipantRegisterOperationBuilderImpl PARTICIPANT_REG_OP_BUILDER = new ParticipantRegisterOperationBuilderImpl();

	private static final ParticipantStateUpdateOperationBuilderImpl PARTICIPANT_STATE_UPDATE_OP_BUILDER = new ParticipantStateUpdateOperationBuilderImpl();

	private LedgerInitOperationBuilder ledgerInitOpBuilder = new LedgerInitOperationBuilderFilter();

	private UserRegisterOperationBuilder userRegOpBuilder = new UserRegisterOperationBuilderFilter();

	private DataAccountRegisterOperationBuilder dataAccRegOpBuilder = new DataAccountRegisterOperationBuilderFilter();

	private ContractCodeDeployOperationBuilder contractCodeDeployOpBuilder = new ContractCodeDeployOperationBuilderFilter();

	private ContractEventSendOperationBuilder contractEventSendOpBuilder = new ContractEventSendOperationBuilderFilter();

	private ContractInvocationProxyBuilder contractInvoProxyBuilder = new ContractInvocationProxyBuilder();

	private ParticipantRegisterOperationBuilder participantRegOpBuilder = new ParticipantRegisterOperationBuilderFilter();

	private ParticipantStateUpdateOperationBuilder participantStateModifyOpBuilder = new ParticipantStateUpdateOperationBuilderFilter();

	// TODO: 暂时只支持单线程情形，未考虑多线程；
	private List<Operation> operationList = new ArrayList<>();

	@Override
	public LedgerInitOperationBuilder ledgers() {
		return ledgerInitOpBuilder;
	}
	
	@Override
	public SecurityOperationBuilder security() {
		return securityOpBuilder;
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

	public ContractEventSendOperationBuilder contractEvents() {
		return contractEventSendOpBuilder;
	}

	@Override
	public ParticipantRegisterOperationBuilder participants() {return participantRegOpBuilder;}

	@Override
	public ParticipantStateUpdateOperationBuilder states() {return participantStateModifyOpBuilder;}

	@Override
	public <T> T contract(String address, Class<T> contractIntf) {
		return contractInvoProxyBuilder.create(address, contractIntf, contractEventSendOpBuilder);
	}

	@Override
	public <T> T contract(Bytes address, Class<T> contractIntf) {
		return contractInvoProxyBuilder.create(address, contractIntf, contractEventSendOpBuilder);
	}

	/**
	 * 返回已经定义的操作列表；
	 * 
	 * @return
	 */
	public Collection<Operation> getOperations() {
		return operationList;
	}

	/**
	 * 返回与操作列表对应的返回值处理器；
	 * 
	 * @return
	 */
	public Collection<OperationResultHandle> getReturnValuetHandlers() {
		List<OperationResultHandle> resultHandlers = new ArrayList<OperationResultHandle>();
		int index = 0;
		for (Operation op : operationList) {
			if (op instanceof ContractEventSendOperation) {
				// 操作具有返回值，创建对应的结果处理器；
				ContractEventSendOpTemplate opTemp = (ContractEventSendOpTemplate) op;
				ContractInvocation invocation = opTemp.getInvocation();
				OperationResultHandle retnHandler;
				if (invocation == null) {
					retnHandler = new NullOperationReturnValueHandler(index);
				} else {
					invocation.setOperationIndex(index);
					retnHandler = invocation;
				}
				resultHandlers.add(retnHandler);
			}
			index++;
		}

		return resultHandlers;
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
	
	private class SecurityOperationBuilderFilter implements SecurityOperationBuilder {

		@Override
		public RolesConfigurer roles() {
			RolesConfigurer rolesConfigurer = SECURITY_OP_BUILDER.roles();
			operationList.add(rolesConfigurer.getOperation());
			return rolesConfigurer;
		}
		
		@Override
		public UserAuthorizer authorziations() {
			UserAuthorizer userAuthorizer = SECURITY_OP_BUILDER.authorziations();
			operationList.add(userAuthorizer.getOperation());
			return userAuthorizer;
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

		private void addOperation() {
			if (op == null) {
				op = innerBuilder.getOperation();
				operationList.add(op);
			}
		}

		@Override
		public DataAccountKVSetOperationBuilder setText(String key, String value, long expVersion) {
			innerBuilder.setText(key, value, expVersion);
			addOperation();
			return this;
		}

		@Override
		public DataAccountKVSetOperationBuilder setInt64(String key, long value, long expVersion) {
			innerBuilder.setInt64(key, value, expVersion);
			addOperation();
			return this;
		}

		@Override
		public DataAccountKVSetOperationBuilder setBytes(String key, Bytes value, long expVersion) {
			innerBuilder.setBytes(key, value, expVersion);
			addOperation();
			return this;
		}

		@Override
		public DataAccountKVSetOperationBuilder setBytes(String key, byte[] value, long expVersion) {
			innerBuilder.setBytes(key, value, expVersion);
			addOperation();
			return this;
		}

		@Override
		public DataAccountKVSetOperationBuilder setImage(String key, byte[] value, long expVersion) {
			innerBuilder.setImage(key, value, expVersion);
			addOperation();
			return this;
		}

		@Override
		public DataAccountKVSetOperationBuilder setJSON(String key, String value, long expVersion) {
			innerBuilder.setJSON(key, value, expVersion);
			addOperation();
			return this;
		}

		@Override
		public DataAccountKVSetOperationBuilder setXML(String key, String value, long expVersion) {
			innerBuilder.setXML(key, value, expVersion);
			addOperation();
			return this;
		}

		@Override
		public DataAccountKVSetOperationBuilder setTimestamp(String key, long value, long expVersion) {
			innerBuilder.setTimestamp(key, value, expVersion);
			addOperation();
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

	private class ParticipantRegisterOperationBuilderFilter implements ParticipantRegisterOperationBuilder {
		@Override
		public ParticipantRegisterOperation register(String  participantName, BlockchainIdentity participantIdentity, NetworkAddress networkAddress) {
			ParticipantRegisterOperation op = PARTICIPANT_REG_OP_BUILDER.register(participantName, participantIdentity, networkAddress);
			operationList.add(op);
			return op;
		}
	}

	private class ParticipantStateUpdateOperationBuilderFilter implements ParticipantStateUpdateOperationBuilder {
		@Override
		public ParticipantStateUpdateOperation update(BlockchainIdentity blockchainIdentity, NetworkAddress networkAddress, ParticipantNodeState participantNodeState) {
			ParticipantStateUpdateOperation op = PARTICIPANT_STATE_UPDATE_OP_BUILDER.update(blockchainIdentity, networkAddress, participantNodeState);
			operationList.add(op);
			return op;
		}
	}

	private class ContractEventSendOperationBuilderFilter implements ContractEventSendOperationBuilder {

		@Override
		public ContractEventSendOperation send(String address, String event, BytesValueList args) {
			return send(Bytes.fromBase58(address), event, args);
		}

		@Override
		public synchronized ContractEventSendOperation send(Bytes address, String event, BytesValueList args) {
			ContractEventSendOpTemplate op = new ContractEventSendOpTemplate(address, event, args);
			operationList.add(op);
			return op;
		}

	}

	/**
	 * 不做任何操作的返回值处理器；
	 * 
	 * @author huanghaiquan
	 *
	 */
	private static class NullOperationReturnValueHandler implements OperationResultHandle {

		private int operationIndex;

		public NullOperationReturnValueHandler(int operationIndex) {
			this.operationIndex = operationIndex;
		}

		@Override
		public int getOperationIndex() {
			return operationIndex;
		}

		@Override
		public Object complete(BytesValue bytesValue) {
			return null;
		}

		@Override
		public void complete(Throwable error) {
		}

	}

}
