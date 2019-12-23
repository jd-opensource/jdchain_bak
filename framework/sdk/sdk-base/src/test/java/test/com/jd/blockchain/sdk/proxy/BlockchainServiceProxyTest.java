package test.com.jd.blockchain.sdk.proxy;

public class BlockchainServiceProxyTest {

//	private static class ArgCaptorMatcher<T> extends CustomMatcher<T> {
//
//		private T arg;
//
//		public ArgCaptorMatcher() {
//			super("OK");
//		}
//
//		@Override
//		public boolean matches(Object item) {
//			this.arg = (T) item;
//			return true;
//		}
//
//		public T getArg() {
//			return arg;
//		}
//	}
//
//	@Test
//	public void testRegisterAccount() throws IOException {
//
//		BlockchainKeyPair gatewayAccount = BlockchainKeyGenerator.getInstance().generate(CryptoKeyType.ED25519);
//		BlockchainKeyPair sponsorAccount = BlockchainKeyGenerator.getInstance().generate(CryptoKeyType.ED25519);
//		BlockchainKeyPair subjectAccount = BlockchainKeyGenerator.getInstance().generate(CryptoKeyType.ED25519);
//		long  sequenceNumber = 110;
//
//		ArgCaptorMatcher<TransactionRequest> txReqCaptor = new ArgCaptorMatcher<>();
//
//		TransactionService consensusService = Mockito.mocker(TransactionService.class);
//		BlockchainQueryService queryService = Mockito.mocker(BlockchainQueryService.class);
//
//		HashDigest txContentHash =CryptoUtils.hash(CryptoAlgorithm.SHA_256).hash(UUID.randomUUID().toString().getBytes("UTF-8"));
//		TxResponseMessage expectedResponse = new TxResponseMessage(txContentHash);
//		expectedResponse.setExecutionState(ExecutionState.SUCCESS);
//
//		when(consensusService.process(argThat(txReqCaptor))).thenReturn(expectedResponse);
//
//		HashDigest ledgerHash = CryptoUtils.hash(CryptoAlgorithm.SHA_256).hash(UUID.randomUUID().toString().getBytes("UTF-8"));
//
//		BlockchainTransactionService serviceProxy = new BlockchainServiceProxy(consensusService, queryService);
//
//		TransactionTemplate txTemplate = serviceProxy.newTransaction(ledgerHash);
//		txTemplate.setSubject(subjectAccount.getAddress(), sequenceNumber);
//
//		BlockchainKeyPair regAccountKeyPair = BlockchainKeyGenerator.getInstance().generate(CryptoKeyType.ED25519);
//		AccountStateType stateType = AccountStateType.MAP;
//		txTemplate.users().register(regAccountKeyPair.getIdentity(), stateType);
//
//		PreparedTransaction prepTx = txTemplate.prepare();
//		HashDigest txHash = prepTx.getHash();
//		prepTx.sign(sponsorAccount);
//
//		TransactionResponse result = prepTx.commit();
//
//		// 验证；
//		// 仅被提交一次；
//		verify(consensusService, times(1)).process(any());
//
//		assertEquals(ExecutionState.SUCCESS, result.getExecutionState());
//
//		// 验证内容；
//		TransactionRequest resolvedTxRequest = txReqCaptor.getArg();
//
//		TransactionContent resolvedTxContent = resolvedTxRequest.getTransactionContent();
//
//		assertEquals(txHash, resolvedTxContent.getHash());
//
//		assertEquals(subjectAccount.getAddress(), resolvedTxContent.getSubjectAccount());
//		assertEquals(sequenceNumber, resolvedTxContent.getSequenceNumber());
//
//
//		Operation[] resolvedOps = resolvedTxContent.getOperations();
//		assertEquals(1, resolvedOps.length);
//		Operation resolvedOP = resolvedOps[0];
////		assertEquals(OperationType.REGISTER_USER.CODE, resolvedOP.getCode());
//
//		UserRegisterOpTemplate accRegOP = new UserRegisterOpTemplate();
//		accRegOP.resolvFrom((OpBlob) resolvedOP);
//
//		assertEquals(regAccountKeyPair.getAddress(), accRegOP.getId().getAddress());
//		assertEquals(regAccountKeyPair.getPubKey().getType(), accRegOP.getId().getPubKey().getType());
//		assertEquals(regAccountKeyPair.getPubKey().getValue(), accRegOP.getId().getPubKey().getValue());
//		assertEquals(stateType, accRegOP.getStateType());
//	}

}
