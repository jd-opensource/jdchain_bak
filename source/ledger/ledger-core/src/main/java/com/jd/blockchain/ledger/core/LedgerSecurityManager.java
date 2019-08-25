package com.jd.blockchain.ledger.core;

import java.util.Set;

import com.jd.blockchain.utils.Bytes;

public interface LedgerSecurityManager {

	String DEFAULT_ROLE = "_DEFAULT";

	SecurityPolicy getSecurityPolicy(Set<Bytes> endpoints, Set<Bytes> nodes);

}