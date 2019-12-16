package com.jd.blockchain.ledger.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.jd.blockchain.ledger.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.core.TransactionRequestExtension.Credential;
import com.jd.blockchain.service.TransactionBatchProcess;
import com.jd.blockchain.service.TransactionBatchResult;
import com.jd.blockchain.service.TransactionBatchResultHandle;
import com.jd.blockchain.transaction.SignatureUtils;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.transaction.TxResponseMessage;

public class TransactionBatchProcessor implements TransactionBatchProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionBatchProcessor.class);

	private LedgerSecurityManager securityManager;

	private LedgerEditor newBlockEditor;

	private LedgerQuery ledger;

	private OperationHandleRegisteration handlesRegisteration;

	// 新创建的交易；
	private LedgerBlock block;

	private TransactionState globalResult;

	private List<TransactionResponse> responseList = new ArrayList<>();

	private TransactionBatchResult batchResult;

	public byte[] getPrevLatestBlockHash() {
		return ledger.getLatestBlockHash().toBytes();
	}

	public byte[] getGenisBlockHash() {
		return ledger.getBlockHash(0).toBytes();
	}

	public long getPreLatestBlockHeight() {
		return ledger.getLatestBlockHeight();
	}

	public HashDigest getLedgerHash() {
		return ledger.getHash();
	}

	/**
	 * @param newBlockEditor 新区块的数据编辑器；
	 * @param newBlockEditor  账本查询器，只包含新区块的前一个区块的数据集；即未提交新区块之前的经过共识的账本最新数据集；
	 * @param opHandles      操作处理对象注册表；
	 */
	public TransactionBatchProcessor(LedgerSecurityManager securityManager, LedgerEditor newBlockEditor,
			LedgerQuery ledger, OperationHandleRegisteration opHandles) {
		this.securityManager = securityManager;
		this.newBlockEditor = newBlockEditor;
		this.ledger = ledger;
		this.handlesRegisteration = opHandles;
	}

	public TransactionBatchProcessor(LedgerRepository ledgerRepo, OperationHandleRegisteration handlesRegisteration) {
		this.ledger = ledgerRepo;
		this.handlesRegisteration = handlesRegisteration;
		
		LedgerBlock ledgerBlock = ledgerRepo.getLatestBlock();
		LedgerDataQuery ledgerDataQuery = ledgerRepo.getLedgerData(ledgerBlock);
		LedgerAdminDataQuery previousAdminDataset = ledgerDataQuery.getAdminDataset();
		this.securityManager = new LedgerSecurityManagerImpl(previousAdminDataset.getAdminInfo().getRolePrivileges(),
				previousAdminDataset.getAdminInfo().getAuthorizations(), previousAdminDataset.getParticipantDataset(),
				ledgerDataQuery.getUserAccountSet());
		
		this.newBlockEditor = ledgerRepo.createNextBlock();

	}

	public static TransactionBatchProcess create(LedgerRepository ledgerRepo,
			OperationHandleRegisteration handlesRegisteration) {
		LedgerBlock ledgerBlock = ledgerRepo.getLatestBlock();
		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
		LedgerDataQuery previousBlockDataset = ledgerRepo.getLedgerData(ledgerBlock);

		LedgerAdminDataQuery previousAdminDataset = previousBlockDataset.getAdminDataset();
		LedgerSecurityManager securityManager = new LedgerSecurityManagerImpl(
				previousAdminDataset.getAdminInfo().getRolePrivileges(),
				previousAdminDataset.getAdminInfo().getAuthorizations(), previousAdminDataset.getParticipantDataset(),
				previousBlockDataset.getUserAccountSet());

		TransactionBatchProcessor processor = new TransactionBatchProcessor(securityManager, newBlockEditor, ledgerRepo,
				handlesRegisteration);
		return processor;
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
		TransactionResponse resp;
		try {
			LOGGER.debug("Start handling transaction... --[BlockHeight={}][RequestHash={}][TxHash={}]",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash());

			TransactionRequestExtension reqExt = new TransactionRequestExtensionImpl(request);

			// 初始化交易的用户安全策略；
			SecurityPolicy securityPolicy = securityManager.createSecurityPolicy(reqExt.getEndpointAddresses(),
					reqExt.getNodeAddresses());
			SecurityContext.setContextUsersPolicy(securityPolicy);

			// 安全校验；
			checkSecurity(securityPolicy);

			// 验证交易请求；
			checkRequest(reqExt);

			// 创建交易上下文；
			// 此调用将会验证交易签名，验签失败将会抛出异常，同时，不记录签名错误的交易到链上；
			LedgerTransactionContext txCtx = newBlockEditor.newTransaction(request);

			// 处理交易；
			resp = handleTx(reqExt, txCtx);

			LOGGER.debug("Complete handling transaction.  --[BlockHeight={}][RequestHash={}][TxHash={}]",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash());

		} catch (IllegalTransactionException e) {
			// 抛弃发生处理异常的交易请求；
			resp = discard(request, e.getTxState());
			LOGGER.error(String.format(
					"Ignore transaction caused by IllegalTransactionException! --[BlockHeight=%s][RequestHash=%s][TxHash=%s] --%s",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash(),
					e.getMessage()), e);

		} catch (BlockRollbackException e) {
			// 发生区块级别的处理异常，向上重新抛出异常进行处理，整个区块可能被丢弃；
			resp = discard(request, e.getState());
			LOGGER.error(String.format(
					"Ignore transaction caused by BlockRollbackException! --[BlockHeight=%s][RequestHash=%s][TxHash=%s] --%s",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash(),
					e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			// 抛弃发生处理异常的交易请求；
			resp = discard(request, TransactionState.SYSTEM_ERROR);
			LOGGER.error(String.format(
					"Ignore transaction caused by the system exception! --[BlockHeight=%s][RequestHash=%s][TxHash=%s] --%s",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash(),
					e.getMessage()), e);

		} finally {
			// 清空交易的用户安全策略；
			SecurityContext.removeContextUsersPolicy();
		}

		responseList.add(resp);
		return resp;
	}

	/**
	 * 执行安全验证；
	 */
	private void checkSecurity(SecurityPolicy securityPolicy) {
		// 验证节点和终端身份的合法性；
		// 多重身份签署的必须全部身份都合法；
		securityPolicy.checkEndpointValidity(MultiIDsPolicy.ALL);
		securityPolicy.checkNodeValidity(MultiIDsPolicy.ALL);

		// 验证参与方节点是否具有核准交易的权限；
		securityPolicy.checkNodePermission(LedgerPermission.APPROVE_TX, MultiIDsPolicy.AT_LEAST_ONE);
	}

	private void checkRequest(TransactionRequestExtension reqExt) {
		// TODO: 把验签和创建交易并行化；
		checkTxContentHash(reqExt);
		checkEndpointSignatures(reqExt);
		checkNodeSignatures(reqExt);
	}

	private void checkTxContentHash(TransactionRequestExtension requestExt) {
		TransactionContent txContent = requestExt.getTransactionContent();
		if (!TxBuilder.verifyTxContentHash(txContent, txContent.getHash())) {
			// 由于哈希校验失败，引发IllegalTransactionException，使外部调用抛弃此交易请求；
			throw new IllegalTransactionException(
					"Wrong  transaction content hash! --[TxHash=" + requestExt.getTransactionContent().getHash() + "]!",
					TransactionState.IGNORED_BY_WRONG_CONTENT_SIGNATURE);
		}
	}

	private void checkNodeSignatures(TransactionRequestExtension request) {
		TransactionContent txContent = request.getTransactionContent();
		Collection<Credential> nodes = request.getNodes();
		if (nodes != null) {
			for (Credential node : nodes) {
				if (!SignatureUtils.verifyHashSignature(txContent.getHash(), node.getSignature().getDigest(),
						node.getPubKey())) {
					// 由于签名校验失败，引发IllegalTransactionException，使外部调用抛弃此交易请求；
					throw new IllegalTransactionException(
							String.format("Wrong transaction node signature! --[Tx Hash=%s][Node Signer=%s]!",
									request.getTransactionContent().getHash(), node.getAddress()),
							TransactionState.IGNORED_BY_WRONG_CONTENT_SIGNATURE);
				}
			}
		}
	}

	private void checkEndpointSignatures(TransactionRequestExtension request) {
		TransactionContent txContent = request.getTransactionContent();
		Collection<Credential> endpoints = request.getEndpoints();
		if (endpoints != null) {
			for (Credential endpoint : endpoints) {
				if (!SignatureUtils.verifyHashSignature(txContent.getHash(), endpoint.getSignature().getDigest(),
						endpoint.getPubKey())) {
					// 由于签名校验失败，引发IllegalTransactionException，使外部调用抛弃此交易请求；
					throw new IllegalTransactionException(
							String.format("Wrong transaction endpoint signature! --[Tx Hash=%s][Endpoint Signer=%s]!",
									request.getTransactionContent().getHash(), endpoint.getAddress()),
							TransactionState.IGNORED_BY_WRONG_CONTENT_SIGNATURE);
				}
			}
		}
	}

	/**
	 * 处理交易；<br>
	 * 
	 * 此方法会处理所有的异常，以不同结果的 {@link TransactionResponse} 返回；
	 * 
	 * @param request
	 * @param txCtx
	 * @return
	 */
	private TransactionResponse handleTx(TransactionRequestExtension request, LedgerTransactionContext txCtx) {
		TransactionState result;
		List<OperationResult> operationResults = new ArrayList<>();
		try {
			LedgerDataset dataset = txCtx.getDataset();

			// 执行操作；
			Operation[] ops = request.getTransactionContent().getOperations();
			OperationHandleContext handleContext = new OperationHandleContext() {
				@Override
				public void handle(Operation operation) {
					// assert; Instance of operation are one of User related operations or
					// DataAccount related operations;
					OperationHandle hdl = handlesRegisteration.getHandle(operation.getClass());
					hdl.process(operation, dataset, request, ledger, this);
				}
			};
			OperationHandle opHandle;
			int opIndex = 0;
			for (Operation op : ops) {
				opHandle = handlesRegisteration.getHandle(op.getClass());
				BytesValue opResult = opHandle.process(op, dataset, request, ledger, handleContext);
				if (opResult != null) {
					operationResults.add(new OperationResultData(opIndex, opResult));
				}
				opIndex++;
			}

			// 提交交易（事务）；
			result = TransactionState.SUCCESS;
			txCtx.commit(result, operationResults);
		} catch (TransactionRollbackException e) {
			result = TransactionState.IGNORED_BY_TX_FULL_ROLLBACK;
			txCtx.rollback();
			LOGGER.error(String.format(
					"Transaction was full rolled back! --[BlockHeight=%s][RequestHash=%s][TxHash=%s] --%s",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash(),
					e.getMessage()), e);
		} catch (BlockRollbackException e) {
			// rollback all the block；
			// TODO: handle the BlockRollbackException in detail；
			result = TransactionState.IGNORED_BY_BLOCK_FULL_ROLLBACK;
			txCtx.rollback();
			LOGGER.error(
					String.format("Transaction was rolled back! --[BlockHeight=%s][RequestHash=%s][TxHash=%s] --%s",
							newBlockEditor.getBlockHeight(), request.getHash(),
							request.getTransactionContent().getHash(), e.getMessage()),
					e);
			// 重新抛出由上层错误处理；
			throw e;
		} catch (LedgerException e) {
			// TODO: 识别更详细的异常类型以及执行对应的处理；
			result = TransactionState.LEDGER_ERROR;
			if (e instanceof DataAccountDoesNotExistException) {
				result = TransactionState.DATA_ACCOUNT_DOES_NOT_EXIST;
			} else if (e instanceof UserDoesNotExistException) {
				result = TransactionState.USER_DOES_NOT_EXIST;
			} else if (e instanceof ContractDoesNotExistException) {
				result = TransactionState.CONTRACT_DOES_NOT_EXIST;
			} else if (e instanceof ParticipantDoesNotExistException) {
				result = TransactionState.PARTICIPANT_DOES_NOT_EXIST;
			} else if (e instanceof DataVersionConflictException) {
				result = TransactionState.DATA_VERSION_CONFLICT;
			}
			txCtx.discardAndCommit(result, operationResults);
			LOGGER.error(String.format(
					"Due to ledger exception, the data changes resulting from transaction execution will be rolled back and the results of the transaction will be committed! --[BlockHeight=%s][RequestHash=%s][TxHash=%s] --%s",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash(),
					e.getMessage()), e);
		} catch (LedgerSecurityException e) {
			// TODO: 识别更详细的异常类型以及执行对应的处理；
			result = TransactionState.REJECTED_BY_SECURITY_POLICY;
			txCtx.discardAndCommit(result, operationResults);
			LOGGER.error(String.format(
					"Due to ledger security exception, the data changes resulting from transaction execution will be rolled back and the results of the transaction will be committed! --[BlockHeight=%s][RequestHash=%s][TxHash=%s] --%s",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash(),
					e.getMessage()), e);
		} catch (Exception e) {
			result = TransactionState.SYSTEM_ERROR;
			txCtx.discardAndCommit(TransactionState.SYSTEM_ERROR, operationResults);
			LOGGER.error(String.format(
					"Due to system exception, the data changes resulting from transaction execution will be rolled back and the results of the transaction will be committed! --[BlockHeight=%s][RequestHash=%s][TxHash=%s] --%s",
					newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash(),
					e.getMessage()), e);
		}
		TxResponseHandle resp = new TxResponseHandle(request, result);

		if (!operationResults.isEmpty()) {
			OperationResult[] operationResultArray = new OperationResult[operationResults.size()];
			resp.setOperationResults(operationResults.toArray(operationResultArray));
		}
		return resp;
	}

	/**
	 * 直接丢弃交易；
	 * 
	 * @param request
	 * @param txState
	 * @return 丢弃交易的回复；只包含原始请求中的交易内容哈希和交易被丢弃的原因，而不包含区块信息；
	 */
	private TransactionResponse discard(TransactionRequest request, TransactionState txState) {
		// 丢弃交易的回复；只返回请求的交易内容哈希和交易被丢弃的原因，
		TxResponseMessage resp = new TxResponseMessage(request.getTransactionContent().getHash());
		resp.setExecutionState(txState);

		LOGGER.error("Discard transaction request! --[BlockHeight={}][RequestHash={}][TxHash={}][ResponseState={}]",
				newBlockEditor.getBlockHeight(), request.getHash(), request.getTransactionContent().getHash(),
				resp.getExecutionState());
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
		this.block = newBlockEditor.prepare();
		this.batchResult = new TransactionBatchResultHandleImpl();
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
//		if (block != null) {
//			return block.getHeight();
//		}
//		return 0;

		return ledger.getLatestBlockHeight();
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

		private OperationResult[] operationResults;

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
		public OperationResult[] getOperationResults() {
			return operationResults;
		}

		public void setOperationResults(OperationResult[] operationResults) {
			this.operationResults = operationResults;
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
