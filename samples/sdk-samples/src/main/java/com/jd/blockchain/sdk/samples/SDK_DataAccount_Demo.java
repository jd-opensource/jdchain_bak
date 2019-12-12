package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.contract.TransferContract;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.transaction.LongValueHolder;
import com.jd.blockchain.utils.Bytes;

import static com.jd.blockchain.sdk.samples.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturnValue.decode;

public class SDK_DataAccount_Demo extends SDK_Base_Demo {

	public static void main(String[] args) {
		new SDK_DataAccount_Demo().executeDataAccount();
	}

	public void executeDataAccount() {

		// 定义交易模板
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		// 注册一个数据账户
		BlockchainKeypair dataAccount = createDataAccount();
		// 获取数据账户地址
		String dataAddress = dataAccount.getAddress().toBase58();
		// 打印数据账户地址
		System.out.printf("DataAccountAddress = %s \r\n", dataAddress);

		// 通过KV创建
		txTpl.dataAccount(dataAddress)
				.setText("zhangsan", "我的世界", -1)
				.setText("张三", "My World", -1);
		TransactionResponse txResp = commit(txTpl);

		System.out.println(txResp.isSuccess());

	}

	private BlockchainKeypair createDataAccount() {
		// 首先注册一个数据账户
		BlockchainKeypair newDataAccount = BlockchainKeyGenerator.getInstance().generate();

		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		txTpl.dataAccounts().register(newDataAccount.getIdentity());
		commit(txTpl);
		return newDataAccount;
	}
}
