package com.jd.blockchain.mocker.handler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.ContractSerializeUtils;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.contract.LedgerContext;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.TransactionRequestContext;
import com.jd.blockchain.ledger.core.impl.LedgerManager;
import com.jd.blockchain.ledger.core.impl.LedgerQueryService;
import com.jd.blockchain.ledger.core.impl.OperationHandleContext;
import com.jd.blockchain.ledger.core.impl.handles.ContractLedgerContext;
import com.jd.blockchain.mocker.proxy.ExecutorProxy;

public class MockerContractExeHandle implements OperationHandle {

	private Map<HashDigest, ExecutorProxy> executorProxyMap = new ConcurrentHashMap<>();

	private LedgerManager ledgerManager;

	private HashDigest ledgerHash;

	@Override
	public byte[] process(Operation op, LedgerDataSet dataset, TransactionRequestContext requestContext,
			LedgerDataSet previousBlockDataset, OperationHandleContext opHandleContext, LedgerService ledgerService) {
		ContractEventSendOperation contractOP = (ContractEventSendOperation) op;

		HashDigest txHash = requestContext.getRequest().getTransactionContent().getHash();

		ExecutorProxy executorProxy = executorProxyMap.get(txHash);

		Object result = null;
		if (executorProxy != null) {
			LedgerQueryService queryService = new LedgerQueryService(ledgerManager);
			ContractLedgerContext ledgerContext = new ContractLedgerContext(queryService, opHandleContext);

			MockerContractEventContext contractEventContext = new MockerContractEventContext(ledgerHash,
					contractOP.getEvent(), requestContext.getRequest(), ledgerContext);

			Object instance = executorProxy.getInstance();
			EventProcessingAware awire = null;

			if (instance instanceof EventProcessingAware) {
				awire = (EventProcessingAware) instance;
				awire.beforeEvent(contractEventContext);
			}

			try {
				result = executorProxy.invoke();
				if (awire != null) {
					// After处理过程
					awire.postEvent(contractEventContext, null);
				}
			} catch (Exception e) {
				if (awire != null) {
					awire.postEvent(contractEventContext, new ContractException(e.getMessage()));
				}
			} finally {
				removeExecutorProxy(txHash);
			}
		}

		// No return value;
		return ContractSerializeUtils.serialize(result);
	}

//	@Override
//	public AsyncFuture<byte[]> asyncProcess(Operation op, LedgerDataSet newBlockDataset, TransactionRequestContext requestContext, LedgerDataSet previousBlockDataset, OperationHandleContext handleContext, LedgerService ledgerService) {
//		return null;
//	}

	@Override
	public boolean support(Class<?> operationType) {
		return ContractEventSendOperation.class.isAssignableFrom(operationType);
	}

	public void initLedger(LedgerManager ledgerManager, HashDigest ledgerHash) {
		this.ledgerManager = ledgerManager;
		this.ledgerHash = ledgerHash;
	}

	public void registerExecutorProxy(HashDigest hashDigest, ExecutorProxy executorProxy) {
		executorProxyMap.put(hashDigest, executorProxy);
	}

	public ExecutorProxy removeExecutorProxy(HashDigest hashDigest) {
		return executorProxyMap.remove(hashDigest);
	}

	public static class MockerContractEventContext implements ContractEventContext {

		private HashDigest ledgeHash;

		private String event;

		private TransactionRequest transactionRequest;

		private LedgerContext ledgerContext;

		public MockerContractEventContext(HashDigest ledgeHash, String event, TransactionRequest transactionRequest,
				LedgerContext ledgerContext) {
			this.ledgeHash = ledgeHash;
			this.event = event;
			this.transactionRequest = transactionRequest;
			this.ledgerContext = ledgerContext;
		}

		@Override
		public HashDigest getCurrentLedgerHash() {
			return ledgeHash;
		}

		@Override
		public TransactionRequest getTransactionRequest() {
			return transactionRequest;
		}

		@Override
		public Set<BlockchainIdentity> getTxSigners() {
			return null;
		}

		@Override
		public String getEvent() {
			return event;
		}

		@Override
		public byte[] getArgs() {
			return null;
		}

		@Override
		public LedgerContext getLedger() {
			return ledgerContext;
		}

		@Override
		public Set<BlockchainIdentity> getContracOwners() {
			return null;
		}
	}
}
