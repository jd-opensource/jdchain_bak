//package test.com.jd.blockchain.ledger;
//
//import static org.junit.Assert.assertArrayEquals;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.nio.file.Paths;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.jd.blockchain.binaryproto.DataContractRegistry;
//import com.jd.blockchain.contract.model.ContractPath;
//import com.jd.blockchain.crypto.CryptoAlgorithm;
//import com.jd.blockchain.crypto.CryptoUtils;
//import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
//import com.jd.blockchain.crypto.asymmetric.PubKey;
//import com.jd.blockchain.crypto.hash.HashDigest;
//import com.jd.blockchain.ledger.BlockchainKeyGenerator;
//import com.jd.blockchain.ledger.BlockchainKeyPair;
//import com.jd.blockchain.ledger.ContractCodeDeployOperation;
//import com.jd.blockchain.ledger.ContractEventSendOperation;
//import com.jd.blockchain.ledger.DataAccountKVSetOperation;
//import com.jd.blockchain.ledger.DataAccountRegisterOperation;
//import com.jd.blockchain.ledger.EndpointRequest;
//import com.jd.blockchain.ledger.LedgerBlock;
//import com.jd.blockchain.ledger.LedgerInitSetting;
//import com.jd.blockchain.ledger.LedgerTransaction;
//import com.jd.blockchain.ledger.NodeRequest;
//import com.jd.blockchain.ledger.TransactionBuilder;
//import com.jd.blockchain.ledger.TransactionContent;
//import com.jd.blockchain.ledger.TransactionContentBody;
//import com.jd.blockchain.ledger.TransactionRequest;
//import com.jd.blockchain.ledger.TransactionRequestBuilder;
//import com.jd.blockchain.ledger.TransactionResponse;
//import com.jd.blockchain.ledger.TransactionState;
//import com.jd.blockchain.ledger.UserRegisterOperation;
//import com.jd.blockchain.ledger.core.CryptoConfig;
//import com.jd.blockchain.ledger.core.DataAccountSet;
//import com.jd.blockchain.ledger.core.LedgerDataSet;
//import com.jd.blockchain.ledger.core.LedgerEditor;
//import com.jd.blockchain.ledger.core.LedgerRepository;
//import com.jd.blockchain.ledger.core.LedgerTransactionContext;
//import com.jd.blockchain.ledger.core.TransactionSet;
//import com.jd.blockchain.ledger.core.UserAccount;
//import com.jd.blockchain.ledger.core.impl.DefaultOperationHandleRegisteration;
//import com.jd.blockchain.ledger.core.impl.LedgerManager;
//import com.jd.blockchain.ledger.core.impl.OperationHandleRegisteration;
//import com.jd.blockchain.ledger.core.impl.TransactionBatchProcessor;
//import com.jd.blockchain.ledger.data.AddressEncoding;
//import com.jd.blockchain.ledger.data.ConsensusParticipantData;
//import com.jd.blockchain.ledger.data.LedgerInitSettingData;
//import com.jd.blockchain.ledger.data.TxBuilder;
//import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
//
//import my.utils.Bytes;
//import my.utils.io.BytesUtils;
//import my.utils.net.NetworkAddress;
//
//public class TransactionBatchProcessorTest {
//	private HashDigest ledgerHash = null;
//	BlockchainKeyPair userKey = BlockchainKeyGenerator.getInstance().generate();
//	BlockchainKeyPair dataKey = BlockchainKeyGenerator.getInstance().generate();
//	BlockchainKeyPair sponsorKey = BlockchainKeyGenerator.getInstance().generate();
//	BlockchainKeyPair gatewayKey = BlockchainKeyGenerator.getInstance().generate();
//	BlockchainKeyPair contractKey = BlockchainKeyGenerator.getInstance().generate();
//	TransactionRequest transactionRequest;
//	String pubKeyVal = "jd.com"+Thread.currentThread();
////	String userPubKeyVal = "this is user's pubKey";
//	byte[] chainCode;
//	// 保存资产总数的键；
//	private static final String KEY_TOTAL = "TOTAL";
//	//第二个参数;
//	private static final String KEY_ABC = "abc";
//	// 采用基于内存的 Storage；
//	MemoryKVStorage storage = new MemoryKVStorage();
//
//	@Before
//	public void setUp(){
//		DataContractRegistry.register(TransactionContent.class);
//		DataContractRegistry.register(TransactionContentBody.class);
//		DataContractRegistry.register(TransactionRequest.class);
//		DataContractRegistry.register(NodeRequest.class);
//		DataContractRegistry.register(EndpointRequest.class);
//		DataContractRegistry.register(TransactionResponse.class);
//		DataContractRegistry.register(UserRegisterOperation.class);
//	}
//
////	@After
//	public void after() {
//		//清理所有使用的临时文件;
//		String outputPath = null;
//		try {
//			outputPath = ContractPath.getOutputPath();
//		}finally {
//			deleteDir(new File(outputPath));
//		}
//	}
//
//	/**
//	 * 递归删除目录下的所有文件及子目录下所有文件
//	 * @param dir 将要删除的文件目录
//	 * @return boolean Returns "true" if all deletions were successful.
//	 *                 If a deletion fails, the method stops attempting to
//	 *                 delete and returns "false".
//	 */
//	private static boolean deleteDir(File dir) {
//		if (dir.isDirectory()) {
//			String[] children = dir.list();
//			//递归删除目录中的子目录下
//			for (int i=0; i<children.length; i++) {
//				boolean success = deleteDir(new File(dir, children[i]));
//				if (!success) {
//					return false;
//				}
//			}
//		}
//		// 目录此时为空，可以删除
//		return dir.delete();
//	}
//
//	@Test
//	public void testContractOperations() {
//		LedgerManager ledgerManager = new LedgerManager();
//
//		ledgerHash = initLedger(ledgerManager, storage, sponsorKey,gatewayKey,contractKey);
//		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, storage);
//
//		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(ledgerRepo.getLatestBlock());
//		UserAccount userAcc = previousBlockDataset.getUserAccountSet().getUser(sponsorKey.getAddress());
//		assertNotNull(userAcc);
//		boolean sponsorRegistered = previousBlockDataset.getUserAccountSet().contains(sponsorKey.getAddress());
//		assertTrue(sponsorRegistered);
//
//		LedgerEditor newBlockEditor = ledgerRepo.createNextBlock();
//
//		OperationHandleRegisteration opReg = new DefaultOperationHandleRegisteration();
//		TransactionBatchProcessor txbatchProcessor = new TransactionBatchProcessor(newBlockEditor, previousBlockDataset,
//				opReg, ledgerManager);
//
//		// TODO: 生成合约交易；以及执行合约交易；
//		transactionRequest = this.contractDeploy(ledgerHash, userKey, dataKey,sponsorKey,gatewayKey,contractKey);
//		txbatchProcessor.schedule(transactionRequest);
//
//		LedgerBlock newBlock = newBlockEditor.prepare();
//		newBlockEditor.commit();
//
//		//验证正确性；
//		ledgerManager = new LedgerManager();
//		ledgerRepo = ledgerManager.register(ledgerHash, storage);
//
//		//验证合约部署
//		byte[] actualChainCode = ledgerRepo.getContractAccountSet(newBlock).getContract(contractKey.getAddress()).getChainCode();
//		assertArrayEquals(chainCode, actualChainCode);
//
//		LedgerBlock latestBlock = ledgerRepo.getLatestBlock();
//		assertEquals(newBlock.getHash(), latestBlock.getHash());
//
//		//执行事件;
//		LedgerEditor newBlockEditor1 = ledgerRepo.createNextBlock();
//		LedgerDataSet previousBlockDataset1 = ledgerRepo.getDataSet(ledgerRepo.getLatestBlock());
////		采用schedule方式;
//		txbatchProcessor = new TransactionBatchProcessor(newBlockEditor1, previousBlockDataset1,
//				opReg, ledgerManager);
//		txbatchProcessor.schedule(this.exeContractReq(ledgerHash, sponsorKey,gatewayKey,contractKey));
//		//采用newTransaction方式处理;
//		/**
//		 LedgerTransactionContext txCtx = newBlockEditor1.newTransaction(
//		 this.exeContractReq(ledgerHash, sponsorKey,gatewayKey,contractKey));
//		 LedgerDataSet ldgDS = txCtx.getDataSet();
//		 SignatureDigest signDigest = CryptoUtils.sign(CryptoAlgorithm.ED25519).sign(contractKey.getPrivKey(), Base58Utils.decode(contractKey.getAddress()));
//		 DigitalSignatureBlob dgtSign = new DigitalSignatureBlob(contractKey.getPubKey(), signDigest);
//		 //		DataAccount dataAccount = ldgDS.getDataAccountSet().register(dataKey.getAddress(), dataKey.getPubKey(),null);
//		 //		dataAccount.setBytes("latestBlockHash", ledgerRepo.getLatestBlock().getPreviousHash().getRawDigest(), -1);
//		 // 提交交易结果；
//		 LedgerTransaction tx = txCtx.commit(TransactionState.SUCCESS);
//		 */
//
//		LedgerBlock newBlock1 = newBlockEditor1.prepare();
//		newBlockEditor1.commit();
////		assertEquals(expected, actual);
//
//		//验证合约中的赋值，外部可以获得;
//		//base test;
//		DataAccountSet dataAccountSet = ledgerRepo.getDataAccountSet(newBlock1);
//		PubKey pubKey = new PubKey(CryptoAlgorithm.ED25519, pubKeyVal.getBytes());
//		Bytes dataAddress = AddressEncoding.generateAddress(pubKey);
//		assertEquals(dataAddress,dataAccountSet.getDataAccount(dataAddress).getAddress());
//		assertEquals("hello",new String(dataAccountSet.getDataAccount(dataAddress).getBytes(KEY_TOTAL,-1)));
//		//验证userAccount，从合约内部赋值，然后外部验证;
////		UserAccountSet userAccountSet = ledgerRepo.getUserAccountSet(newBlock1);
////		PubKey userPubKey = new PubKey(CryptoAlgorithm.ED25519, userPubKeyVal.getBytes());
////		String userAddress = AddressEncoding.generateAddress(userPubKey);
////		assertEquals(userAddress,userAccountSet.getUser(userAddress).getAddress());
//
//	}
//
//	private TransactionRequest contractDeploy(HashDigest ledgerHash, BlockchainKeyPair userKey, BlockchainKeyPair dataKey,
//											  BlockchainKeyPair sponsorKey, BlockchainKeyPair gatewayKey, BlockchainKeyPair contractKey){
//		// Build transaction request;
//		TransactionBuilder txBuilder = new TxBuilder(ledgerHash);
//		UserRegisterOperation userRegOp = txBuilder.users().register(userKey.getIdentity());
//		DataAccountRegisterOperation dataAccRegOp = txBuilder.dataAccounts().register(dataKey.getIdentity());
//
//		DataAccountKVSetOperation kvsetOP = txBuilder.dataAccount(dataKey.getAddress())
//				.set("A", "Value_A_0".getBytes(), -1)
//				.set("B", "Value_B_0".getBytes(), -1)
//				.set(KEY_TOTAL, "total value,dataAccount".getBytes(), -1)
//				.set(KEY_ABC, "abc value,dataAccount".getBytes(), -1)
//				//所有的模拟数据都在这个dataAccount中填充;
//				.set("ledgerHash", ledgerHash.getRawDigest(), -1)
////				.set("latestBlockHash", ledgerBlock.getPreviousHash().getRawDigest(), -1)
//				.getOperation();
//		chainCode = this.getChainCodeBytes();
//		ContractCodeDeployOperation contractDplOP = txBuilder.contracts().deploy(contractKey.getIdentity(), this.getChainCodeBytes());
////		ContractEventSendOperation contractEvtSendOP = txBuilder.contractEvents().send(contractKey.getAddress(), "test",
////				"TestContractArgs".getBytes());
//		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
//		txReqBuilder.signAsEndpoint(sponsorKey);
//		txReqBuilder.signAsNode(gatewayKey);
//		return txReqBuilder.buildRequest();
//	}
//
//	private TransactionRequest exeContractReq(HashDigest ledgerHash, BlockchainKeyPair sponsorKey, BlockchainKeyPair gatewayKey, BlockchainKeyPair contractKey){
//		// Build transaction request;
//		TransactionBuilder txBuilder = new TxBuilder(ledgerHash);
//		LedgerManager ledgerManager = new LedgerManager();
//		LedgerRepository ledgerRepo = ledgerManager.register(ledgerHash, storage);
//		LedgerBlock ledgerBlock = ledgerRepo.getLatestBlock();
////		txBuilder.dataAccounts().register(dataKey1.getIdentity());
////		txBuilder.dataAccount(dataKey.getAddress())
////				.set("latestBlockHash", ledgerBlock.getPreviousHash().getRawDigest(), -1).getOperation();
////		HashDigest txRootHashDigest = ledgerRepo.getTransactionSet(ledgerBlock).getRootHash();
//
//		LedgerDataSet previousBlockDataset = ledgerRepo.getDataSet(ledgerRepo.getLatestBlock());
//		previousBlockDataset.getUserAccountSet().getUser(userKey.getAddress());
//
//
//		ContractEventSendOperation contractEvtSendOP = txBuilder.contractEvents().send(contractKey.getAddress(), "issue-asset",
//				("888##abc##"+dataKey.getAddress()+"##"+ledgerBlock.getPreviousHash().toBase58()+"##"+
//						userKey.getAddress()+"##"+contractKey.getAddress()+"##"+transactionRequest.getTransactionContent().getHash().toBase58()
//				+"##"+pubKeyVal).getBytes());
//
//		TransactionSet txset = ledgerRepo.getTransactionSet(ledgerBlock);
//		txset.get(transactionRequest.getTransactionContent().getHash());//此处有value;
//
//		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
//		txReqBuilder.signAsEndpoint(sponsorKey);
//		txReqBuilder.signAsNode(gatewayKey);
//		return txReqBuilder.buildRequest();
//	}
//
//	private HashDigest initLedger(LedgerManager ledgerManager, MemoryKVStorage storage, BlockchainKeyPair userKP,
//								  BlockchainKeyPair gatewayKey,BlockchainKeyPair contractKey) {
//		// 创建账本初始化配置；
//		LedgerInitSetting initSetting = createLedgerInitSetting();
//
//		// 新建账本；
//		LedgerEditor ldgEdt = ledgerManager.newLedger(initSetting, storage);
//
//		// 创建一个模拟的创世交易；
//		TransactionRequest genesisTxReq = LedgerTestUtils.createTxRequest(ledgerHash,
//				CryptoUtils.sign(CryptoAlgorithm.ED25519));
//
//		// 记录交易，注册用户；
//		LedgerTransactionContext txCtx = ldgEdt.newTransaction(genesisTxReq);
//		LedgerDataSet ldgDS = txCtx.getDataSet();
//		UserAccount userAccount = ldgDS.getUserAccountSet().register(userKP.getAddress(), userKP.getPubKey());
//		userAccount.setProperty("Name", "孙悟空", -1);
//		userAccount.setProperty("Age", "10000", -1);
//		System.out.println("UserAddress=" + userAccount.getAddress());
//
//		UserAccount gatewayUserAccount = ldgDS.getUserAccountSet().register(gatewayKey.getAddress(), gatewayKey.getPubKey());
//		userAccount.setProperty("Name", "齐天大圣", -1);
//		userAccount.setProperty("Age", "11111", -1);
//		System.out.println("gatewayUserAccount=" + userAccount.getAddress());
//
//		//注册contract;
////		SignatureDigest signDigest = CryptoUtils.sign(CryptoAlgorithm.ED25519).sign(contractKey.getPrivKey(), Base58Utils.decode(contractKey.getAddress()));
////		DigitalSignatureBlob dgtSign = new DigitalSignatureBlob(contractKey.getPubKey(), signDigest);
////		ldgDS.getContractAccountSet().deploy(contractKey.getAddress(),contractKey.getPubKey(),dgtSign,this.contractBytes());
//
//		// 提交交易结果；
//		LedgerTransaction tx = txCtx.commit(TransactionState.SUCCESS);
//
//		// 生成区块；
//		LedgerBlock genesisBlock = ldgEdt.prepare();
//		ldgEdt.commit();
//		return genesisBlock.getHash();
//	}
//
////    private HashDigest handleContractEvent(LedgerEditor ldgEdt,BlockchainKeyPair userKP,
////                                  BlockchainKeyPair gatewayKey,BlockchainKeyPair contractKey) {
////        // 记录交易，注册用户；
////        LedgerTransactionContext txCtx = ldgEdt.newTransaction(
////                this.genContractReq(ledgerHash,sponsorKey,gatewayKey,contractKey));
////        LedgerDataSet ldgDS = txCtx.getDataSet();
////
////        //注册contract;
////		SignatureDigest signDigest = CryptoUtils.sign(CryptoAlgorithm.ED25519).sign(contractKey.getPrivKey(), Base58Utils.decode(contractKey.getAddress()));
////		DigitalSignatureBlob dgtSign = new DigitalSignatureBlob(contractKey.getPubKey(), signDigest);
////		ldgDS.getContractAccountSet().deploy(contractKey.getAddress(),contractKey.getPubKey(),dgtSign,this.getChainCodeBytes());
////
////        // 提交交易结果；
////        LedgerTransaction tx = txCtx.commit(TransactionState.SUCCESS);
////
////        // 生成区块；
////        LedgerBlock newBlock = ldgEdt.prepare();
////        ldgEdt.commit();
////        return newBlock.getHash();
////    }
//
//	private byte[] getChainCodeBytes(){
//		//构建合约的字节数组;
//		byte[] contractCode= null;
//		File file = null;
//		InputStream input = null;
//		try {
//			String contractZipName = "AssetContract1.contract";
//			URL url = Paths.get(ContractPath.getOutputPath() + contractZipName).toUri().toURL();
//			file = new File(url.toURI());
//			assertTrue("contract zip file is not exist.",file.exists()==true);
//			input = new FileInputStream(file);
//			//这种暴力的读取压缩包，在class解析时有问题，所有需要改进;
//			contractCode = new byte[input.available()];
//			input.read(contractCode);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if(input!=null){
//					input.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return contractCode;
//	}
//
//	private LedgerInitSetting createLedgerInitSetting() {
//		CryptoConfig defCryptoSetting = new CryptoConfig();
//		defCryptoSetting.setAutoVerifyHash(true);
//		defCryptoSetting.setHashAlgorithm(CryptoAlgorithm.SHA256);
//
//		LedgerInitSettingData initSetting = new LedgerInitSettingData();
//
//		initSetting.setLedgerSeed(BytesUtils.toBytes("A Test Ledger seed!", "UTF-8"));
//		initSetting.setCryptoSetting(defCryptoSetting);
//		ConsensusParticipantData[] parties = new ConsensusParticipantData[4];
//		parties[0] = new ConsensusParticipantData();
//		parties[0].setId(0);
//		parties[0].setName("John");
//		CryptoKeyPair kp0 = CryptoUtils.sign(CryptoAlgorithm.ED25519).generateKeyPair();
//		parties[0].setPubKey(kp0.getPubKey());
//		parties[0].setHostAddress(new NetworkAddress("127.0.0.1", 9000));
//
//		parties[1] = new ConsensusParticipantData();
//		parties[1].setId(1);
//		parties[1].setName("Mary");
//		CryptoKeyPair kp1 = CryptoUtils.sign(CryptoAlgorithm.ED25519).generateKeyPair();
//		parties[1].setPubKey(kp1.getPubKey());
//		parties[1].setHostAddress(new NetworkAddress("127.0.0.1", 9010));
//
//		parties[2] = new ConsensusParticipantData();
//		parties[2].setId(2);
//		parties[2].setName("Jerry");
//		CryptoKeyPair kp2 = CryptoUtils.sign(CryptoAlgorithm.ED25519).generateKeyPair();
//		parties[2].setPubKey(kp1.getPubKey());
//		parties[2].setHostAddress(new NetworkAddress("127.0.0.1", 9020));
//
//		parties[3] = new ConsensusParticipantData();
//		parties[3].setId(3);
//		parties[3].setName("Tom");
//		CryptoKeyPair kp3 = CryptoUtils.sign(CryptoAlgorithm.ED25519).generateKeyPair();
//		parties[3].setPubKey(kp1.getPubKey());
//		parties[3].setHostAddress(new NetworkAddress("127.0.0.1", 9030));
//
//		initSetting.setConsensusParticipants(parties);
//
//		return initSetting;
//	}
//}
