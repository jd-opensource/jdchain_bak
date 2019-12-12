package com.jd.blockchain.ledger.core;

import java.util.Set;

import com.jd.blockchain.utils.Bytes;

public interface LedgerSecurityManager {

	String DEFAULT_ROLE = "DEFAULT";

	/**
	 * 创建一项与指定的终端用户和节点参与方相关的安全策略；
	 * 
	 * @param endpoints 终端用户的地址列表；
	 * @param nodes     节点参与方的地址列表；
	 * @return 一项安全策略；
	 */
	SecurityPolicy createSecurityPolicy(Set<Bytes> endpoints, Set<Bytes> nodes);

}