package com.jd.blockchain.ledger.core.impl.handles;

import com.jd.blockchain.contract.LocalContractEventContext;
import com.jd.blockchain.contract.engine.ContractServiceProviders;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.core.*;
import com.jd.blockchain.ledger.core.impl.LedgerQueryService;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;
import org.springframework.stereotype.Service;

import static com.jd.blockchain.utils.BaseConstant.CONTRACT_SERVICE_PROVIDER;

@Service
public class ContractEventSendOperationHandle implements OperationHandle {
	@Override
	public void process(Operation op, LedgerDataSet dataset, TransactionRequestContext requestContext,
			LedgerDataSet previousBlockDataset, OperationHandleContext opHandleContext, LedgerService ledgerService) {
		ContractEventSendOperation contractOP = (ContractEventSendOperation) op;
		// 先从账本校验合约的有效性；
		// 注意：必须在前一个区块的数据集中进行校验，因为那是经过共识的数据；从当前新区块链数据集校验则会带来攻击风险：未经共识的合约得到执行；
		if (!previousBlockDataset.getContractAccountSet().contains(contractOP.getContractAddress())) {
			throw new LedgerException(
					String.format("Target contract of ContractEvent was not registered! --[ContractAddress=%s]",
							contractOP.getContractAddress()));
		}
		
		//创建合约的账本上下文实例；
		LedgerQueryService queryService = new LedgerQueryService(ledgerService) ;
		ContractLedgerContext ledgerContext = new ContractLedgerContext(queryService, opHandleContext);

		// TODO:从合约引擎加载合约，执行合约代码；
		ContractAccount contract = previousBlockDataset.getContractAccountSet()
				.getContract(contractOP.getContractAddress());
		try {
			// 在调用方法前，需要加载上下文信息;
			LocalContractEventContext localContractEventContext = new LocalContractEventContext(
					requestContext.getRequest().getTransactionContent().getLedgerHash(),contract.getChainCode(), contractOP.getEvent());
			localContractEventContext.setArgs(contractOP.getArgs()).setTransactionRequest(requestContext.getRequest()).
					setLedgerContext(ledgerContext);
			ContractServiceProviders.getProvider(CONTRACT_SERVICE_PROVIDER).getEngine().setupContract(
					contract.getAddress().toBase58(),contract.getChaincodeVersion(),contract.getChainCode()).
					processEvent(localContractEventContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean support(Class<?> operationType) {
		return ContractEventSendOperation.class.isAssignableFrom(operationType);
	}

}
