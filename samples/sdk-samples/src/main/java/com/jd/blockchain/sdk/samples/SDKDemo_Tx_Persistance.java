/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.samples.SDKDemo_RegisterUser
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/10/18 下午2:00
 * Description: 注册用户
 */
package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.transaction.SignatureUtils;
import com.jd.blockchain.utils.ConsoleUtils;

/**
 * 注册用户
 * 
 * @author shaozhuguang
 * @create 2018/10/18
 * @since 1.0.0
 */

public class SDKDemo_Tx_Persistance {
	public static void main(String[] args) {

		String GATEWAY_IPADDR = "127.0.0.1";
		int GATEWAY_PORT = 11000;
		if (args != null && args.length == 2) {
			GATEWAY_IPADDR = args[0];
			GATEWAY_PORT = Integer.parseInt(args[1]);
		}

		// 注册相关class
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(TransactionResponse.class);

		PrivKey privKey1 = SDKDemo_Params.privkey0;
		PubKey pubKey1 = SDKDemo_Params.pubKey0;
		PrivKey privKey2 = SDKDemo_Params.privkey1;
		PubKey pubKey2 = SDKDemo_Params.pubKey1;

		BlockchainKeypair CLIENT_CERT = new BlockchainKeypair(SDKDemo_Params.pubKey0, SDKDemo_Params.privkey0);

		boolean SECURE = false;
		GatewayServiceFactory gatewayServiceFactory = GatewayServiceFactory.connect(GATEWAY_IPADDR, GATEWAY_PORT, SECURE,
				CLIENT_CERT);
		BlockchainService blockchainService = gatewayServiceFactory.getBlockchainService();

		HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();
		// 在本地定义注册账号的 TX；
		TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHashs[0]);

		// existed signer
		AsymmetricKeypair keyPair1 = new BlockchainKeypair(pubKey1, privKey1);
		AsymmetricKeypair keyPair2 = new BlockchainKeypair(pubKey2, privKey2);

		BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();

		// 注册
		txTemp.users().register(user.getIdentity());

		// 定义角色权限；
		txTemp.security().roles().configure("MANAGER")
				.enable(LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT)
				.enable(TransactionPermission.CONTRACT_OPERATION);
		txTemp.security().authorziations().forUser(user.getIdentity()).authorize("MANAGER");

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();
		
		// 序列化交易内容；
		byte[] txContentBytes = BinaryProtocol.encode(prepTx.getTransactionContent(), TransactionContent.class);
		
		// 反序列化交易内容；
		TransactionContent txContent = BinaryProtocol.decode(txContentBytes, TransactionContent.class);
		
		// 对交易内容签名；
		DigitalSignature signature1 = SignatureUtils.sign(keyPair1.getAlgorithm(), txContent, keyPair1);
		
		// 根据交易内容重新准备交易；
		PreparedTransaction decodedPrepTx = blockchainService.prepareTransaction(txContent);
		
		// 使用私钥进行签名，或附加签名；
		decodedPrepTx.addSignature(signature1);
		decodedPrepTx.sign(keyPair2);

		// 提交交易；
		TransactionResponse transactionResponse = decodedPrepTx.commit();
		// 解析返回结果；如果是合约调用的操作，需要自行解析返回结果；
		if (transactionResponse.isSuccess()) {
			// 操作结果对应于交易中的操作顺序；无返回结果的操作对应结果为 null;
			OperationResult opResult = transactionResponse.getOperationResults()[0];//
			Class<?> dataClazz = null;//返回值的类型；
			Object value = BytesValueEncoding.decode(opResult.getResult(), dataClazz);
		}
		

		ConsoleUtils.info("register user complete, result is [%s]", transactionResponse.isSuccess());
	}
}