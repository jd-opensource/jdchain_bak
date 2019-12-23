package com.jd.blockchain.ledger.core.handles;

import org.springframework.stereotype.Service;

import com.jd.blockchain.contract.LocalContractEventContext;
import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.LedgerException;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.core.ContractAccount;
import com.jd.blockchain.ledger.core.ContractAccountQuery;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.LedgerQueryService;
import com.jd.blockchain.ledger.core.MultiIDsPolicy;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.SecurityContext;
import com.jd.blockchain.ledger.core.SecurityPolicy;
import com.jd.blockchain.ledger.core.TransactionRequestExtension;

@Service
public abstract class AbtractContractEventSendOperationHandle implements OperationHandle {

	@Override
	public Class<?> getOperationType() {
		return ContractEventSendOperation.class;
	}

	@Override
	public BytesValue process(Operation op, LedgerDataset newBlockDataset, TransactionRequestExtension requestContext,
			LedgerQuery ledger, OperationHandleContext opHandleContext) {
		// 权限校验；
		SecurityPolicy securityPolicy = SecurityContext.getContextUsersPolicy();
		securityPolicy.checkEndpointPermission(TransactionPermission.CONTRACT_OPERATION, MultiIDsPolicy.AT_LEAST_ONE);

		// 操作账本；
		ContractEventSendOperation contractOP = (ContractEventSendOperation) op;

		return doProcess(requestContext, contractOP, newBlockDataset, ledger, opHandleContext);
	}

	private BytesValue doProcess(TransactionRequestExtension request, ContractEventSendOperation contractOP,
			LedgerDataset newBlockDataset, LedgerQuery ledger, OperationHandleContext opHandleContext) {
		// 先从账本校验合约的有效性；
		// 注意：必须在前一个区块的数据集中进行校验，因为那是经过共识的数据；从当前新区块链数据集校验则会带来攻击风险：未经共识的合约得到执行；
		ContractAccountQuery contractSet = ledger.getContractAccountset();
		if (!contractSet.contains(contractOP.getContractAddress())) {
			throw new LedgerException(String.format("Contract was not registered! --[ContractAddress=%s]",
					contractOP.getContractAddress()));
		}

		// 创建合约的账本上下文实例；
		LedgerQueryService queryService = new LedgerQueryService(ledger);
		ContractLedgerContext ledgerContext = new ContractLedgerContext(queryService, opHandleContext);

		// 先检查合约引擎是否已经加载合约；如果未加载，再从账本中读取合约代码并装载到引擎中执行；
		ContractAccount contract = contractSet.getAccount(contractOP.getContractAddress());
		if (contract == null) {
			throw new LedgerException(String.format("Contract was not registered! --[ContractAddress=%s]",
					contractOP.getContractAddress()));
		}

		// 创建合约上下文;
		LocalContractEventContext localContractEventContext = new LocalContractEventContext(
				request.getTransactionContent().getLedgerHash(), contractOP.getEvent());
		localContractEventContext.setArgs(contractOP.getArgs()).setTransactionRequest(request)
				.setLedgerContext(ledgerContext);

		// 装载合约；
		ContractCode contractCode = loadContractCode(contract);

		// 处理合约事件；
		return contractCode.processEvent(localContractEventContext);
	}

	protected abstract ContractCode loadContractCode(ContractAccount contract);

}
