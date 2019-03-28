package com.jd.blockchain.ledger.core;

import java.util.SortedSet;

public interface Privilege {
	
	SortedSet<Byte> getOpCodes();
	
	long getVersion();
	
}
