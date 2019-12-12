package com.jd.blockchain.mocker.handler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.ContractException;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.contract.LedgerContext;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueEncoding;
import com.jd.blockchain.ledger.BytesValueList;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.core.LedgerDataset;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.ledger.core.LedgerQueryService;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.OperationHandleContext;
import com.jd.blockchain.ledger.core.TransactionRequestExtension;
import com.jd.blockchain.ledger.core.handles.ContractLedgerContext;
import com.jd.blockchain.mocker.proxy.ExecutorProxy;

public class MockerContractExeHandle implements OperationHandle {

	private Map<HashDigest, ExecutorProxy> executorProxyMap = new ConcurrentHashMap<>();

	private LedgerManager ledgerManager;

	private HashDigest ledgerHash;

	@Override
	public BytesValue process(Operation op, LedgerDataset dataset, TransactionRequestExtension request,
			LedgerQuery ledger, OperationHandleContext opHandleContext) {
		ContractEventSendOperation contractOP = (ContractEventSendOperation) op;

		HashDigest txHash = request.getTransactionContent().getHash();

		ExecutorProxy executorProxy = executorProxyMap.get(txHash);

		Object result = null;
		if (executorProxy != null) {
			LedgerQueryService queryService = new LedgerQueryService(ledger);
			ContractLedgerContext ledgerContext = new ContractLedgerContext(queryService, opHandleContext);

			MockerContractEventContext contractEventContext = new MockerContractEventContext(ledgerHash,
					contractOP.getEvent(), request, ledgerContext);

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
		return BytesValueEncoding.encodeSingle(result, null);
	}

	@Override
	public Class<?> getOperationType() {
		return ContractEventSendOperation.class;
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
		public BytesValueList getArgs() {
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
