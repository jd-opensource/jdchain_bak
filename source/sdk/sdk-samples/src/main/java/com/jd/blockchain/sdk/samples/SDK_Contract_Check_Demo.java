package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.contract.TransferContract;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.utils.Bytes;
import com.jd.chain.contracts.ContractTestInf;

import static com.jd.blockchain.sdk.samples.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturnValue.decode;

public class SDK_Contract_Check_Demo extends SDK_Base_Demo {

	public static void main(String[] args) {
		new SDK_Contract_Check_Demo().executeContract();
	}

	public void executeContract() {

		// 发布jar包
		// 定义交易模板
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		// 将jar包转换为二进制数据
		byte[] contractCode = readChainCodes("contract-jdchain.jar");

		// 生成一个合约账号
		BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();

		// 生成发布合约操作
		txTpl.contracts().deploy(contractDeployKey.getIdentity(), contractCode);

		// 生成预发布交易；
		PreparedTransaction ptx = txTpl.prepare();

		// 对交易进行签名
		ptx.sign(adminKey);

		// 提交并等待共识返回；
		TransactionResponse txResp = ptx.commit();

		// 获取合约地址
		Bytes contractAddress = contractDeployKey.getAddress();

		// 打印交易返回信息
		System.out.printf("Tx[%s] -> BlockHeight = %s, BlockHash = %s, isSuccess = %s, ExecutionState = %s \r\n",
				txResp.getContentHash().toBase58(), txResp.getBlockHeight(), txResp.getBlockHash().toBase58(),
				txResp.isSuccess(), txResp.getExecutionState());

		// 打印合约地址
		System.out.printf("ContractAddress = %s \r\n", contractAddress.toBase58());

		// 执行合约
		exeContract(contractAddress);
	}

	private void exeContract(Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		ContractTestInf contract = txTpl.contract(contractAddress, ContractTestInf.class);
		GenericValueHolder<String> result = decode(contract.randomChars(1024));
		commit(txTpl);
		String random = result.get();
		System.out.println(random);
	}


}
