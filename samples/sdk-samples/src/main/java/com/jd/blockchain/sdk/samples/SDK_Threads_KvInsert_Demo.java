package com.jd.blockchain.sdk.samples;

import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.codec.Base58Utils;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SDK_Threads_KvInsert_Demo extends SDK_Base_Demo {

	public static void main(String[] args) throws Exception {
		new SDK_Threads_KvInsert_Demo().executeThreadsInsert();
	}

	public void executeThreadsInsert() throws Exception {

		final int MAX = 30;

		final String dataAddress = "LdeNqP4S88t1YjkGQaCGbX95ygD6hA2B6yjp6";

		ExecutorService threadPool = Executors.newFixedThreadPool(50);

		final CountDownLatch latch = new CountDownLatch(MAX);

		for (int i = 0; i < MAX; i++) {

			threadPool.execute(() -> {
				TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

				String key = System.currentTimeMillis() + "-" +
						System.nanoTime() + "-" +
						new Random(Thread.currentThread().getId()).nextInt(1024);

				txTemp.dataAccount(dataAddress).setText(key,"value1",-1);

				// TX 准备就绪
				PreparedTransaction prepTx = txTemp.prepare();
				prepTx.sign(adminKey);

				// 提交交易；
				TransactionResponse response = prepTx.commit();

				System.out.printf("Key = %s, Result = %s \r\n", key, response.isSuccess());

				latch.countDown();

			});
		}

		latch.await();
		System.out.println("It is Over !!!");
		System.exit(0);
	}
}
