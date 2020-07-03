package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.net.NetworkAddress;

public class SDK_RegistParticipant_Demo extends SDK_Base_Demo {

	public static void main(String[] args) {
		new SDK_RegistParticipant_Demo().regParticipant();
	}

	public void regParticipant() {

		//新参与方的公私钥
		String PUB = "3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9";
		String PRIV = "177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x";

		PrivKey privKey = KeyGenUtils.decodePrivKeyWithRawPassword(PRIV, SDKDemo_Constant.PASSWORD);

		PubKey pubKey = KeyGenUtils.decodePubKey(PUB);

		System.out.println("Address = " + AddressEncoding.generateAddress(pubKey));

		BlockchainKeypair user = new BlockchainKeypair(pubKey, privKey);

		// 新参与方的共识网络地址
		NetworkAddress networkAddress = new NetworkAddress("127.0.0.1", 20000);

		// 定义交易模板
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		// 注册参与方
		txTpl.participants().register("Peer4", user.getIdentity(), networkAddress);

		// TX 准备就绪；
		PreparedTransaction prepTx = txTpl.prepare();

		// 使用私钥进行签名；
		prepTx.sign(adminKey);

		// 提交交易；
		TransactionResponse transactionResponse = prepTx.commit();

		System.out.println(transactionResponse.isSuccess());

	}
}
