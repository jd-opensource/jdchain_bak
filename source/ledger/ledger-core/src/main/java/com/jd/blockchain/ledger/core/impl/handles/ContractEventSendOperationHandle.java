package com.jd.blockchain.ledger.core.impl.handles;

import static com.jd.blockchain.utils.BaseConstant.CONTRACT_SERVICE_PROVIDER;

import org.springframework.stereotype.Service;

import com.jd.blockchain.contract.LocalContractEventContext;
import com.jd.blockchain.contract.engine.ContractCode;
import com.jd.blockchain.contract.engine.ContractEngine;
import com.jd.blockchain.contract.engine.ContractServiceProviders;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.ContractAccount;
import com.jd.blockchain.ledger.core.ContractAccountSet;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerException;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.TransactionRequestContext;
import com.jd.blockchain.ledger.core.impl.LedgerQueryService;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;

@Service
public class ContractEventSendOperationHandle implements OperationHandle {

	private static final ContractEngine JVM_ENGINE;

	static {
		JVM_ENGINE = ContractServiceProviders.getProvider(CONTRACT_SERVICE_PROVIDER).getEngine();
	}

	@Override
	public void process(Operation op, LedgerDataSet dataset, TransactionRequestContext requestContext,
			LedgerDataSet previousBlockDataset, OperationHandleContext opHandleContext, LedgerService ledgerService) {
		ContractEventSendOperation contractOP = (ContractEventSendOperation) op;
		// 先从账本校验合约的有效性；
		// 注意：必须在前一个区块的数据集中进行校验，因为那是经过共识的数据；从当前新区块链数据集校验则会带来攻击风险：未经共识的合约得到执行；
		ContractAccountSet contractSet = previousBlockDataset.getContractAccountSet();
		if (!contractSet.contains(contractOP.getContractAddress())) {
			throw new LedgerException(String.format("Contract was not registered! --[ContractAddress=%s]",
					contractOP.getContractAddress()));
		}

		// 创建合约的账本上下文实例；
		LedgerQueryService queryService = new LedgerQueryService(ledgerService);
		ContractLedgerContext ledgerContext = new ContractLedgerContext(queryService, opHandleContext);

		// 先检查合约引擎是否已经加载合约；如果未加载，再从账本中读取合约代码并装载到引擎中执行；
		ContractAccount contract = contractSet.getContract(contractOP.getContractAddress());
		if (contract == null) {
			throw new LedgerException(String.format("Contract was not registered! --[ContractAddress=%s]",
					contractOP.getContractAddress()));
		}

		// 创建合约上下文;
		LocalContractEventContext localContractEventContext = new LocalContractEventContext(
				requestContext.getRequest().getTransactionContent().getLedgerHash(), contractOP.getEvent());
		localContractEventContext.setArgs(contractOP.getArgs()).setTransactionRequest(requestContext.getRequest())
				.setLedgerContext(ledgerContext);

		ContractCode contractCode = JVM_ENGINE.getContract(contract.getAddress(), contract.getChaincodeVersion());
		if (contractCode == null) {
			// 装载合约；
			contractCode = JVM_ENGINE.setupContract(contract.getAddress(), contract.getChaincodeVersion(),
					contract.getChainCode());
		}

		// 处理合约事件；
		contractCode.processEvent(localContractEventContext);
	}

	@Override
	public boolean support(Class<?> operationType) {
		return ContractEventSendOperation.class.isAssignableFrom(operationType);
	}

}
