package test.com.jd.blockchain.intgr.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.utils.ConsoleUtils;

public class TransactionCommitter {
	
	private PreparedTransaction[] ptxs;
	private int startIndex;
	private int count;
	
	public TransactionCommitter(PreparedTransaction[] ptxs, int startIndex, int count) {
		this.ptxs = ptxs;
		this.startIndex = startIndex;
		this.count = count;
	}
	
	public void start(CyclicBarrier barrier, CountDownLatch latch) {
		Thread thrd = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					barrier.await();
				} catch (Exception e) {
					System.out.println(" Barrier await error! --" + e.getMessage());
					e.printStackTrace();
				}
				ConsoleUtils.info("Start committing... [%s]", startIndex);
				try {
					for (int i = 0; i < count; i++) {
						ptxs[startIndex + i].commit();
					}
				} catch (Exception e) {
					System.out.println("Error occured on committing! --" + e.getMessage());
					e.printStackTrace();
				}
				latch.countDown();
			}
		});
		thrd.start();
	}
	
}
