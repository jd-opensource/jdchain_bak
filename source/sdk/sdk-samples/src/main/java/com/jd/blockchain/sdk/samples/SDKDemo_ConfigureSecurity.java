/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.sdk.samples.SDKDemo_RegisterUser
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/10/18 下午2:00
 * Description: 注册用户
 */
package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.utils.ConsoleUtils;

/**
 * 注册用户
 * 
 * @author shaozhuguang
 * @create 2018/10/18
 * @since 1.0.0
 */

public class SDKDemo_ConfigureSecurity {
	public static void main(String[] args) {

		String GATEWAY_IPADDR = "127.0.0.1";
		int GATEWAY_PORT = 8081;
		if (args != null && args.length == 2) {
			GATEWAY_IPADDR = args[0];
			GATEWAY_PORT = Integer.parseInt(args[1]);
		}

		// 注册相关class
		DataContractRegistry.register(TransactionContent.class);
		DataContractRegistry.register(TransactionContentBody.class);
		DataContractRegistry.register(TransactionRequest.class);
		DataContractRegistry.register(NodeRequest.class);
		DataContractRegistry.register(EndpointRequest.class);
		DataContractRegistry.register(TransactionResponse.class);

		PrivKey privKey = SDKDemo_Params.privkey1;
		PubKey pubKey = SDKDemo_Params.pubKey1;

		BlockchainKeypair CLIENT_CERT = new BlockchainKeypair(SDKDemo_Params.pubKey0, SDKDemo_Params.privkey0);

		boolean SECURE = false;
		GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IPADDR, GATEWAY_PORT, SECURE,
				CLIENT_CERT);
		BlockchainService service = serviceFactory.getBlockchainService();

		HashDigest[] ledgerHashs = service.getLedgerHashs();
		// 在本地定义注册账号的 TX；
		TransactionTemplate txTemp = service.newTransaction(ledgerHashs[0]);

		// existed signer
		AsymmetricKeypair signer = getSigner();

		BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();

		// 注册
		txTemp.users().register(user.getIdentity());

		txTemp.security().roles().configure("ADMIN")
				.enable(LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT)
				.enable(TransactionPermission.DIRECT_OPERATION).configure("GUEST")
				.enable(TransactionPermission.CONTRACT_OPERATION);

		// TX 准备就绪；
		PreparedTransaction prepTx = txTemp.prepare();

		// 使用私钥进行签名；
		prepTx.sign(signer);

		// 提交交易；
		TransactionResponse transactionResponse = prepTx.commit();

		ConsoleUtils.info("register user complete, result is [%s]", transactionResponse.isSuccess());
	}

	private static AsymmetricKeypair getSigner() {
		return new BlockchainKeypair(SDKDemo_Params.pubKey1, SDKDemo_Params.privkey1);
	}
}