package com.jd.blockchain.ledger.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.impl.handles.ContractEventSendOperationHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.core.LedgerDataSet;
import com.jd.blockchain.ledger.core.LedgerEditor;
import com.jd.blockchain.ledger.core.LedgerException;
import com.jd.blockchain.ledger.core.LedgerService;
import com.jd.blockchain.ledger.core.LedgerTransactionContext;
import com.jd.blockchain.ledger.core.OperationHandle;
import com.jd.blockchain.ledger.core.TransactionRequestContext;
import com.jd.blockchain.service.TransactionBatchProcess;
import com.jd.blockchain.service.TransactionBatchResult;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.utils.Bytes;

public class TransactionBatchProcessor implements TransactionBatchProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionBatchProcessor.class);
	
	private LedgerService ledgerService;

	private LedgerEditor newBlockEditor;

	private LedgerDataSet previousBlockDataset;

	private OperationHandleRegisteration opHandles;

	// 新创建的交易；
	private LedgerBlock block;

	private TransactionState globalResult;

	private List<TransactionResponse> responseList = new ArrayList<>();

	private TransactionBatchResult batchResult;

	/**
	 * @param newBlockEditor
	 *            新区块的数据编辑器；
	 * @param previousBlockDataset
	 *            新区块的前一个区块的数据集；即未提交新区块之前的经过共识的账本最新数据集；
	 * @param opHandles
	 *            操作处理对象注册表；
	 */
	public TransactionBatchProcessor(LedgerEditor newBlockEditor, LedgerDataSet previousBlockDataset,
			OperationHandleRegisteration opHandles, LedgerService ledgerService) {
		this.newBlockEditor = newBlockEditor;
		this.previousBlockDataset = previousBlockDataset;
		this.opHandles = opHandles;
		this.ledgerService = ledgerService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jd.blockchain.ledger.core.impl.TransactionBatchProcess#schedule(com.jd.
	 * blockchain.ledger.TransactionRequest)
	 */
	@Override
	public TransactionResponse schedule(TransactionRequest request) {
		// 此调用将会验证交易签名，验签失败将会抛出异常，同时，不记录签名错误的交易到链上；
		LedgerTransactionContext txCtx = newBlockEditor.newTransaction(request);
		TransactionState result;
		TransactionReturnMessageData returnMessageData = new TransactionReturnMessageData();

		try {
			LedgerDataSet dataset = txCtx.getDataSet();
			TransactionRequestContext reqCtx = new TransactionRequestContextImpl(request);
			// TODO: 验证签名者的有效性；
			for (Bytes edpAddr : reqCtx.getEndpoints()) {
				if (!previousBlockDataset.getUserAccountSet().contains(edpAddr)) {
					throw new LedgerException("The endpoint signer[" + edpAddr + "] was not registered!");
				}
			}
			for (Bytes edpAddr : reqCtx.getNodes()) {
				if (!previousBlockDataset.getUserAccountSet().contains(edpAddr)) {
					throw new LedgerException("The node signer[" + edpAddr + "] was not registered!");
				}
			}

			// 执行操作；
			Operation[] ops = request.getTransactionContent().getOperations();
			OperationHandleContext handleContext = new OperationHandleContext() {
				@Override
				public void handle(Operation operation) {
					//assert; Instance of operation are one of User related operations or DataAccount related operations;
					OperationHandle hdl = opHandles.getHandle(operation.getClass());
					hdl.process(operation, dataset, reqCtx, previousBlockDataset, this, ledgerService);
				}
			};
			OperationHandle opHandle;
			int contractOpIndex = 0;
			for (Operation op : ops) {
				opHandle = opHandles.getHandle(op.getClass());
				// 合约执行需要填充执行结果
				if (opHandle instanceof ContractEventSendOperationHandle) {
					CompletableFuture<String> currContractReturn = new CompletableFuture<>();
					ContractReturnMessageData crmd = new ContractReturnMessageData(contractOpIndex++, currContractReturn);
					returnMessageData.addContractReturnMessage(crmd);
					((ContractEventSendOperationHandle) opHandle).process(op, dataset, reqCtx, previousBlockDataset, handleContext, ledgerService, currContractReturn);
				} else {
					opHandle.process(op, dataset, reqCtx, previousBlockDataset, handleContext, ledgerService);
				}
			}

			// 提交交易（事务）；
			result = TransactionState.SUCCESS;
			txCtx.commit(result, returnMessageData);
		} catch (LedgerException e) {
			// TODO: 识别更详细的异常类型以及执行对应的处理；
			result = TransactionState.LEDGER_ERROR;
			txCtx.discardAndCommit(TransactionState.LEDGER_ERROR, returnMessageData);
			LOGGER.warn(String.format("Transaction rollback caused by the ledger exception! --[TxHash=%s] --%s",
					request.getHash().toBase58(), e.getMessage()), e);
		} catch (Exception e) {
			result = TransactionState.SYSTEM_ERROR;
			txCtx.discardAndCommit(TransactionState.SYSTEM_ERROR, returnMessageData);
			LOGGER.warn(String.format("Transaction rollback caused by the system exception! --[TxHash=%s] --%s",
					request.getHash().toBase58(), e.getMessage()), e);
		}
		TxResponseHandle resp = new TxResponseHandle(request, result);

		if (!returnMessageData.isContractReturnEmpty()) {

			ContractReturnMessage[] contractReturnMessages = returnMessageData.getContractReturn();

			// 获取结果中的字符串
			String[] returnValue = new String[contractReturnMessages.length];
			try {
				for (int i = 0; i < contractReturnMessages.length; i++) {
					returnValue[i] = contractReturnMessages[i].getReturnMessage();
				}
				resp.setContractReturn(returnValue);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		responseList.add(resp);

		return resp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.core.impl.TransactionBatchProcess#prepare()
	 */
	@Override
	public TransactionBatchResultHandle prepare() {
		if (batchResult != null) {
			throw new IllegalStateException("Batch result has already been prepared or canceled!");
		}
		block = newBlockEditor.prepare();
		batchResult = new TransactionBatchResultHandleImpl();
		return (TransactionBatchResultHandle) batchResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jd.blockchain.ledger.core.impl.TransactionBatchProcess#cancel(com.jd.
	 * blockchain.ledger.ExecutionState)
	 */
	@Override
	public TransactionBatchResult cancel(TransactionState errorResult) {
		if (batchResult != null) {
			throw new IllegalStateException("Batch result has already been prepared or canceled!");
		}

		cancelInError(errorResult);

		batchResult = new TransactionBatchResultImpl();
		return batchResult;
	}

	@Override
	public long blockHeight() {
		if (block != null) {
			return block.getHeight();
		}
		return 0;
	}

	private void commitSuccess() {
		newBlockEditor.commit();
		onCommitted();
	}

	private void cancelInError(TransactionState errorResult) {
		if (errorResult == TransactionState.SUCCESS) {
			throw new IllegalArgumentException("Cann't cancel by an success result!");
		}
		newBlockEditor.cancel();
		this.globalResult = errorResult;
		onCanceled();
	}

	/**
	 * 模板事件方法：交易已提交；
	 */
	protected void onCommitted() {
	}

	/**
	 * 模板事件方法：交易已取消；
	 */
	protected void onCanceled() {
	}

	private class TxResponseHandle implements TransactionResponse {

		private TransactionRequest request;

		private TransactionState result;

		private String[] contractReturn;

		public TxResponseHandle(TransactionRequest request, TransactionState result) {
			this.request = request;
			this.result = result;
		}

		@Override
		public HashDigest getContentHash() {
			return request.getTransactionContent().getHash();
		}

		@Override
		public TransactionState getExecutionState() {
			return result;
		}

		@Override
		public HashDigest getBlockHash() {
			return block == null ? null : block.getHash();
		}

		@Override
		public long getBlockHeight() {
			return block == null ? -1 : block.getHeight();
		}

		@Override
		public boolean isSuccess() {
			return globalResult == null ? result == TransactionState.SUCCESS : globalResult == TransactionState.SUCCESS;
		}

		@Override
		public String[] getContractReturn() {
			return contractReturn;
		}

		public void setContractReturn(String[] contractReturn) {
			this.contractReturn = contractReturn;
		}
	}

	private class TransactionBatchResultImpl implements TransactionBatchResult {

		@Override
		public LedgerBlock getBlock() {
			return block;
		}

		@Override
		public Iterator<TransactionResponse> getResponses() {
			return responseList.iterator();
		}

	}

	private class TransactionBatchResultHandleImpl extends TransactionBatchResultImpl
			implements TransactionBatchResultHandle {

		@Override
		public void commit() {
			commitSuccess();
		}

		@Override
		public void cancel(TransactionState errorResult) {
			cancelInError(errorResult);
		}

	}
}
