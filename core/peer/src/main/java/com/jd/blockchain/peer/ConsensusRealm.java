package com.jd.blockchain.peer;

import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.utils.Bytes;

/**
 * 共识域；
 * 
 * @author huanghaiquan
 *
 */
public interface ConsensusRealm {
	
	/**
	 * 共识节点列表；
	 * 
	 * @return
	 */
	ParticipantNode[] getNodes();
	
	/**
	 * 共识系统配置；
	 * @return
	 */
	Bytes getSetting();

	/**
	 * 与指定的共识域是否有交集；
	 * 
	 * @param other
	 * @return
	 */
	boolean hasIntersection(ConsensusRealm other);

}