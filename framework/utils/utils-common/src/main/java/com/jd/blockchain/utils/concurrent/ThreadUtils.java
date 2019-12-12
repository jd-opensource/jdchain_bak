package com.jd.blockchain.utils.concurrent;

public abstract class ThreadUtils {
	
	/**
	 * 以不可中断的方式使当前线程 sleep 指定时长；
	 * 
	 * @param millis 要等待的时长；单位毫秒；
	 */
	public static void sleepUninterrupted(long millis){
		long start = System.currentTimeMillis();
		long elapseTime = 0;
		while(elapseTime < millis){
			try {
				Thread.sleep(elapseTime);
			} catch (InterruptedException e) {
				// ignore ;
			}
			elapseTime = System.currentTimeMillis() - start;
		}
	}
	
}
