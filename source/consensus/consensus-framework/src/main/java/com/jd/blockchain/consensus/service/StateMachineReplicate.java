package com.jd.blockchain.consensus.service;

import java.io.InputStream;
import java.util.Iterator;

/**
 * 状态机复制接口；
 * 
 * @author huanghaiquan
 *
 */
public interface StateMachineReplicate {

	/**
	 * 获取最新的状态编号；<br>
	 * 
	 * 注：新的状态编号总数比旧的状态编号大；
	 * 
	 * @return
	 */
	long getLatestStateID(String realmName);

	/**
	 * 返回指定状态编号的快照；
	 * 
	 * @param stateId
	 * @return
	 */
	StateSnapshot getSnapshot(String realmName, long stateId);

	/**
	 * 返回包含指定的起止状态编号在内的全部状态快照；
	 * 
	 * @param fromStateId
	 *            起始的状态编号（含）；
	 * @param toStateId
	 *            截止的状态编号（含）；
	 * @return
	 */
	Iterator<StateSnapshot> getSnapshots(String realmName, long fromStateId, long toStateId);

	/**
	 * 读状态数据；
	 * 
	 * @param stateId
	 * @return
	 */
	InputStream readState(String realmName, long stateId);

	/**
	 * 装载状态数据；
	 * 
	 * @param snapshot
	 * @param state
	 */
	void setupState(String realmName, StateSnapshot snapshot, InputStream state);

}
