package com.jd.blockchain.utils.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可对线程命名的线程工厂；
 * 
 * @author haiq
 *
 */
public class NamedThreadFactory implements ThreadFactory {

	private String name;

	private boolean indexThread = false;
	
	private AtomicLong index;

	private boolean deamon;
	
	/**
	 * 创建 NamedThreadFactory 实例；
	 * 
	 * @param name
	 *            名称；
	 */
	public NamedThreadFactory(String name) {
		this(name, false, false);
	}

	/**
	 * 创建 NamedThreadFactory 实例；
	 * 
	 * @param name
	 *            名称；
	 * @param deamon
	 *            是否守护线程；
	 */
	public NamedThreadFactory(String name, boolean deamon) {
		this(name, false, deamon);
	}

	/**
	 * 创建 NamedThreadFactory 实例；
	 * 
	 * @param name
	 *            名称；
	 * @param indexThread
	 *            是否记录创建的线程个数；如果是，则通过在指定的线程名称前加上索引值构成最终的线程名称，诸如："threadName-0，threadName-1"；
	 * @param deamon
	 *            是否守护线程；
	 */
	public NamedThreadFactory(String name, boolean indexThread, boolean deamon) {
		this.name = name;
		this.indexThread = indexThread;
		if (indexThread) {
			index = new AtomicLong(0);
		}
		this.deamon = deamon;
	}

	@Override
	public Thread newThread(Runnable r) {
		String thrdName = name;
		if (indexThread) {
			long i = index.incrementAndGet();
			thrdName = name +"-" + i;
		}
		Thread thrd = new Thread(r, thrdName);
		thrd.setDaemon(deamon);
		return thrd;
	}

}
