package com.jd.blockchain.ump.model.penetrate.store;

import com.jd.blockchain.ump.model.UmpConstant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 通过枚举的方式来实现内存队列;避免大并发时引起的不必要的多次实例化;
 * 
 * @author zhaoguangwei
 * 
 */
public enum MemQueue {
	instance;
	private BlockingQueue<String> queue = null;

	private MemQueue() {
		queue = new LinkedBlockingQueue<String>();
	}

	/**
	 * 记录放入内存队列;
	 * @param key
	 * @return
	 */
	public boolean put(String key) {
		boolean rtn = false;
		try {
			while (queue.size() >= UmpConstant.MEMORY_MAP_MAX_COUNT) {
				queue.remove();
			}
			queue.add(key);
			rtn =  true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	/**
	 * 从内存队列取出一条记录;
	 * 
	 * @return
	 */
	public String get() {
		String record = null;
		try {
			record = queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return record;
	}

	/**
	 * 内存队列清除;
	 */
	public void clear() {
		queue.clear();
	}

	/**
	 * 获得记录数;
	 * 
	 * @return
	 */
	public int size() {
		return queue.size();
	}
}
