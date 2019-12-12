package com.jd.blockchain.utils.concurrent;

/**
 * 在新线程执行调用的工具类；<br>
 * 
 * 注意：一个实例只能调用一次，并且不应在多线程间共享；
 * 
 * @author huanghaiquan
 *
 * @param <T> class
 */
public abstract class ThreadInvoker<T> {

	private volatile T retn;

	private volatile Exception error;

	private volatile boolean started = false;

	public Exception getError() {
		return error;
	}

	public T getRetn() {
		return retn;
	}

	protected abstract T invoke() throws Exception;

	/**
	 * Start invoke in new thread, and wait for the invoking thread to die;
	 * 
	 * @return t
	 */
	public T startAndWait() {
		Thread thrd = doStart();
		try {
			thrd.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		if (error != null) {
			throw new IllegalStateException(error.getMessage(), error);
		}
		return retn;
	}

	/**
	 * Start invoke in new thread;
	 * 
	 * @return AsyncCallback t;
	 */
	public AsyncCallback<T> start() {
		Thread thrd = doStart();
		return new AsyncCallback<T>(thrd, this);
	}

	public synchronized void reset() {
		if (started) {
			throw new IllegalStateException("Cann't reset when this invoking is running.");
		}
		retn = null;
		error = null;
	}

	private synchronized Thread doStart() {
		if (started) {
			throw new IllegalStateException("Invoker thread has started. Cann't start again until it's over.");
		}
		if (retn != null || error != null) {
			throw new IllegalStateException("Cann't start again until the result of last invoking is reseted.");
		}
		started = true;
		Thread thrd = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					retn = invoke();
				} catch (Exception e) {
					error = e;
				} finally {
					started = false;
				}
			}
		});
		thrd.start();
		return thrd;
	}

	public static class AsyncCallback<T> {
		private Thread thrd;

		private ThreadInvoker<T> invoker;

		public AsyncCallback(Thread thrd, ThreadInvoker<T> invoker) {
			this.thrd = thrd;
			this.invoker = invoker;
		}

		/**
		 * 等待调用返回；
		 * @return class t;
		 */
		public T waitReturn() {
			try {
				thrd.join();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
			if (invoker.error != null) {
				throw new IllegalStateException(invoker.error.getMessage(), invoker.error);
			}
			return invoker.retn;
		}
	}
}